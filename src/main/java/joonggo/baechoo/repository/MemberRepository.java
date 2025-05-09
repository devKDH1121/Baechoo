package joonggo.baechoo.repository;

import joonggo.baechoo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    List<Member> findAllByDel(int del);
    Optional<Member> findByIdAndDel(Long id, int del);



}
