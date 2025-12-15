package com.scsa.attend.controller;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.auth.LoginRequest;
import com.scsa.attend.service.AuthService;
import com.scsa.attend.vo.User;
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
    public User login (@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return user;
    }

    @PostMapping("/logout")
    public SuccessResponse logout (@RequestHeader(value = "userId", required = false) Integer userId) {
        SuccessResponse response = authService.logout(userId);
        return response;
    }
}
