package com._depth.jpa;

import com._depth.jpa.board.Board;
import com._depth.jpa.member.Member;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BoardLike {

    @EmbeddedId
    private BoardLikeId id;

    @ManyToOne
    @MapsId("boardId")
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "USR_ID")
    private Member member;

    private String likeType;
}