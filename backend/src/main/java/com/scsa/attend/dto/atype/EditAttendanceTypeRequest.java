package com.scsa.attend.dto.atype;

import com.scsa.attend.vo.AttendanceType;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditAttendanceTypeRequest {
    @Pattern(regexp = ".*\\S.*") // 아예 빈 문자열이거나, 공백만 있는것을 허용 X
    private String name;
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String earliestTime;
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String startTime;
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String endTime;
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String latestTime;

    public void updateAType(AttendanceType aType) {
        if (name != null) {
            aType.setName(name);
        }
        if (earliestTime != null) {
            aType.setEarliestTime(earliestTime);
        }
        if (startTime != null) {
            aType.setStartTime(startTime);
        }
        if (endTime != null) {
            aType.setEndTime(endTime);
        }
        if (latestTime != null) {
            aType.setLatestTime(latestTime);
        }
    }
}
