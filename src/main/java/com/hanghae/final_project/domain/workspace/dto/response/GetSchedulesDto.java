package com.hanghae.final_project.domain.workspace.dto.response;

import com.hanghae.final_project.domain.workspace.model.Schedule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetSchedulesDto {

    private Long id;
    private String content;
    private String eventDate;

    public static GetSchedulesDto of(Schedule schedule) {
        return GetSchedulesDto.builder()
                .id(schedule.getId())
                .content(schedule.getContent())
                .eventDate(schedule.getDate())
                .build();
    }
}
