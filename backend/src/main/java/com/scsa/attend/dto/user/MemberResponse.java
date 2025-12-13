package com.scsa.attend.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scsa.attend.vo.User;
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
public class MemberResponse {
    private Integer id;
    private String loginId;
//    private String loginPwd; 비밀번호 제외하고 응답으로 반환
    private String name;
    private String company;
    private String role;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDay;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDay;

    public static MemberResponse fromUser(User user) {
        MemberResponse response = new MemberResponse();
        response.setId(user.getId());
        response.setLoginId(user.getLoginId());
        response.setName(user.getName());
        response.setCompany(user.getCompany());
        response.setRole(user.getRole());
        response.setStartDay(user.getStartDay());
        response.setEndDay(user.getEndDay());

        return response; // 완성된 응답 객체 반환

    }
}
