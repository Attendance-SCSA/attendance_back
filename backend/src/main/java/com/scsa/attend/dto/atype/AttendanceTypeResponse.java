package com.scsa.attend.dto.atype;

import com.scsa.attend.dto.user.MemberResponse;
import com.scsa.attend.vo.AttendanceType;
import com.scsa.attend.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceTypeResponse {
    private Integer id;
    private String name;
    private String earliestTime;
    private String startTime;
    private String endTime;
    private String latestTime;

    public static AttendanceTypeResponse fromAType(AttendanceType aType) {
        AttendanceTypeResponse response = new AttendanceTypeResponse();
        response.setId(aType.getId());
        response.setName(aType.getName());
        response.setEarliestTime(aType.getEarliestTime());
        response.setStartTime(aType.getStartTime());
        response.setEndTime(aType.getEndTime());
        response.setLatestTime(aType.getLatestTime());
        return response;
    }
}
