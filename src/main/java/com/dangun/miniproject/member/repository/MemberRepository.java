package com.dangun.miniproject.member.repository;

import com.dangun.miniproject.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String email);

    Member findByEmail(String email);

    Boolean existsByNickname(String nickname);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.member.id = :memberId")
    void deleteCommentsByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM Board b WHERE b.member.id = :memberId")
    void deleteBoardsByMemberId(@Param("memberId") Long memberId);

}
