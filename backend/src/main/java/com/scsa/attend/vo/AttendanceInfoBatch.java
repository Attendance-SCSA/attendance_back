package com.scsa.attend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date; // Date 타입을 유지

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceInfoBatch {
    private Integer memId;
    private Date startDay; // java.util.Date 사용
    private Date endDay;   // java.util.Date 사용

    public static AttendanceInfoBatch fromUser(User user){
        AttendanceInfoBatch batchParams = new AttendanceInfoBatch();
        batchParams.memId = user.getId();
        batchParams.startDay = user.getStartDay();
        batchParams.endDay = user.getEndDay();
        return batchParams;
    }
}