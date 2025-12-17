package com.scsa.attend.service;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.atype.AddAttendanceTypeRequest;
import com.scsa.attend.dto.atype.AttendanceTypeResponse;
import com.scsa.attend.dto.atype.EditAttendanceTypeRequest;
import com.scsa.attend.exception.InvalidInputException;
import com.scsa.attend.exception.NotFoundException;
import com.scsa.attend.exception.ResourceConflictException;
import com.scsa.attend.mapper.AttendanceInfoMapper;
import com.scsa.attend.mapper.AttendanceTypeMapper;
import com.scsa.attend.vo.AttendanceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceTypeService {

    private final AttendanceTypeMapper aTypeMapper;
    private final AttendanceInfoMapper aInfoMapper;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<AttendanceTypeResponse> findAllATypes(Integer userId) {
        userService.requireAdmin(userId);
        List<AttendanceType> aTypeList = aTypeMapper.selectAllATypes();
        List<AttendanceTypeResponse> responseList = aTypeList.stream()
                .map(AttendanceTypeResponse::fromAType)
                .toList();
        return responseList;
    }

    @Transactional
    public AttendanceTypeResponse createAType(Integer userId, AddAttendanceTypeRequest request) {
        userService.requireAdmin(userId);
        checkNameDuplicateForAdd(request.getName());
        checkPeriodValidation(request.getEarliestTime(), request.getStartTime(), request.getEndTime(), request.getLatestTime());
        AttendanceType aType = request.toAType();
        aTypeMapper.insertAType(aType);
        AttendanceTypeResponse response = AttendanceTypeResponse.fromAType(aType);
        return response;
    }

    @Transactional(readOnly = true)
    public AttendanceTypeResponse findAType(Integer userId, Integer aTypeId) {
        userService.requireAdmin(userId);
        AttendanceType aType = aTypeMapper.selectAType(aTypeId);
        checkExistingAType(aType);
        AttendanceTypeResponse response = AttendanceTypeResponse.fromAType(aType);
        return response;
    }

    @Transactional
    public AttendanceTypeResponse modifyAType(Integer userId, @NotNull Integer aTypeId, @Valid EditAttendanceTypeRequest request) {
        userService.requireAdmin(userId);
        checkDefaultAType(aTypeId);
        checkNameDuplicateForUpdate(request.getName(), aTypeId);
        AttendanceType aType = aTypeMapper.selectAType(aTypeId);
        checkExistingAType(aType);
        request.updateAType(aType);
        checkPeriodValidation(aType.getEarliestTime(), aType.getStartTime(), aType.getEndTime(), aType.getLatestTime());
        aTypeMapper.updateAType(aType);
        AttendanceTypeResponse response = AttendanceTypeResponse.fromAType(aType);
        return response;
    }

    @Transactional
    public SuccessResponse removeAType(Integer userId, @NotNull Integer aTypeId) {
        userService.requireAdmin(userId);
        checkDefaultAType(aTypeId);
        AttendanceType aType = aTypeMapper.selectAType(aTypeId);
        checkExistingAType(aType);

        // 기본 유형으로 변경
        int updatedCount = aInfoMapper.updateAInfoATypeToDefault(aTypeId, 1);

        // 삭제
        aTypeMapper.deleteAType(aTypeId);

        return new SuccessResponse("출결 유형이 성공적으로 삭제되었습니다. (" + updatedCount + "건의 출결 정보가 기본 유형으로 변경됨)");

    }


    // -------------------------- 내부 로직 -------------------------

    protected void checkExistingAType(AttendanceType aType) {
        if (aType == null) {
            throw new NotFoundException("해당하는 출석 유형을 찾을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    private void checkNameDuplicateForAdd(String name)  {
        AttendanceType aType = aTypeMapper.selectATypeByName(name);
        if (aType != null) {
            throw new ResourceConflictException("이미 존재하는 출석 유형 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    private void checkNameDuplicateForUpdate(String newName, Integer aTypeId)  {
        AttendanceType existingAType = aTypeMapper.selectATypeByName(newName);
        if (existingAType != null && !existingAType.getId().equals(aTypeId)) {
            throw new ResourceConflictException("이미 존재하는 출석 유형 이름입니다.");
        }
    }


    private void checkDefaultAType(Integer aTypeId) {
        if (aTypeId.equals(1)) {
            throw new InvalidInputException("기본 출석 유형은 수정하거나 삭제할 수 없습니다.");
        }
    }

    private void checkPeriodValidation(String earliestTime, String startTime, String endTime, String latestTime) {
        // 1. LocalTime 객체로 변환
        LocalTime earliest;
        LocalTime start;
        LocalTime end;
        LocalTime latest;

        try {
            // String을 LocalTime 객체로 파싱합니다.
            // 참고: 패턴 검증(@Pattern)이 이미 완료되었다고 가정합니다.
            earliest = LocalTime.parse(earliestTime);
            start = LocalTime.parse(startTime);
            end = LocalTime.parse(endTime);
            latest = LocalTime.parse(latestTime);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("시간 형식이 올바르지 않습니다: HH:MM:SS");
        }

        // 2. 엄격한 순서 비교 및 유효성 검사 (T1 < T2 여부 확인)

        // 검증 1: earliest < start
        // T_start가 T_earliest보다 빠르거나 같으면 예외 발생 (T_start <= T_earliest)
        if (!start.isAfter(earliest)) { // T_start.isAfter(T_earliest) 가 아니면
            throw new InvalidInputException("최소 시작 시간(" + earliestTime + ")은 시작 시간(" + startTime + ")보다 빨라야 합니다.");
        }

        // 검증 2: start < end
        // T_end가 T_start보다 빠르거나 같으면 예외 발생 (T_end <= T_start)
        if (!end.isAfter(start)) { // T_end.isAfter(T_start) 가 아니면
            throw new InvalidInputException("시작 시간(" + startTime + ")은 종료 시간(" + endTime + ")보다 빨라야 합니다.");
        }

        // 검증 3: end < latest
        // T_latest가 T_end보다 빠르거나 같으면 예외 발생 (T_latest <= T_end)
        if (!latest.isAfter(end)) { // T_latest.isAfter(T_end) 가 아니면
            throw new InvalidInputException("종료 시간(" + endTime + ")은 최대 마감 시간(" + latestTime + ")보다 빨라야 합니다.");
        }
    }



}
