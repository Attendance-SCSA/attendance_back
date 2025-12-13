package com.scsa.attend.service;

import com.scsa.attend.dto.ainfo.AttendanceInfoResponse;
import com.scsa.attend.dto.ainfo.SearchAttendanceInfoRequest;
import com.scsa.attend.mapper.AttendanceInfoMapper;
import com.scsa.attend.vo.AttendanceFullInfo;
import com.scsa.attend.vo.AttendanceInfo;
import com.scsa.attend.vo.AttendanceInfoBatch;
import com.scsa.attend.vo.AttendanceInfoSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceInfoService {

    private final AttendanceInfoMapper aInfoMapper;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<AttendanceInfoResponse> findAInfosByCondition(Integer userId, SearchAttendanceInfoRequest request) {
        Integer memId = request.getMemId();
        if (memId == null) {
            userService.requireAdmin(userId);
        } else {
            userService.requireAdminOrSelf(userId, memId);
        }
        userService.checkPeriodValidation(request.getStartDate(), request.getEndDate());

        AttendanceInfoSearchCondition condition = request.convertToSearchCondition();
        List<AttendanceFullInfo> aFullInfoList = aInfoMapper.selectAFullInfosByCondition(condition);
        List<AttendanceInfoResponse> responseList = aFullInfoList.stream()
                .map(AttendanceInfoResponse::fromFullInfo)
                .toList();

        return responseList;

    }


    // --------------- 내부 로직

    @Transactional
    public void createAInfoBatch(AttendanceInfoBatch batchParams) {
        aInfoMapper.insertAInfoBatch(batchParams);
    }

    @Transactional
    public int modifyAInfoATypeToDefault(Integer aTypeId, Integer defaultTypeId) {
        return aInfoMapper.updateAInfoATypeToDefault(aTypeId, defaultTypeId);
    }
}
