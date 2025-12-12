package com.scsa.attend.mapper;

import com.scsa.attend.vo.AttendanceInfoBatch;

public interface AttendanceInfoMapper {

    /**
     * 지정된 기간 내의 평일 날짜에 대해 ATTENDANCE_INFO 테이블에
     * 출결 행을 일괄 생성합니다. (Oracle CONNECT BY 구문 사용)
     * @param batchParams memId, startDay, endDay 정보를 담은 VO
     */
    void insertAttendanceInfoBatch(AttendanceInfoBatch batchParams);
}
