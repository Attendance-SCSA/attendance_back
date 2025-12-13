package com.scsa.attend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated // DTO 아닌 개별 메서드 파라미터에도 @NotNull 등의 검증을 가능하게 함
@RequestMapping("/attendance_info")
public class AttendanceInfoController {
}
