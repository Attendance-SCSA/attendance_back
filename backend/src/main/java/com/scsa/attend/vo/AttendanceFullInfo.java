package com.scsa.attend.vo;

import lombok.*;

/**
 * DB 조인 결과를 담는 통합 VO (Full Info)
 * DTO 변환 시 필요한 모든 VO를 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceFullInfo {

    // 핵심 출석 정보
    private AttendanceInfo attendanceInfo;

    // 회원 정보
    private User user;

    // 출석 유형 정보
    private AttendanceType attendanceType;
}