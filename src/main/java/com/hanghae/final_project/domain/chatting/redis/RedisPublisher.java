package com.hanghae.final_project.domain.chatting.redis;

import com.hanghae.final_project.domain.chatting.dto.request.AlarmMessageDto;
import com.hanghae.final_project.domain.chatting.dto.request.ChatMessageSaveDto;
import com.hanghae.final_project.domain.chatting.dto.request.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisPublisher {

    /*
     * redis 발행 서비스
     *
     * */
    private final RedisTemplate<String, Object> redisTemplate;

    public void publishChatMessage(ChannelTopic topic, MessageDto<ChatMessageSaveDto> messageDto) {

        redisTemplate.convertAndSend(topic.getTopic(), messageDto);
    }

    public void pulishAlarmMessage(ChannelTopic topic, MessageDto<AlarmMessageDto> messageDto){
        redisTemplate.convertAndSend(topic.getTopic(),messageDto);
    }
}