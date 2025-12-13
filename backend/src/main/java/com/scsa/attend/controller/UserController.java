package com.scsa.attend.controller;

import com.scsa.attend.dto.user.AddMemberRequest;
import com.scsa.attend.dto.user.EditMemberRequest;
import com.scsa.attend.dto.user.MemberResponse;
import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.exception.InvalidInputException;
import com.scsa.attend.exception.NotFoundException;
import com.scsa.attend.exception.PermissionDeniedException;
import com.scsa.attend.exception.ResourceConflictException;
import com.scsa.attend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated // DTO 아닌 개별 메서드 파라미터에도 @NotNull 등의 검증을 가능하게 함
@RequestMapping("/members")
public class UserController {

    private final Integer adminTmpId = 1;
    private final Integer memberTmpId = 3;

    private final UserService userService;

    @GetMapping
    public List<MemberResponse> getAllMembers() {
        Integer userId = adminTmpId;
        List<MemberResponse> responseList = userService.findAllMembers(userId);
        return responseList;

    }

    @PostMapping
    public MemberResponse addMember(@Valid @RequestBody AddMemberRequest request) {
        Integer userId = adminTmpId;
        MemberResponse response = userService.createMember(userId, request);
        return response;
    }

    @GetMapping("/{memberId}")
    public MemberResponse getMember(@NotNull @PathVariable("memberId") Integer memberId) {
        Integer userId = memberTmpId;
        MemberResponse response = userService.findMember(userId, memberId);
        return response;
    }

    @PatchMapping("/{memberId}")
    public MemberResponse editMember(@NotNull @PathVariable("memberId") Integer memberId,
                                     @Valid @RequestBody EditMemberRequest request) {
        Integer userId = memberTmpId;
        MemberResponse response = userService.modifyMember(userId, memberId, request);
        return response;
    }

    @DeleteMapping("/{memberId}")
    public SuccessResponse deleteMember(@NotNull @PathVariable("memberId") Integer memberId) {
        Integer userId = adminTmpId;
        SuccessResponse response = userService.removeMember(userId, memberId);
        return response;
    }


}
