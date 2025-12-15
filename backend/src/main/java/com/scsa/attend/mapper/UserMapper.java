package com.scsa.attend.mapper;

import com.scsa.attend.vo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    List<User> selectAllMembers();
    void insertMember(User user);
    User selectMember(@Param("memberId") Integer memberId);
    void updateUser(User user);
    void deleteUser(Integer memberId);

    User selectUserByLoginIdAndLoginPwd(User user);

    // ------------ 내부 로직 --------------
    User selectUser(@Param("userId") Integer userId);
    User selectUserByLoginId(@Param("loginId") String loginId);



}
