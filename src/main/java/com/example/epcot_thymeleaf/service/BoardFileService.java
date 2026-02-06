package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.entity.BoardFileEntity;
import com.example.epcot_thymeleaf.repository.BoardFileRepository;
import lombok.RequiredArgsConstructor;
import org.osgi.resource.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardFileService {

  private final BoardFileRepository boardFileRepository;
//  private final Path uploadDir = Paths.get("upload");
//
  @Value("${org.zerock.upload.path}")
  private String uploadDir;

  public void uploadFiles(Long boardId, List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return;
    }

    try {
      Path dir = Paths.get(uploadDir, "board", String.valueOf(boardId));
      Files.createDirectories(dir);

      for (MultipartFile file : files) {
        if (file == null || file.isEmpty()) {
          continue;
        }

        String original = file.getOriginalFilename();
        String stored = UUID.randomUUID() + "_" + (original == null ? "file" : original);

        Path target = dir.resolve(stored);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        BoardFileEntity meta = new BoardFileEntity();
        meta.setBoardId(boardId);
        meta.setOriginalName(original);
        meta.setStoredName(stored);
        meta.setStoredPath(target.toString());
        meta.setContentType(file.getContentType());
        meta.setSize(file.getSize());

        boardFileRepository.save(meta);
      }

    } catch (IOException e) {
      throw new RuntimeException("파일 업로드 실패", e);
    }
  }

  public List<BoardFileEntity> getFiles(Long boardId) {
    return boardFileRepository.findAllByBoardIdOrderByIdDesc(boardId);
  }

  public ResponseEntity<UrlResource> download(Long fileId) {

    BoardFileEntity meta = boardFileRepository.findById(fileId)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다"));

    try {
      Path path = Paths.get(meta.getStoredPath());
      UrlResource resource = new UrlResource(path.toUri());

      if (!resource.exists()) {
        return ResponseEntity.notFound().build();
      }

      ContentDisposition contentDisposition = ContentDisposition.attachment()
          .filename(meta.getOriginalName(), StandardCharsets.UTF_8)
          .build();

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);

    } catch (MalformedURLException e) {
      throw new RuntimeException("다운로드 처리 실패", e);
    }
  }

  public ResponseEntity<UrlResource> preview(Long fileId) throws MalformedURLException {
    BoardFileEntity file = boardFileRepository.findById(fileId).orElseThrow();

    if (!isImage(file)) {
      return ResponseEntity.notFound().build();
    }

    Path path = Paths.get(uploadDir, "board").resolve(file.getStoredName());
    UrlResource resource = new UrlResource( "file : " + path);
//    UrlResource resource = new UrlResource("file: " + uploadDir.resolveConstantDesc());

    System.out.println("resource.getFilename() : " + resource.getFilename());
    System.out.println("file: " + file);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(file.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
        .body(resource);
  }

  public boolean isImage(BoardFileEntity file) {
    return file.getContentType() != null && file.getContentType().startsWith("image");
  }
}
