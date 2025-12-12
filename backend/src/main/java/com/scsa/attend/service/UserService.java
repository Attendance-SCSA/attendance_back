package com.scsa.attend.service;

import com.scsa.attend.dto.AddMemberRequest;
import com.scsa.attend.dto.EditMemberRequest;
import com.scsa.attend.dto.MemberResponse;
import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.exception.NotFoundException;
import com.scsa.attend.exception.PermissionDeniedException;
import com.scsa.attend.mapper.UserMapper;
import com.scsa.attend.vo.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<MemberResponse> findAllMembers(Integer userId)
            throws PermissionDeniedException {
        requireAdmin(userId);
        List<User> userList = userMapper.selectAllMembers();
        List<MemberResponse> responseList = userList.stream()
                .map(MemberResponse::fromUser)
                .toList();

        return responseList;
    }

    @Transactional
    public MemberResponse createMember(Integer userId, @Valid AddMemberRequest request)
            throws PermissionDeniedException, NotFoundException {
        requireAdmin(userId);
        checkLoginIdDuplicate(request.getLoginId());
        User user = request.toUser();
        user.setRole("member");
        userMapper.insertMember(user);
        MemberResponse response = MemberResponse.fromUser(user);
        return response;

    }

    @Transactional(readOnly = true)
    public MemberResponse findMember(Integer userId, Integer memberId)
            throws PermissionDeniedException, NotFoundException {
        requireAdminOrSelf(userId, memberId);
        User user = userMapper.selectMember(memberId);
        checkExistingMember(user);
        MemberResponse response = MemberResponse.fromUser(user);
        return response;
    }

    @Transactional

    public MemberResponse modifyMember(Integer userId, Integer memberId, EditMemberRequest request)
            throws PermissionDeniedException, NotFoundException {
        requireAdminOrSelf(userId, memberId);
        User user = userMapper.selectMember(memberId);
        checkExistingMember(user);
        request.updateUser(user);
        userMapper.updateUser(user);
        MemberResponse response = MemberResponse.fromUser(user);
        return response;
    }

    @Transactional
    public SuccessResponse removeMember(Integer userId,Integer memberId)
            throws PermissionDeniedException, NotFoundException {
        requireAdmin(userId);
        User user = userMapper.selectUser(memberId);
        checkExistingMember(user);
        userMapper.deleteUser(memberId);
        SuccessResponse response = new SuccessResponse("학생 삭제가 완료되었습니다.");
        return response;
    }

    // --------------- 내부 유효 요청 검증 로직 ----------------
    @Transactional(readOnly = true)
    private void requireAdmin(Integer userId) throws PermissionDeniedException {
        User user = userMapper.selectUser(userId);
        if (user == null || !"admin".equals(user.getRole())) {
            throw new PermissionDeniedException("해당 작업에 대한 권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    private void requireAdminOrSelf(Integer userId, Integer memberId) throws PermissionDeniedException {
        User user = userMapper.selectUser(userId);
        if (user == null || (!"admin".equals(user.getRole()) && !memberId.equals(userId)) ) {
            throw new PermissionDeniedException("해당 작업에 대한 권한이 없습니다.");
        }
    }

    private void checkExistingMember(User member) throws NotFoundException {
        if (member == null) {
            throw new NotFoundException("해당하는 멤버를 찾을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    private void checkLoginIdDuplicate(String loginId) throws NotFoundException {
        User user = userMapper.selectUserByLoginId(loginId);
        if (user != null) {
            throw new NotFoundException("이미 존재하는 로그인 ID입니다.");
        }
    }

}
