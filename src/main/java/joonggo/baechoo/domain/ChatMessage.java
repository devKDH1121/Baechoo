package joonggo.baechoo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    private String message;
    private LocalDateTime sendTime;

    @Column(name = "is_read")
    private boolean isRead;

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, String message){
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.sendTime = LocalDateTime.now();
        this.isRead = false;
    }
}

