package com.scsa.attend.dto.ainfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scsa.attend.vo.AttendanceInfoSearchCondition;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAttendanceInfoRequest {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    private List<Integer> memIdList;

    // JSON 배열 ["late/early", "absent", null] 등 매핑을 위한 List<String>
    private List<@Pattern(regexp = "^(late/early|absent|normal)$") String> statusList;

    private List<@Pattern(regexp = "^(denied|approved)$") String> isApprovedList;

    private List<@Pattern(regexp = "^[YNyn]$") String> isOfficialList;

    public AttendanceInfoSearchCondition convertToSearchCondition() {

        AttendanceInfoSearchCondition condition = new AttendanceInfoSearchCondition();
        condition.setStartDate(startDate);

        if (endDate != null) {
            // 종료 날짜 설정 (DB 쿼리 최적화를 위한 +1일 처리), 쿼리에서 < (미만) 연산자를 사용하기 위해 하루를 더함
            LocalDate adjustedEndDate = endDate.plusDays(1);
            condition.setEndDate(adjustedEndDate);
        } else {
            condition.setEndDate(null);
        }

        condition.setMemIdList(memIdList);
        condition.setStatusList(statusList);
        condition.setIsApprovedList(isApprovedList);
        condition.setIsOfficialList(isOfficialList);

        return condition;
    }

}
