package com.scsa.attend.dto.user;

import com.scsa.attend.vo.User;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditMemberRequest {
    @Pattern(regexp = "^\\S+$") // 공백을 아예 허용 X
    private String loginPwd;
    @Pattern(regexp = ".*\\S.*") // 아예 빈 문자열이거나, 공백만 있는것을 허용 X
    private String name;
    @Pattern(regexp = "DS|DX|SDS")
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
