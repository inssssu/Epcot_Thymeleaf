package com.example.epcot_thymeleaf.controller;

import com.example.epcot_thymeleaf.dto.response.BoardResponseDTO;
import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.UserRepository;
import com.example.epcot_thymeleaf.service.BoardService;
import com.example.epcot_thymeleaf.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;
  private final UserService userService;
  private final UserRepository userRepository;

  @GetMapping("/list")
  public String boardListPage(Model model) {
    List<BoardEntity> boardList = boardService.getBoardList();

    model.addAttribute("boardList", boardList);

    return "/board/board-list";
  }

  @GetMapping("/write")
  public String boardWritePage(Model model) {
    if (!model.containsAttribute("title")) model.addAttribute("title", "");
    if (!model.containsAttribute("content")) model.addAttribute("content", "");

    return "/board/board-write";
  }

  @PostMapping("/write")
  public String boardWrite(
      @RequestParam String title,
      @RequestParam String content,
      @AuthenticationPrincipal UserDetails userDetails,
      Model model
  ) {


//    Long loginUserId = (Long) session.getAttribute("loginUserId");
//    if (loginUserId == null) {
//      return "redirect:/login";
//    }
    //dev

    if (title == null || title.trim().isBlank()) {
      model.addAttribute("errorMsg", "제목을 작성해 주세요");
      model.addAttribute("title", title);
      model.addAttribute("content", content);

      return "board/board-write";
    }

    if (content == null || content.trim().isBlank()) {
      model.addAttribute("errorMsg", "내용을 입력해 주세요");
      model.addAttribute("title", title);
      model.addAttribute("content", content);

      return "board/board-write";
    }

    UserEntity loginUser = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보를 찾을 수 없습니다"));

    BoardEntity board = BoardEntity.builder()
        .author(loginUser)
        .title(title)
        .content(content)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    boardService.write(board);
    System.out.println("board write");

    return "redirect:/board/list";
  }

  @GetMapping("/detail/{id}")
  public String boardDetailPage(@PathVariable String id) {


    return "board/board-detail";
  }
}
