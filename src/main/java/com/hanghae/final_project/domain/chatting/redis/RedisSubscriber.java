package com.hanghae.final_project.domain.chatting.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.final_project.domain.chatting.dto.request.AlarmMessageDto;
import com.hanghae.final_project.domain.chatting.dto.request.ChatMessageSaveDto;
import com.hanghae.final_project.domain.chatting.dto.request.MessageDto;
import com.hanghae.final_project.domain.chatting.service.ChatRedisCacheService;
import com.hanghae.final_project.global.commonDto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatRedisCacheService chatRedisCacheService;

    public void sendMessage(String publishMessage) {
        log.info("Subscribe on message() 여기 들어옴");
        try {
            //redis에서 발행된 데이터를 받아 deserialize
            MessageDto<?> messageDto=objectMapper.readValue(publishMessage, MessageDto.class);

            log.info("messageDto의 type : {}",messageDto.getType());
            if (messageDto.getType().equals(MessageDto.MessageType.CHAT)) {
                MessageDto<ChatMessageSaveDto> chatMessage =objectMapper.readValue(publishMessage, new TypeReference<>() {});


                log.info("채팅내용  : {}",chatMessage.getData().getMessage());
                log.info("채팅방 : {}",chatMessage.getData().getRoomId());

                //WebSocket 구독자에게 채팅 메시지 Send
                messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getData().getRoomId(), chatMessage.getData());
                return;
            }

            if(messageDto.getType().equals(MessageDto.MessageType.ALARM)){
                MessageDto<AlarmMessageDto> alarmMessage = objectMapper.readValue(publishMessage,new TypeReference<>(){});

                log.info("알람메시지 : {}",alarmMessage.getData().getAlarmMessage());
                log.info("워크스페이스ID : {}",alarmMessage.getData().getWorkspaceId());
                log.info("알람보내는 사람 : {}",alarmMessage.getData().getWriter());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
