package com._depth.jpa.board;

import com._depth.jpa.Comment.QComment;
import com._depth.jpa.member.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/*@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardResponseDto> searchBoards(String title, Pageable pageable) {

        QBoard b = QBoard.board;
        QMember m = QMember.member;
        QComment c = QComment.comment;

        QComment c1 = new QComment("c1"); // 댓글 수 서브쿼리용
        QBoard b2 = new QBoard("b2");     // 작성자 게시글 수 서브쿼리용

        // 1️⃣ Board + Member + Comment 조회 (fetch join)
        List<Board> boards = queryFactory
                .selectFrom(b)
                .join(b.member, m).fetchJoin() // Board → Member
                .leftJoin(b.comments, c).fetchJoin() // Board → Comment
                .where(title != null && !title.isEmpty() ? b.title.contains(title) : null)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct() // fetch join 중복 제거
                .orderBy(b.id.desc())
                .fetch();

        // 2️⃣ DTO 변환 (댓글 수, 작성자 게시글 수 포함)
        List<BoardResponseDto> content = boards.stream()
                .map(board -> {
                    Long commentCount = queryFactory
                            .select(c1.count())
                            .from(c1)
                            .where(c1.board.eq(board))
                            .fetchOne();

                    Long writerBoardCount = queryFactory
                            .select(b2.count())
                            .from(b2)
                            .where(b2.member.eq(board.getMember()))
                            .fetchOne();

                    List<CommentDto> comments = board.getComments().stream()
                            .map(cmt -> new CommentDto(
                                    cmt.getCMT_ID(),
                                    cmt.getCMT_NAME(),
                                    cmt.getCMT_DESCRIPTION()
                            ))
                            .collect(Collectors.toList());

                    return new BoardResponseDto(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getMember().getUSR_ID(),
                            board.getMember().getUSR_NM(),
                            commentCount,
                            writerBoardCount,
                            comments
                    );
                })
                .collect(Collectors.toList());

        // 3️⃣ 총 게시글 수 (페이징용)
        long total = queryFactory
                .selectFrom(b)
                .where(title != null && !title.isEmpty() ? b.title.contains(title) : null)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}*/

