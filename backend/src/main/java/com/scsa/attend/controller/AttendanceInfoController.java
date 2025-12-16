package com.scsa.attend.controller;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.ainfo.*;
import com.scsa.attend.service.AttendanceInfoService;
import com.scsa.attend.vo.AttendanceFullInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated // DTO 아닌 개별 메서드 파라미터에도 @NotNull 등의 검증을 가능하게 함
@RequestMapping("/attendance_info")
public class AttendanceInfoController {

    private final AttendanceInfoService aInfoService;

    @PostMapping("/search")
    public List<AttendanceInfoResponse> getAFullInfosByCondition (@RequestHeader(value = "userId", required = false) Integer userId,
                                                                  @Valid @RequestBody SearchAttendanceInfoRequest request) {
//        Integer userId = AuthController.userId;
        List<AttendanceInfoResponse> responseList = aInfoService.findAInfosByCondition(userId, request);
        return responseList;

    }

    @GetMapping("/{aInfoId}")
    public AttendanceInfoResponse getAFullInfo (@RequestHeader(value = "userId", required = false) Integer userId,
                                                @NotNull @PathVariable("aInfoId") Integer aInfoId) {
//        Integer userId = AuthController.userId;
        AttendanceInfoResponse response = aInfoService.findAFullInfo(userId, aInfoId);
        return response;
    }

    @PatchMapping("/{aInfoId}")
    public AttendanceInfoResponse editAInfo (@RequestHeader(value = "userId", required = false) Integer userId,
                                             @NotNull @PathVariable("aInfoId") Integer aInfoId,
                                             @Valid @RequestBody EditAttendanceInfoByAdminRequest updateData) {
//        Integer userId = AuthController.userId;
        AttendanceInfoResponse response = aInfoService.modifyAInfo(userId, aInfoId, updateData);
        return response;
    }

    @PatchMapping
    public SuccessResponse editAInfoMulti(@RequestHeader(value = "userId", required = false) Integer userId,
                                          @Valid @RequestBody EditAttendanceInfoMultiRequest request) {
//        Integer userId = AuthController.userId;
        SuccessResponse response = aInfoService.modifyAInfoMulti(userId, request);
        return response;
    }

    @PatchMapping("/{aInfoId}/arrival")
    public AttendanceFullInfo updateArrivalTime(@RequestHeader(value = "userId", required = false) Integer userId,
                                             @NotNull @PathVariable("aInfoId") Integer aInfoId) {
//        Integer userId = 3;
        AttendanceFullInfo response = aInfoService.modifyArrivalTime(userId, aInfoId);
        return response;
    }

    @PatchMapping("/{aInfoId}/leaving")
    public AttendanceFullInfo updateLeavingTime(@RequestHeader(value = "userId", required = false) Integer userId,
                                                @NotNull @PathVariable("aInfoId") Integer aInfoId) {
//        Integer userId = 3;
        AttendanceFullInfo response = aInfoService.modifyLeavingTime(userId, aInfoId);
        return response;
    }

    @PatchMapping("/calculate-status")
    public SuccessResponse updateStatus(@RequestHeader(value = "userId", required = false) Integer userId,
                                             @Valid @RequestBody CalculateAttendanceInfoStatusRequest request) {
//        Integer userId = AuthController.userId;
        SuccessResponse response = aInfoService.calculateAInfoStatus(request);
        return response;

    }

}
