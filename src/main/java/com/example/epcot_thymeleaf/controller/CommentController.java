package com.example.epcot_thymeleaf.controller;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.service.BoardService;
import com.example.epcot_thymeleaf.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final BoardService boardService;

  @PostMapping("/write")
  public String write(@RequestParam Long postId, @RequestParam String content, @RequestParam(required = false) Long parentId, Principal principal){
    commentService.saveComment(postId, principal.getName(), content, parentId);

    return "redirect:/board/detail/" + postId;
  }

  // 댓글 수정
  @PostMapping("/edit/{id}")
  public String editComment(
      @PathVariable Long id,
      @RequestParam String content,
      Principal principal
    ) throws IOException {

    Long postId = commentService.updateComment(id, content, principal.getName());

    return "redirect:/board/detail/" + postId;
  }

  // 댓글 삭제
  @PostMapping("/delete/{id}")
  public String deleteComment(@PathVariable Long id, Principal principal) {
    Long postId = commentService.deleteComment(id, principal.getName());

    return "redirect:/board/detail/" + postId;
  }
}
