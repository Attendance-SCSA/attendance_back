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
    private LocalDate startDate; // 둘 다 null이거나 둘 다 Date여야 함 (로직으로 검증)

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate; // 둘 다 null이거나 둘 다 Date여야 함 (로직으로 검증)

    private Integer memId;

    // JSON 배열 ["late/early", "absent"] 매핑을 위한 List<String>
    private List<@Pattern(regexp = "^(late/early|absent|normal)$") String> statusList;

    // 요청 예시가 "denied"이므로, Y/N 정규식은 제거하거나 수정해야 함
    @Pattern(regexp = "^(denied|accepted)$")
    private String isApproved;

    // Y/N만 허용된다면, 정규식을 유지하거나 해당 도메인 값을 허용하도록 수정
    @Pattern(regexp = "^[YNyn]$")
    private String isOfficial;

    public AttendanceInfoSearchCondition convertToSearchCondition() {

        // 이 메서드를 호출하기 전에 validateSearchDatePeriod(request)가 반드시 호출되어야 합니다.

        // VO 객체 생성 및 Setter를 이용한 데이터 복사
        AttendanceInfoSearchCondition condition = new AttendanceInfoSearchCondition();

        // 1. 시작 날짜 설정 (가공 없음)
        condition.setStartDate(startDate);

        // 2. 종료 날짜 설정 (DB 쿼리 최적화를 위한 +1일 처리)
        if (endDate != null) {
            // 쿼리에서 < (미만) 연산자를 사용하기 위해 하루를 더함
            LocalDate adjustedEndDate = endDate.plusDays(1);
            condition.setEndDate(adjustedEndDate);
        } else {
            condition.setEndDate(null);
        }

        // 3. 나머지 필드 설정
        condition.setMemId(memId);
        condition.setStatusList(statusList);
        condition.setIsApproved(isApproved);
        condition.setIsOfficial(isOfficial);

        return condition;
    }

}
