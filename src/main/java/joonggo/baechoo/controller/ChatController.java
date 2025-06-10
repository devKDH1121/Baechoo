package joonggo.baechoo.controller;

import joonggo.baechoo.domain.ChatMessage;
import joonggo.baechoo.domain.ChatRoom;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.dto.ChatMessageDto;
import joonggo.baechoo.dto.ChatRoomDto;
import joonggo.baechoo.repository.MemberRepository;
import joonggo.baechoo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final MemberRepository memberRepository;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("userId") String userId) {
        log.debug("Received message from userId: {}", userId);

        try {
            ChatRoom chatRoom = chatService.getChatRoom(message.getRoomId());
            Member sender = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(sender)
                    .message(message.getMessage())
                    .build();

            // 메시지를 DB에 저장
            ChatMessage savedMessage = chatService.saveMessage(chatMessage);

            ChatMessageDto responseDto = ChatMessageDto.from(savedMessage);
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), responseDto);

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생", e);
        }
    }




    @GetMapping("/chat/rooms")
    @ResponseBody
    public List<ChatRoomDto> getRooms(@AuthenticationPrincipal Member member){
        return chatService.getMyChatRooms(member).stream()
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());
    }

    // API 용 엔드포인트
    @PostMapping("/api/chat/room")
    @ResponseBody
    public ChatRoomDto createChatRoomApi(@AuthenticationPrincipal Member sender,
                                         @RequestParam String receiverId) {
        Member receiver = memberRepository.findByUserId(receiverId)
                .orElseThrow(() -> new RuntimeException("상대방을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatService.createChatRoom(sender, receiver);
        return ChatRoomDto.from(chatRoom);
    }




    @GetMapping("/chat/room/{roomId}")
    public String chatRoom(@PathVariable String roomId, Model model, Authentication authentication){
        if (authentication == null) return "redirect:/login"; // 로그인 안했으면 로그인 페이지로

        ChatRoom chatRoom = chatService.getChatRoom(roomId);
        List<ChatMessage> messages = chatService.getChatMessages(roomId);

        // [추가] 채팅방에 연결된 Item 정보를 모델에 추가
        model.addAttribute("item", chatRoom.getItem());

        model.addAttribute("roomId", roomId);
        model.addAttribute("messages", messages);
        // [추가] 현재 로그인한 사용자 정보도 모델에 추가 (메시지 sent/received 구분용)
        model.addAttribute("currentUser", memberRepository.findByUserId(authentication.getName()).orElse(null));

        return "chat/room";
    }

    @GetMapping("/chat")
    public String chatList(Model model, Authentication authentication) {
        log.info("chatList called with authentication: {}", authentication);

        if (authentication == null) {
            return "redirect:/login";
        }

        Member member;
        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                // 카카오 로그인의 경우
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String providerId = String.valueOf(oauthToken.getPrincipal().getAttributes().get("id"));
                member = memberRepository.findByProviderId(providerId)
                        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
            } else {
                // 일반 로그인의 경우
                String userId = authentication.getName();
                member = memberRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
            }

            List<ChatRoom> chatRooms = chatService.getMyChatRooms(member);
            model.addAttribute("chatRooms", chatRooms);
            model.addAttribute("currentUser", member);

            return "chat/list";
        } catch (Exception e) {
            log.error("채팅 목록 조회 중 오류 발생: ", e);
            return "redirect:/error";
        }
    }

    @PostMapping("/chat/room")
    public String createRoom(Authentication authentication,
                             @RequestParam String receiverId,
                             @RequestParam Long itemId) { // [추가] itemId 파라미터 받기
        log.info("createRoom 호출됨. receiverId: {}, itemId: {}", receiverId, itemId);

        if (authentication == null) {
            log.warn("인증되지 않은 사용자의 채팅방 생성 시도");
            return "redirect:/login";
        }

        try {
            // [수정] chatService 호출 시 itemId도 함께 전달
            ChatRoom chatRoom = chatService.createChatRoom(receiverId, itemId, authentication);
            return "redirect:/chat/room/" + chatRoom.getRoomId();
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage(), e);
            return "redirect:/error";
        }
    }
    }








