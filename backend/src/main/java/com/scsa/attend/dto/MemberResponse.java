package com.scsa.attend.dto;

import com.scsa.attend.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    public static MemberResponse fromUser(User user) {
        MemberResponse response = new MemberResponse();
        response.setId(user.getId());
        response.setLoginId(user.getLoginId());
        response.setName(user.getName());
        response.setCompany(user.getCompany());
        response.setRole(user.getRole());

        return response; // 완성된 응답 객체 반환

    }
}
