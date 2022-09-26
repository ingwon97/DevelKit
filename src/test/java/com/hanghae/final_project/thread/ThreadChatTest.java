package com.hanghae.final_project.thread;

import com.hanghae.final_project.domain.chatting.dto.request.ChatMessageSaveDto;
import com.hanghae.final_project.domain.chatting.service.ChatRedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ThreadChatTest {

    @Autowired
    private ChatRedisCacheService chatRedisCacheService;

    private ArrayList<ChatMessageSaveDto> chatMessageSaveDtoArrayList;

    @BeforeAll
    void setup(){
        chatMessageSaveDtoArrayList =new ArrayList<>();
        for(int i =0 ; i <1; i++){
            chatMessageSaveDtoArrayList.add(ChatMessageSaveDto.builder()
                            .message("text "+i)
                            .writer("hosung")
                            .roomId("1")
                            .createdAt("asd")
                    .build());
        }
    }

    @Nested
    @DisplayName("chat 저장 test")
    class ChatDataInsertTest{

        @DisplayName(" mysql insert by MultiThread")
        void test1(){
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            for(ChatMessageSaveDto chatMessageSaveDto : chatMessageSaveDtoArrayList){
             //   chatRedisCacheService.addChatWithThread(chatMessageDto);
            }
            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());
        }


        @DisplayName("redis insert by MultiThread")
        void test2(){

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            for(ChatMessageSaveDto chatMessageSaveDto : chatMessageSaveDtoArrayList){
                chatRedisCacheService.addChat(chatMessageSaveDto);
            }

            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());
        }

    }


}
