package joonggo.baechoo.service;

import joonggo.baechoo.domain.ChatMessage;
import joonggo.baechoo.domain.ChatRoom;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.repository.ChatMessageRepository;
import joonggo.baechoo.repository.ChatRoomRepository;
import joonggo.baechoo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 기존 Member 객체로 생성하는 메소드
    @Transactional
    public ChatRoom createChatRoom(Member sender, Member receiver) {
        if (sender == null) {
            throw new IllegalStateException("로그인이 필요한 서비스입니다.");
        }

        // 기존 채팅방 확인
        Optional<ChatRoom> existingRoom = findExistingChatRoom(sender, receiver);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // 새 채팅방 생성
        ChatRoom chatRoom = ChatRoom.create(sender, receiver);
        return chatRoomRepository.save(chatRoom);
    }
    // Authentication 객체로 생성하는 메소드
    @Transactional
    public ChatRoom createChatRoom(String receiverId, Authentication authentication) {
        Member sender;

        if (authentication instanceof OAuth2AuthenticationToken) {
            // 카카오 로그인의 경우
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            String nickname = (String) attributes.get("nickname");

            log.info("카카오 로그인 사용자 nickname: {}, providerID: {}", nickname, attributes.get("id"));

            // provider_id로 찾아보기
            sender = memberRepository.findByProviderId(String.valueOf(attributes.get("id")))
                    .orElseThrow(() -> new IllegalStateException("카카오 로그인 사용자 정보를 찾을 수 없습니다. providerId: " + attributes.get("id")));
        } else {
            String userId = authentication.getName();
            sender = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("일반 로그인 사용자 정보를 찾을 수 없습니다."));
        }

        // receiver 찾기
        Member receiver = memberRepository.findByUserId(receiverId)
                .orElseGet(() -> memberRepository.findByName(receiverId)
                        .orElseThrow(() -> new IllegalStateException("상대방을 찾을 수 없습니다: " + receiverId)));

        log.info("채팅방 생성 - 발신자: {}, 수신자: {}", sender.getName(), receiver.getName());
        return createChatRoom(sender, receiver);
    }

    public Optional<ChatRoom> findExistingChatRoom(Member user1, Member user2) {
        return user1.getSendChatRooms().stream()
                .filter(room -> room.getReceiver().equals(user2))
                .findFirst()
                .or(() -> user1.getReceiveChatRooms().stream()
                        .filter(room -> room.getSender().equals(user2))
                        .findFirst());
    }

    public List<ChatMessage> getChatMessages(String roomId) {  // 메소드 이름 수정
        ChatRoom chatRoom = getChatRoom(roomId);
        return chatMessageRepository.findByChatRoomOrderBySendTimeAsc(chatRoom);
    }

    public ChatRoom getChatRoom(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(Member member) {
        log.info("Getting chat rooms for member: {}", member.getUserId());
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberWithUsers(member);
        log.info("Found {} chat rooms for member {}", chatRooms.size(), member.getUserId());
        return chatRooms;
    }

    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        log.info("Saving chat message from user: {} in room: {}",
                message.getSender().getUserId(),
                message.getChatRoom().getRoomId());
        return chatMessageRepository.save(message);
    }



}