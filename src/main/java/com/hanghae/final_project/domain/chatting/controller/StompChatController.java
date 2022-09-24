package com.hanghae.final_project.domain.chatting.controller;


import com.hanghae.final_project.domain.chatting.dto.request.ChatMessageSaveDto;
import com.hanghae.final_project.domain.chatting.redis.RedisPublisher;

import com.hanghae.final_project.domain.chatting.service.ChatRedisCacheService;
import com.hanghae.final_project.global.config.security.jwt.HeaderTokenExtractor;
import com.hanghae.final_project.global.config.security.jwt.JwtDecoder;
import com.hanghae.final_project.global.config.security.jwt.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StompChatController {

    private final RedisPublisher redisPublisher;


    private final ChatRedisCacheService chatRedisCacheService;

    private final RedisTemplate<String,String> roomRedisTemplate;
    private final ChannelTopic channelTopic;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;
    /*
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     * */

    @MessageMapping("/chat/message")
    public void message(ChatMessageSaveDto message, @Header("token") String token){
        UserInfo userInfo = jwtDecoder.decodeUsername(headerTokenExtractor.extract(token));

        message.setNickname(userInfo.getNickname());
        message.setWriter(userInfo.getUsername());
        message.setType(ChatMessageSaveDto.MessageType.TALK);
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")));

        redisPublisher.publish(channelTopic,message);
        chatRedisCacheService.addChat(message);
    }

}
