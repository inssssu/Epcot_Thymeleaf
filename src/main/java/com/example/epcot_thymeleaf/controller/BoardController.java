package com.example.epcot_thymeleaf.controller;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.BoardFileEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.BoardFileRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import com.example.epcot_thymeleaf.service.BoardFileService;
import com.example.epcot_thymeleaf.service.BoardService;
import com.example.epcot_thymeleaf.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;
  private final UserService userService;
  private final UserRepository userRepository;
  private final BoardFileService boardFileService;
  private final BoardFileRepository boardFileRepository;

  @GetMapping("/list")
  public String boardListPage(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model, Authentication user) {
    if (user != null) {
      model.addAttribute("loginId", user.getName());
      System.out.println("Username is not null");
    }

//    List<BoardEntity> boardList = boardService.getBoardList   ();

    Page<BoardEntity> boardPage = boardService.getBoardPage(pageable);
    model.addAttribute("boardPage", boardPage);
    model.addAttribute("boardList", boardPage.getContent());
    model.addAttribute("page", boardPage.getNumber());

    return "/board/board-list";
  }

  @GetMapping("/write")
  public String boardWritePage(BoardEntity board, Model model) {
    model.addAttribute("item", board);

    return "board/board-write";
  }

  @PostMapping("/write")
  public String boardWrite(
      @ModelAttribute("item") BoardEntity form,
      @RequestParam(value = "files", required = false) List<MultipartFile> files,
      @AuthenticationPrincipal UserDetails userDetails,
      Model model
  ) {

//    Long loginUserId = (Long) session.getAttribute("loginUserId");
//    if (loginUserId == null) {
//      return "redirect:/login";
//    }
    //dev

    if (form.getTitle() == null || form.getTitle().trim().isBlank()) {
      model.addAttribute("errorMsg", "제목을 작성해 주세요");
      model.addAttribute("title", form.getTitle());
      model.addAttribute("content", form.getContent());

      return "board/board-write";
    }

    if (form.getContent() == null || form.getContent().trim().isBlank()) {
      model.addAttribute("errorMsg", "내용을 입력해 주세요");
      model.addAttribute("title", form.getTitle());
      model.addAttribute("content", form.getContent());

      return "board/board-write";
    }

    UserEntity loginUser = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보를 찾을 수 없습니다"));

    BoardEntity board = BoardEntity.builder()
        .author(loginUser)
        .title(form.getTitle())
        .content(form.getContent())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    boardService.write(board);
    System.out.println("board write");
    boardFileService.uploadFiles(board.getId(), files);

    return "redirect:/board/detail/" + board.getId();
  }

  @GetMapping("/detail/{id}")
  public String boardDetailPage(
      @PathVariable Long id,
      @PageableDefault Pageable pageable,
      Authentication user,
      Model model
    ) {
    if (user != null) {
      model.addAttribute("loginId", user);
    }

    BoardEntity board = boardService.getBoardItem(id);
    Page<BoardEntity> boardPage = boardService.getBoardPage(pageable);

    model.addAttribute("item", board);
    model.addAttribute("attachedFiles", boardFileService.getFiles(id));

    model.addAttribute("page", boardPage.getNumber());

//    boardFileService.preview();

    return "board/board-detail";
  }

  @GetMapping("/board/files/{fileId}/view")
  @ResponseBody
  public ResponseEntity<Resource> viewImage(@PathVariable Long fileId) {
    // 1. DB에서 파일 정보 가져오기
    BoardFileEntity meta = boardFileRepository.findById(fileId)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다"));

    // 2. 파일 리소스 생성
    Resource resource = new FileSystemResource(meta.getStoredPath());

    // 3. 응답 생성
    return ResponseEntity.ok()
        // 브라우저가 이미지로 인식하도록 Content-Type 설정 (예: image/jpeg)
        .header(HttpHeaders.CONTENT_TYPE, meta.getContentType())
        .body(resource);
  }

  @PostMapping("/detail/{id}/delete")
  public String boardDelete(@PathVariable Long id) {
    boardService.delete(id);

    return "redirect:/board/list";
  }

  @GetMapping("/edit/{id}")
  public String boardEditPage(
      @PathVariable Long id,
      HttpSession httpSession,
      Model model
    ) throws MalformedURLException {
    BoardEntity board = boardService.getBoardItem(id);

    model.addAttribute("item", board);
    model.addAttribute("attachedFiles", boardFileService.getFiles(id));

    return "board/board-edit";
  }

  @PostMapping("/edit/{id}")
  public String boardEdit(
      @PathVariable Long id,
      @ModelAttribute("item") BoardEntity form,
      @RequestParam List<MultipartFile> files,
      @RequestParam(required = false) List<Long> deleteFileIds,
      HttpSession httpSession) throws IOException {

    form.setId(id);

    BoardEntity board = boardService.getBoardItem(id);

    board.setTitle(form.getTitle());
    board.setContent(form.getContent());
    board.setUpdatedAt(LocalDateTime.now());
    boardService.write(board);
    boardFileService.uploadFiles(id, files);
    boardFileService.fileDelete(deleteFileIds);

    return "redirect:/board/detail/" + id;
  }

  @GetMapping("/files/{fileId}/download")
  public ResponseEntity<UrlResource> fileDownload(@PathVariable Long fileId) {

    return boardFileService.download(fileId);
  }

  @GetMapping("/files/{fileId}/view")
  public ResponseEntity<UrlResource> filePreview(@PathVariable Long fileId) throws MalformedURLException {

    return boardFileService.view(fileId);
  }
}
