package joonggo.baechoo.repository;

import joonggo.baechoo.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByMemberId(Long memberId);

    @Query("SELECT i from Item i JOIN FETCH i.member")
    List<Item> findAllWithMember();

    List<Item> findTop5ByOrderByCreatedDateDesc();
}
