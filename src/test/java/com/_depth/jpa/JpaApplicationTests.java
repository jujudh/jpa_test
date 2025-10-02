package com._depth.jpa;

import com._depth.jpa.board.Board;
import com._depth.jpa.board.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Transactional
class JpaApplicationTests {
    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testLoading() {
        Board board = boardRepository.findById(1L).orElseThrow();

        System.out.println("== Board 조회 완료 ==");

        // Member 접근 전
        System.out.println("== Member 아직 접근 안 함 ==");

        // Member 이름 접근
/*        System.out.println("작성자 이름: " + board.getMember().getUSR_NM());*/

        UUID uuid = UUID.randomUUID();
        System.out.println("uuid : " + uuid);

        String datePath = LocalDate.now().toString();
        System.out.println("datePath : " + datePath);
        System.out.println("datePath2 : " + LocalDate.now());
    }

}
