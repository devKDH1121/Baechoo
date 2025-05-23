package joonggo.baechoo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();

    public static ChatRoom create(Member sender, Member receiver){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.sender = sender;
        chatRoom.receiver = receiver;
        chatRoom.roomId = UUID.randomUUID().toString();

        return chatRoom;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(roomId, chatRoom.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @PrePersist
    public void generateRoomId() {
        if (this.roomId == null) {
            this.roomId = UUID.randomUUID().toString();
        }
    }


}
