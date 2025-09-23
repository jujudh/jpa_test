package com._depth.jpa.board;


import com._depth.jpa.Comment.Comment;
import com._depth.jpa.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR"
        ,sequenceName = "BOARD_SEQ"
        ,initialValue = 1,allocationSize=1
)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")
    public Long id;

    public String title;

    public String content;

    @ManyToOne
    @JoinColumn(name = "USR_ID")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

}
