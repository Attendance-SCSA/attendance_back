package com.scsa.attend.controller;

import com.scsa.attend.dto.ainfo.AttendanceInfoResponse;
import com.scsa.attend.dto.ainfo.SearchAttendanceInfoRequest;
import com.scsa.attend.service.AttendanceInfoService;
import com.scsa.attend.vo.AttendanceInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated // DTO 아닌 개별 메서드 파라미터에도 @NotNull 등의 검증을 가능하게 함
@RequestMapping("/attendance_info")
public class AttendanceInfoController {

    private final Integer adminTmpId = 1;
    private final Integer memberTmpId = 3;

    private final AttendanceInfoService aInfoService;

    @PostMapping("/search")
    public List<AttendanceInfoResponse> getAInfosByCondition (@Valid @RequestBody SearchAttendanceInfoRequest request) {
        Integer userId = adminTmpId;
        List<AttendanceInfoResponse> responseList = aInfoService.findAInfosByCondition(userId, request);
        return responseList;

    }



}
