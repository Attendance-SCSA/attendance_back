package com.scsa.attend.controller;

import com.scsa.attend.dto.MemberResponse;
import com.scsa.attend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated // DTO 아닌 개별 메서드 파라미터에도 @NotNull 등의 @valid 이용한 검증을 가능하게 함
public class UserController {

    private final Integer adminTmpId = 1;
    private final Integer memberTmpId = 2;

    private final UserService userService;


    @GetMapping
    public List<MemberResponse> getAllMembers() {
        Integer userId = adminTmpId;
        List<MemberResponse> responseList = userService.findAllmembers(userId);
        return responseList;

    }

}
