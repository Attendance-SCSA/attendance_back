package com.scsa.attend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date; // Date 타입을 유지

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceInfoBatch {
    private Integer memId;
    private LocalDate startDay;
    private LocalDate endDay;   

    public static AttendanceInfoBatch fromUser(User user){
        AttendanceInfoBatch batchParams = new AttendanceInfoBatch();
        batchParams.memId = user.getId();
        batchParams.startDay = user.getStartDay();
        batchParams.endDay = user.getEndDay();
        return batchParams;
    }
}