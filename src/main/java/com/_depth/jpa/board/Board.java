package com._depth.jpa.board;


import com._depth.jpa.Comment.Comment;
import com._depth.jpa.file.FileInfo;
import com._depth.jpa.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String title;

    public String content;
    @Column(name = "img_file")
    public String img_file;
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USR_ID")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();
    */

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private FileInfo file;

}
