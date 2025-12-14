package com.scsa.attend.mapper;

import com.scsa.attend.vo.AttendanceFullInfo;
import com.scsa.attend.vo.AttendanceInfo;
import com.scsa.attend.vo.AttendanceInfoBatch;
import com.scsa.attend.vo.AttendanceInfoSearchCondition;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceInfoMapper {

    List<AttendanceFullInfo> selectAFullInfosByCondition(AttendanceInfoSearchCondition condition);
    AttendanceFullInfo selectAFullInfo(@Param("aInfoId") Integer aInfoId);
    int updateAttendanceInfo(AttendanceInfo attendanceInfo);
    AttendanceFullInfo selectAFullInfoByDateAndMemId(@Param("aDate") LocalDate aDate, @Param("memId") Integer memId);
    void updateArrivalTime(@Param("aInfoId") Integer aInfoId, @Param("recordTime") LocalDateTime recordTime);
    void updateLeavingTime(@Param("aInfoId") Integer aInfoId, @Param("recordTime") LocalDateTime recordTime);
    void updateStatus(AttendanceInfo aInfo);

    /**
     * 지정된 기간 내의 평일 날짜에 대해 ATTENDANCE_INFO 테이블에
     * 출결 행을 일괄 생성합니다. (Oracle CONNECT BY 구문 사용)
     * @param batchParams memId, startDay, endDay 정보를 담은 VO
     */
    void insertAInfoBatch(AttendanceInfoBatch batchParams);
    int updateAInfoATypeToDefault(@Param("oldTypeId") Integer oldTypeId, @Param("defaultTypeId") Integer defaultTypeId);
    List<AttendanceFullInfo> selectFullInfosByDate(@Param("targetDate") LocalDate targetDate);



}
