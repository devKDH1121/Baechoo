package joonggo.baechoo.dto;

import joonggo.baechoo.domain.ChatMessage;
import joonggo.baechoo.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String roomId;
    private String userId;
    private String message;
    private LocalDateTime sendTime;

    public static ChatMessageDto from(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .roomId(chatMessage.getChatRoom().getRoomId())
                .userId(chatMessage.getSender().getUserId())
                .message(chatMessage.getMessage())
                .sendTime(chatMessage.getSendTime())
                .build();
    }



}

