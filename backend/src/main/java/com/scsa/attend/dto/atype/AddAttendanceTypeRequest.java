package com.scsa.attend.dto.atype;

import com.scsa.attend.vo.AttendanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAttendanceTypeRequest {
    @NotBlank
    @Pattern(regexp = ".*\\S.*") // 아예 빈 문자열이거나, 공백만 있는것을 허용 X
    private String name;
    @NotBlank
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String earliestTime;
    @NotBlank
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String startTime;
    @NotBlank
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String endTime;
    @NotBlank
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])")
    private String latestTime;

    public AttendanceType toAType() {
        AttendanceType aType = new AttendanceType();
        aType.setName(name);
        aType.setEarliestTime(earliestTime);
        aType.setStartTime(startTime);
        aType.setEndTime(endTime);
        aType.setLatestTime(latestTime);
        return aType;
    }
}
