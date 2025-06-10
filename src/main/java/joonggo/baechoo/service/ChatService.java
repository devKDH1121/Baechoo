package joonggo.baechoo.service;

import joonggo.baechoo.domain.ChatMessage;
import joonggo.baechoo.domain.ChatRoom;
import joonggo.baechoo.domain.Item;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.repository.ChatMessageRepository;
import joonggo.baechoo.repository.ChatRoomRepository;
import joonggo.baechoo.repository.ItemRepository;
import joonggo.baechoo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository; // ItemRepository 의존성 주입

    /**
     * [API용] 채팅방 생성 - item 정보 없이 sender와 receiver로만 생성
     */
    @Transactional
    public ChatRoom createChatRoom(Member sender, Member receiver) {
        // 이 메서드는 item 정보 없이 채팅방을 생성합니다. (기존 로직 유지)
        Optional<ChatRoom> existingRoom = findExistingChatRoom(sender, receiver, null);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }
        ChatRoom newChatRoom = ChatRoom.create(sender, receiver, null); // item은 null로 전달
        return chatRoomRepository.save(newChatRoom);
    }

    /**
     * [주요 기능] 상품 상세 페이지에서 채팅방 생성 - item 정보 포함
     */
    @Transactional
    public ChatRoom createChatRoom(String receiverId, Long itemId, Authentication authentication) {
        Member sender = findSenderByAuthentication(authentication);

        Member receiver = memberRepository.findByUserId(receiverId)
                .orElseThrow(() -> new IllegalStateException("상대방을 찾을 수 없습니다: " + receiverId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("상품 정보를 찾을 수 없습니다."));

        // 동일 인물, 동일 상품에 대한 채팅방이 있는지 확인
        Optional<ChatRoom> existingRoom = findExistingChatRoom(sender, receiver, item);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // 새 채팅방 생성
        ChatRoom newChatRoom = ChatRoom.create(sender, receiver, item);
        return chatRoomRepository.save(newChatRoom);
    }

    // 현재 로그인한 사용자(sender) 정보를 찾는 헬퍼 메서드
    private Member findSenderByAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String providerId = String.valueOf(oauthToken.getPrincipal().getAttributes().get("id"));
            return memberRepository.findByProviderId(providerId)
                    .orElseThrow(() -> new IllegalStateException("카카오 로그인 사용자 정보를 찾을 수 없습니다."));
        } else {
            String userId = authentication.getName();
            return memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("일반 로그인 사용자 정보를 찾을 수 없습니다."));
        }
    }

    // 기존 채팅방을 찾는 헬퍼 메서드 (item 정보 포함)
    public Optional<ChatRoom> findExistingChatRoom(Member user1, Member user2, Item item) {
        return chatRoomRepository.findBySenderAndReceiverAndItem(user1, user2, item)
                .or(() -> chatRoomRepository.findBySenderAndReceiverAndItem(user2, user1, item));
    }

    // 특정 사용자의 모든 채팅방 목록 조회
    public List<ChatRoom> getMyChatRooms(Member member) {
        return chatRoomRepository.findAllByMemberWithUsers(member);
    }

    // roomId로 특정 채팅방 조회
    public ChatRoom getChatRoom(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
    }

    // 특정 채팅방의 모든 메시지 조회
    public List<ChatMessage> getChatMessages(String roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        return chatMessageRepository.findByChatRoomOrderBySendTimeAsc(chatRoom);
    }

    // 메시지 저장
    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
}