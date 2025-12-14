package com.scsa.attend.dto.ainfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scsa.attend.dto.atype.AttendanceTypeResponse;
import com.scsa.attend.dto.user.MemberResponse;
import com.scsa.attend.vo.AttendanceFullInfo;
import com.scsa.attend.vo.AttendanceInfo;
import com.scsa.attend.vo.AttendanceType;
import com.scsa.attend.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceInfoResponse {

    private Integer aInfoId;

    // 1. 재활용 DTO
    private MemberResponse member;
    private AttendanceTypeResponse aType;

    // 2. ATTENDANCE_INFO 필드

    // 날짜는 YYYY-MM-DD 형식으로 출력
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate aDate;

    //  Lombok의 getADate() 생성을 억제하고 수동으로 작성 (응답 제이슨이 adate, aDate 두개가 생기는 문제)
    @JsonProperty("aDate")
    public LocalDate getaDate() {
        return this.aDate;
    }

    private String isOff;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime leavingTime;


    private String status;      // normal, late/early, absent
    private String isApproved;  // approved, denied
    private String isOfficial;  // Y/N
    private String hasDoc;      // Y/N
    private String memNote;     // 학생 사유
    private String adminNote;   // 관리자 메모

    /**
     * VO 객체를 응답 DTO로 변환하는 정적 팩토리 메서드
     * (Service 계층에서 이 메서드를 사용해 데이터를 매핑합니다.)
     */
    /**
     * 통합 VO 객체(AttendanceFullInfo)를 응답 DTO로 변환하는 정적 팩토리 메서드
     * (Service 계층에서 이 메서드를 사용해 데이터를 매핑합니다.)
     */
    public static AttendanceInfoResponse fromFullInfo(AttendanceFullInfo fullInfo) {

        // 1. FullInfo에서 개별 VO 객체 추출
        AttendanceInfo info = fullInfo.getAttendanceInfo();
        User user = fullInfo.getUser();
        AttendanceType aType = fullInfo.getAttendanceType();

        // 2. DTO 변환을 재활용하여 수행 (강한 결합 방지)
        MemberResponse memberResponse = MemberResponse.fromUser(user);
        AttendanceTypeResponse aTypeResponse = AttendanceTypeResponse.fromAType(aType);

        return AttendanceInfoResponse.builder()
                .aInfoId(info.getId())
                .member(memberResponse)
                .aType(aTypeResponse)
                .aDate(info.getADate())
                .isOff(info.getIsOff())
                .arrivalTime(info.getArrivalTime())
                .leavingTime(info.getLeavingTime())
                .status(info.getStatus())
                .isApproved(info.getIsApproved())
                .isOfficial(info.getIsOfficial())
                // hasDoc 로직 (docPath는 AttendanceInfo에 있다고 가정)
                .hasDoc(info.getDocPath() != null && !info.getDocPath().isEmpty() ? "Y" : "N")
                .memNote(info.getMemNote())
                .adminNote(info.getAdminNote())
                .build();
    }

}