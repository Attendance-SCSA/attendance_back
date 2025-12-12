package com.scsa.attend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceType {
    private Integer id;
    private String name;
    private String earliestTime;
    private String startTime;
    private String endTime;
    private String latestTime;

}
