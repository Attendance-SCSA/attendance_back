package com.scsa.attend.controller;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.atype.AddAttendanceTypeRequest;
import com.scsa.attend.dto.atype.AttendanceTypeResponse;
import com.scsa.attend.dto.atype.EditAttendanceTypeRequest;
import com.scsa.attend.dto.user.AddMemberRequest;
import com.scsa.attend.dto.user.MemberResponse;
import com.scsa.attend.exception.InvalidInputException;
import com.scsa.attend.exception.NotFoundException;
import com.scsa.attend.exception.PermissionDeniedException;
import com.scsa.attend.exception.ResourceConflictException;
import com.scsa.attend.service.AttendanceTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated // DTO 아닌 개별 메서드 파라미터에도 @NotNull 등의 검증을 가능하게 함
@RequestMapping("/attendance_types")
public class AttendanceTypeController {

    private final AttendanceTypeService aTypeService;

    @GetMapping
    public List<AttendanceTypeResponse> getAllAType()
            throws PermissionDeniedException {
        Integer userId = AuthController.userId;
        List<AttendanceTypeResponse> responseList = aTypeService.findAllATypes(userId);
        return responseList;
    }

    @PostMapping
    public AttendanceTypeResponse addAType(@Valid @RequestBody AddAttendanceTypeRequest request)
            throws ResourceConflictException, PermissionDeniedException, InvalidInputException {
        Integer userId = AuthController.userId;
        AttendanceTypeResponse response = aTypeService.createAType(userId, request);
        return response;
    }

    @GetMapping("/{aTypeId}")
    public AttendanceTypeResponse getAType(@NotNull @PathVariable("aTypeId") Integer aTypeId)  {
        Integer userId = AuthController.userId;
        AttendanceTypeResponse response = aTypeService.findAType(userId, aTypeId);
        return response;
    }

    @PatchMapping("/{aTypeId}")
    public AttendanceTypeResponse editAType(@NotNull @PathVariable("aTypeId") Integer aTypeId,
                                            @Valid @RequestBody EditAttendanceTypeRequest request)  {
        Integer userId = AuthController.userId;
        AttendanceTypeResponse response = aTypeService.modifyAType(userId, aTypeId, request);
        return response;
    }

    @DeleteMapping("/{aTypeId}")
    public SuccessResponse removeATypeMember(@NotNull @PathVariable("aTypeId") Integer aTypeId)  {
        Integer userId = AuthController.userId;
        SuccessResponse response = aTypeService.removeAType(userId, aTypeId);
        return response;
    }




}
