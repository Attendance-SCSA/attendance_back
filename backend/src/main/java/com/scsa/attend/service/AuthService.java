package com.scsa.attend.service;

import com.scsa.attend.dto.auth.LoginRequest;
import com.scsa.attend.mapper.UserMapper;
import com.scsa.attend.vo.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserMapper userMapper;

    public Integer login(@Valid LoginRequest request) {
        User user = new User();
        user.setLoginId(request.getLoginId());
        user.setLoginPwd(request.getLoginPwd());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"  + user);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + request);
        User loginUser = userMapper.selectUserByLoginIdAndLoginPwd(user);

        return loginUser.getId();

    }
}
