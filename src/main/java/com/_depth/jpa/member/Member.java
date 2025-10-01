package com._depth.jpa.member;


import com._depth.jpa.board.Board;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer USR_ID;
    public String USR_NM;
    public String USR_EMAIL;
    @Column(name = "USR_PWD")
    public String usrPwd;

/*
    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();*/
}
