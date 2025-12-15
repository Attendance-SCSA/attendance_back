package com.scsa.attend.service;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.auth.LoginRequest;
import com.scsa.attend.exception.NotFoundException;
import com.scsa.attend.mapper.UserMapper;
import com.scsa.attend.vo.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserMapper userMapper;

    public Set<Integer> loginedUserIds = new HashSet<>();

    public User login(@Valid LoginRequest request) {
        User user = new User();
        user.setLoginId(request.getLoginId());
        user.setLoginPwd(request.getLoginPwd());
        User loginUser = userMapper.selectUserByLoginIdAndLoginPwd(user);
        loginedUserIds.add(loginUser.getId());

        return loginUser;
    }

    public SuccessResponse logout(Integer userId) {
        if (!loginedUserIds.contains(userId)) {
            throw new NotFoundException("이미 로그아웃된 유저입니다.");
        } else {
            loginedUserIds.remove(userId);
            return new SuccessResponse("로그아웃에 성공했습니다.");
        }
    }
}
