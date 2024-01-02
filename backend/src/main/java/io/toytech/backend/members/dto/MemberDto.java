package io.toytech.backend.members.dto;

import io.toytech.backend.board.dto.BoardDto;
import io.toytech.backend.comment.dto.CommentDto;
import io.toytech.backend.members.domain.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {

  private Long id;
  private String name;

  private List<BoardDto> boards = new ArrayList<>();

  private List<CommentDto> comments;


  public MemberDto(Member member) {
    this.id = member.getId();
    this.name = member.getName();
    this.boards = member.getBoards().stream()
        .map(c -> new BoardDto(c)).collect(Collectors.toList());
    this.comments = member.getComments().stream()
        .map(c -> new CommentDto(c)).collect(Collectors.toList());
  }
}
