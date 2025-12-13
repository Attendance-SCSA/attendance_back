package com.scsa.attend.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scsa.attend.vo.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMemberRequest {
    @NotBlank
    private String loginId;
    @NotBlank
    private String loginPwd;
    @NotBlank
    private String name;
    @Pattern(regexp = "DS|DX|SDS")
    @NotBlank
    private String company;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull
    private LocalDate startDay;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull
    private LocalDate endDay;

    public User toUser() {
        User user = new User();
        user.setLoginId(loginId);
        user.setLoginPwd(loginPwd);
        user.setName(name);
        user.setCompany(company);
        user.setStartDay(startDay);
        user.setEndDay(endDay);
        return user;
    }
}
