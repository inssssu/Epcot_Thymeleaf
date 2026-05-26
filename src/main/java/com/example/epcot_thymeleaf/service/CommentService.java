package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.CommentEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.BoardRepository;
import com.example.epcot_thymeleaf.repository.CommentRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;
  private final UserRepository userRepository;

  public void saveComment(Long postId, String username, String content, Long parentId) {

    BoardEntity board = boardRepository.findById(postId).orElseThrow();
    UserEntity user = userRepository.findByUsername(username).orElseThrow();

    CommentEntity.CommentEntityBuilder builder = CommentEntity.builder()
        .post(board)
        .user(user)
        .content(content);

    if (parentId != null) {
      CommentEntity parent = commentRepository.findById(parentId).orElseThrow();
      builder.parent(parent);
    }

    commentRepository.save(builder.build());

  }

  public List<CommentEntity> findCommentsByPostId(Long postId) {
    return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);
  }

  public Long updateComment(Long id, String content, String username) {

    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("댓글은 공백일 수 없습니다");
    }

    CommentEntity comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다"));

    if (!comment.getUser().getUsername().equals(username)) {
      throw new RuntimeException("본인이 작성한 댓글만 수정할 수 있습니다");
    }

    comment.setContent(content);
    comment.setUpdatedAt(LocalDateTime.now());

    return comment.getPost().getId();
  }

  public Long deleteComment(Long id, String username) {

    CommentEntity comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다"));

    if (!comment.getUser().getUsername().equals(username)) {
      throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다");
    }

    Long postId = comment.getPost().getId();
    commentRepository.delete(comment);

    return postId;
  }
}
