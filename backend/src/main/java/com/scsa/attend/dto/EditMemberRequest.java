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
public class EditMemberRequest {
    private String loginPwd;
    private String name;
    private String company;

    public void updateUser(User user) {
        if (loginPwd != null) {
            user.setLoginPwd(loginPwd);
        }
        if (name != null) {
            user.setName(name);
        }
        if (company != null) {
            user.setCompany(company);
        }
    }
}
