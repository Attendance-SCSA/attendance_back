package com.scsa.attend.mapper;

import com.scsa.attend.vo.AttendanceFullInfo;
import com.scsa.attend.vo.AttendanceInfoBatch;
import com.scsa.attend.vo.AttendanceInfoSearchCondition;

import java.util.List;

public interface AttendanceInfoMapper {

    List<AttendanceFullInfo> selectAFullInfosByCondition(AttendanceInfoSearchCondition condition);


    /**
     * 지정된 기간 내의 평일 날짜에 대해 ATTENDANCE_INFO 테이블에
     * 출결 행을 일괄 생성합니다. (Oracle CONNECT BY 구문 사용)
     * @param batchParams memId, startDay, endDay 정보를 담은 VO
     */
    void insertAInfoBatch(AttendanceInfoBatch batchParams);
    int updateAInfoATypeToDefault(Integer oldTypeId, Integer defaultTypeId);



}
