package com.hanghae.final_project.domain.chatting.dto.request;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto <T>{

    public enum MessageType {
        CHAT, ALARM
    }

    private MessageType type;
    private T  data;

//    @JsonCreator
//    public MessageDto(@JsonProperty("data") T data,MessageType messageType){
//        log.info("여기실행 : 생성자 @JSONCRAEATER ");
//        this.type=messageType;
//        this.data =data;
//    }
//    public T getData() {
//        return data;
//    }

}
