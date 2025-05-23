package joonggo.baechoo.dto;

import joonggo.baechoo.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {

    private String roomId;
    private String otherUserName;

    public static ChatRoomDto from(ChatRoom chatRoom){
        return new ChatRoomDto(
                chatRoom.getRoomId(),
                chatRoom.getReceiver().getName()
        );
    }
}
