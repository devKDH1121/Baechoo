package joonggo.baechoo.repository;

import joonggo.baechoo.domain.ChatMessage;
import joonggo.baechoo.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderBySendTimeAsc(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoom_RoomIdOrderBySendTimeAsc(String roomId); // 추가

}
