package com.scsa.attend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// Lombok 어노테이션은 그대로 유지합니다.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceInfoSearchCondition {

    // DTO와 동일하게 LocalDate를 유지 (검색 시작 날짜)
    private LocalDate startDate;

    // DTO와 동일하게 LocalDate를 유지 (검색 종료 날짜)
    private LocalDate endDate;

    private List<Integer> memIdList;

    private List<String> statusList;
    private List<String> isApprovedList;
    private List<String> isOfficialList;
}