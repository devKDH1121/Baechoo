package joonggo.baechoo.repository;

import joonggo.baechoo.domain.ChatRoom;
import joonggo.baechoo.domain.Item;
import joonggo.baechoo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "LEFT JOIN FETCH cr.sender s " +
            "LEFT JOIN FETCH cr.receiver r " +
            "LEFT JOIN FETCH cr.messages m " +
            "WHERE cr.sender.id = :#{#member.id} OR cr.receiver.id = :#{#member.id} " +
            "ORDER BY cr.id DESC")


    List<ChatRoom> findAllByMemberWithUsers(Member member);

    Optional<ChatRoom> findByRoomId(String roomId);

    List<ChatRoom> findBySender(Member sender);
    List<ChatRoom> findByReceiver(Member receiver);

    Optional<ChatRoom> findBySenderAndReceiverAndItem(Member sender, Member receiver, Item item);

}