package com.scsa.attend.controller;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.auth.LoginRequest;
import com.scsa.attend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
public class AuthController {

    public static Integer userId = null;

    private final AuthService authService;

    @PostMapping("/login")
    public Integer login (@Valid @RequestBody LoginRequest request) {

        Integer loginId = authService.login(request);
        userId = loginId;
        Map<String, Integer> response = new HashMap<>();
        response.put("loginId", userId);
        return userId;

    }

    @PostMapping("/logout")
    public void logout () {
        userId = null;
    }
}
