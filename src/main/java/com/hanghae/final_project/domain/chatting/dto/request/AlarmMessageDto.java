package com.hanghae.final_project.domain.chatting.dto.request;

import com.hanghae.final_project.domain.workspace.model.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AlarmMessageDto {

    public enum MessageType {
        NOTICE, DOCUMENT
    }

    private String alarmMessage;
    private String writer;
    private String workspaceTitle;
    private Long workspaceId;


    public static AlarmMessageDto makeNoticeAlarmMessageDto(Notice notice) {
        return AlarmMessageDto.builder()
                .workspaceId(notice.getWorkSpace().getId())
                .workspaceTitle(notice.getWorkSpace().getTitle())
                .alarmMessage(" 프로젝트\""+notice.getWorkSpace().getTitle()+"\"에 새로운 문서가 등록되었습니다.")
                .build();

    }
}
