package kr.goodit.assignment.member.repository;

import kr.goodit.assignment.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
