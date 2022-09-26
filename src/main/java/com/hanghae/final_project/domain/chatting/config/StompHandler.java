package com.hanghae.final_project.domain.chatting.config;


import com.hanghae.final_project.domain.chatting.dto.request.ChatMessageSaveDto;
import com.hanghae.final_project.domain.chatting.dto.request.MessageDto;
import com.hanghae.final_project.domain.chatting.redis.RedisPublisher;
import com.hanghae.final_project.domain.chatting.service.ChatRoomService;
import com.hanghae.final_project.domain.chatting.utils.ChatUtils;
import com.hanghae.final_project.global.config.security.jwt.HeaderTokenExtractor;
import com.hanghae.final_project.global.config.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public static final String TOKEN = "token";
    public static final String SIMP_DESTINATION = "simpDestination";
    public static final String SIMP_SESSION_ID = "simpSessionId";
    public static final String INVALID_ROOM_ID = "InvalidRoomId";

    public static final String SUB_NOTICE = "notice";
    public static final String SUB_CHAT = "chat";

    private final HeaderTokenExtractor headerTokenExtractor;
    private final ChatUtils chatUtils;

    private final ChannelTopic topic;

    private final ChatRoomService chatRoomService;

    private final RedisPublisher redisPublisher;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


        // 최초 소켓 연결
        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("token 확인");
            log.info("token : " + accessor.getFirstNativeHeader(TOKEN));
            String headerToken = accessor.getFirstNativeHeader(TOKEN);
            String token = headerTokenExtractor.extract(headerToken);
            log.info(jwtDecoder.decodeUsername(token).getUsername());

        }
        // 소켓 연결 후 ,SUBSCRIBE 등록
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            log.info("SubScribe destination : " + message.getHeaders().get(SIMP_DESTINATION));
            log.info("SubScribe sessionId : " + message.getHeaders().get(SIMP_SESSION_ID));

            String headerToken = accessor.getFirstNativeHeader(TOKEN);
            String token = headerTokenExtractor.extract(headerToken);
            String username = jwtDecoder.decodeUsername(token).getUsername();

            String destination = Optional.ofNullable(
                    (String) message.getHeaders().get(SIMP_DESTINATION)
            ).orElse(INVALID_ROOM_ID);

            String sessionId = Optional.ofNullable(
                    (String) message.getHeaders().get(SIMP_SESSION_ID)
            ).orElse(null);

            // 채팅 구독 / 알림 구독 분기처리

            //알람일 경우
            if (destination.contains(SUB_NOTICE)) {

                //이 타이밍에 뭘 해야할까..?

                log.info("알림 구독 하는 곳 ");

            }

            //채팅일 경우
            if (destination.contains(SUB_CHAT)) {

                String roomId = chatUtils.getRoodIdFromDestination(destination);

                //redis에  key(roomId) :  Value( sessionId , nickname ) 저장
                chatRoomService.enterChatRoom(roomId, sessionId, username);


                ChatMessageSaveDto chatMessageSaveDto = ChatMessageSaveDto.builder()
                        .type(ChatMessageSaveDto.MessageType.ENTER)
                        .roomId(roomId)
                        .userList(chatRoomService.findUser(roomId, sessionId))
                        .build();
                //list 주기
                MessageDto<ChatMessageSaveDto> chatmessage= MessageDto.<ChatMessageSaveDto>builder()
                        .type(MessageDto.MessageType.CHAT)
                        .data(chatMessageSaveDto)
                        .build();

                redisPublisher.publishChatMessage(topic,
                        chatmessage
                );

            }


        } else if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) {
            //진행해야할 것
            //reids SubScribe 해제
            log.info("UNSUBSCRIBE sessionId : " + message.getHeaders().get(SIMP_SESSION_ID));
            log.info("UNSUBSCRIBE destination : " + message.getHeaders().get(SIMP_DESTINATION));

            String sessionId = Optional.ofNullable(
                    (String) message.getHeaders().get(SIMP_SESSION_ID)
            ).orElse(null);

            String roomId = chatRoomService.leaveChatRoom(sessionId);

            log.info("Socket 연결 끊어진 RoomId : " + roomId);


           ChatMessageSaveDto chatMessageSaveDto =  ChatMessageSaveDto.builder()
                    .type(ChatMessageSaveDto.MessageType.QUIT)
                    .roomId(roomId)
                    .userList(chatRoomService.findUser(roomId, sessionId))
                    .build();
            //list 주기

            redisPublisher.publishChatMessage(topic,MessageDto.<ChatMessageSaveDto>builder()
                    .data(chatMessageSaveDto)
                    .type(MessageDto.MessageType.CHAT)
                    .build()
            );
        }
        //소켓 연결 후 , 소켓 연결 해제 시
        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("Disconnect sessionId : " + message.getHeaders().get(SIMP_SESSION_ID));

            //Session_Id를 얻기
            String sessionId = Optional.ofNullable(
                    (String) message.getHeaders().get(SIMP_SESSION_ID)
            ).orElse(null);

            String roomId = chatRoomService.disconnectWebsocket(sessionId);
            if (roomId == null) return message;

            log.info("Socket 연결 끊어진 RoomId : " + roomId);


            ChatMessageSaveDto chatMessageSaveDto=ChatMessageSaveDto.builder()
                    .type(ChatMessageSaveDto.MessageType.QUIT)
                    .roomId(roomId)
                    .userList(chatRoomService.findUser(roomId, sessionId))
                    .build();

            //list 주기

            redisPublisher.publishChatMessage(topic,
                    MessageDto.<ChatMessageSaveDto>builder()
                            .data(chatMessageSaveDto)
                            .type(MessageDto.MessageType.CHAT).build()

            );

        }
        return message;
    }
}
