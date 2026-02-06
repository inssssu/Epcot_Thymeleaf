package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFileEntity, Long> {

  List<BoardFileEntity> findAllByBoardIdOrderByIdDesc(Long boardId);

  /* file list 조회
  *   boardId - 게시글 조회 번호
  *   @return - file list
  * */
//  List<BoardFileResponse> findAllByBoardId(Long boardId);

  /* file list 조회
   *   @param ids - PK list
   *   @return file - list
   * */
//  List<BoardFileResponse> findByAllIds(List<Long> ids);

  /* file 삭제
  *   @param ids - PK list
  * */
//  void deleteAllByIds(List<Long> ids);
}
