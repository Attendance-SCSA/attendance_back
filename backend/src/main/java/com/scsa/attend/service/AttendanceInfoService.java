package com.scsa.attend.service;

import com.scsa.attend.dto.SuccessResponse;
import com.scsa.attend.dto.ainfo.*;
import com.scsa.attend.exception.InvalidInputException;
import com.scsa.attend.exception.NotFoundException;
import com.scsa.attend.exception.PermissionDeniedException;
import com.scsa.attend.mapper.AttendanceInfoMapper;
import com.scsa.attend.mapper.AttendanceTypeMapper;
import com.scsa.attend.vo.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceInfoService {

    private static final ZoneId KST_ZONE = ZoneId.of("Asia/Seoul");

    private final AttendanceInfoMapper aInfoMapper;
    private final AttendanceTypeMapper aTypeMapper;

    private final UserService userService;
    private final AttendanceTypeService aTypeService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Transactional(readOnly = true)
    public List<AttendanceInfoResponse> findAInfosByCondition(Integer userId, SearchAttendanceInfoRequest request) {
        List<Integer> memIdList = request.getMemIdList();
        if (memIdList == null || memIdList.size() != 1) {
            userService.requireAdmin(userId);
        } else {
            userService.requireAdminOrSelf(userId, memIdList.get(0));
        }
        userService.checkPeriodValidation(request.getStartDate(), request.getEndDate());
        AttendanceInfoSearchCondition condition = request.convertToSearchCondition();
        List<AttendanceFullInfo> aFullInfoList = aInfoMapper.selectAFullInfosByCondition(condition);
        List<AttendanceInfoResponse> responseList = aFullInfoList.stream()
                .map(AttendanceInfoResponse::fromFullInfo)
                .toList();

        return responseList;

    }

    @Transactional(readOnly = true)
    public AttendanceInfoResponse findAFullInfo(Integer userId, Integer aInfoId) {
        AttendanceFullInfo aFullInfo = aInfoMapper.selectAFullInfo(aInfoId);
        checkExistingAFullInfo(aFullInfo);
        Integer memId = aFullInfo.getUser().getId();
        userService.requireAdminOrSelf(userId, memId);
        AttendanceInfoResponse response = AttendanceInfoResponse.fromFullInfo(aFullInfo);
        return response;
    }

    @Transactional
    public AttendanceInfoResponse modifyAInfo(Integer userId, @NotNull Integer aInfoId, @Valid EditAttendanceInfoByAdminRequest updateData) {
        userService.requireAdmin(userId);
        AttendanceFullInfo aFullInfo = aInfoMapper.selectAFullInfo(aInfoId);
        checkExistingAFullInfo(aFullInfo);
        // aFullInfo 객체의 필드를 updateData로 채우고 시간 검증까지 수행
        updateData.updateAFullInfo(aFullInfo);
        checkArrivalLeavingTime(aFullInfo);
        Integer aTypeId = updateData.getATypeId();
        AttendanceType aType = aTypeMapper.selectAType(aTypeId);
        if (aType != null) { // 출석 유형이 실제로 있는 경우만 변경 가능
            aTypeService.checkExistingAType(aType);
        }
        // DB 반영
        aInfoMapper.updateAInfo(aFullInfo.getAttendanceInfo());

        // 수정 된 것 다시 불러오기
        aFullInfo = aInfoMapper.selectAFullInfo(aInfoId);

        // 응답 DTO 변환 및 반환
        return AttendanceInfoResponse.fromFullInfo(aFullInfo);

    }

    @Transactional
    public SuccessResponse modifyAInfoMulti(Integer userId, EditAttendanceInfoMultiRequest request) {
        userService.requireAdmin(userId);
        Integer aTypeId = request.getUpdateData().getATypeId();
        if (aTypeId != null) {
            AttendanceType aType = aTypeMapper.selectAType(aTypeId);
            aTypeService.checkExistingAType(aType);
        }

        int updatedCount = 0;

        for (Integer aInfoId : request.getAInfoIdList()) {
            AttendanceFullInfo aFullInfo = aInfoMapper.selectAFullInfo(aInfoId);

            if (aFullInfo != null) {
                EditAttendanceInfoByAdminRequest updateData = request.getUpdateData();
                updateData.updateAFullInfo(aFullInfo);
                updatedCount += aInfoMapper.updateAInfo(aFullInfo.getAttendanceInfo());

            }
        }

        return new SuccessResponse("출석 정보가 성공적으로 변경되었습니다. (요청 건수 : " + request.getAInfoIdList().size() + ", 성공 건수 : " + updatedCount + "건)");
    }

    public AttendanceFullInfo modifyArrivalTime(Integer userId, Integer aInfoId) {
        AttendanceFullInfo aFullInfo = aInfoMapper.selectAFullInfo(aInfoId);
        checkExistingAFullInfo(aFullInfo);

        userService.requireAdminOrSelf(userId, aFullInfo.getUser().getId());
        LocalDateTime currentDateTime = LocalDateTime.now(KST_ZONE);

        LocalDate aDate = aFullInfo.getAttendanceInfo().getADate();
        String isOff = aFullInfo.getAttendanceInfo().getIsOff();
        checkToday(currentDateTime, aDate, isOff);

        AttendanceType aType = aFullInfo.getAttendanceType();

        String earliestStr = aType.getEarliestTime();
        String endStr = aType.getEndTime();

        LocalTime earliest;
        LocalTime end;
        try {
            earliest = LocalTime.parse(earliestStr);
            end = LocalTime.parse(endStr);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("출석 유형 시간이 유효하지 않은 형식입니다: " + e.getMessage());
        }

        // 5. 서버 시간을 기준으로 완전한 LocalDateTime 생성
        LocalDateTime earliestTime = LocalDateTime.of(aDate, earliest);
        LocalDateTime endTime = LocalDateTime.of(aDate, end);

        // 최종 기록 시간 (서버 시간 사용)
        LocalDateTime recordTime = currentDateTime;

        if (recordTime.isBefore(earliestTime) || !recordTime.isBefore(endTime)) {
            throw new InvalidInputException(
                    String.format("현재 시각(%s)은 출근 인정 시간대 (%s ~ %s)가 아닙니다.",
                            recordTime.toLocalTime(), earliestTime.toLocalTime(), endTime.toLocalTime())
            );
        }

        // 핵심: 최소값 갱신 로직 (DB에서 검증 안 하므로 Service에서 처리)
        LocalDateTime existingArrival = aFullInfo.getAttendanceInfo().getArrivalTime();

        // 기존 출근 시간이 없거나, 현재 기록 시간이 기존 시간보다 빠른 경우에만 업데이트를 진행
        if (existingArrival != null && !recordTime.isBefore(existingArrival)) {
            throw new InvalidInputException("출근 시간이 이미 기록되어 있습니다. 기록 시간 : " + existingArrival);
        }

        aInfoMapper.updateArrivalTime(aInfoId, recordTime);

        AttendanceFullInfo updatedFullInfo = aInfoMapper.selectAFullInfo(aInfoId);

        return updatedFullInfo;

    }

    public AttendanceFullInfo modifyLeavingTime(Integer userId, @NotNull Integer aInfoId) {

        // 1. 기본 정보 조회 및 검증
        AttendanceFullInfo aFullInfo = aInfoMapper.selectAFullInfo(aInfoId);
        checkExistingAFullInfo(aFullInfo);

        // 2. 권한 검증 및 데이터 준비
        userService.requireAdminOrSelf(userId, aFullInfo.getUser().getId());

        // 3. 서버 시간 및 날짜 검증
        LocalDateTime currentDateTime = LocalDateTime.now(KST_ZONE);
        LocalDate aDate = aFullInfo.getAttendanceInfo().getADate();
        String isOff = aFullInfo.getAttendanceInfo().getIsOff();
        checkToday(currentDateTime, aDate, isOff);

        // 4. 출근 기록 존재 여부 및 시간 추출 (퇴근은 출근에 종속)
        LocalDateTime existingArrival = aFullInfo.getAttendanceInfo().getArrivalTime();
        if (existingArrival == null) {
            throw new InvalidInputException("퇴근 기록을 등록하려면 출근 기록(ARRIVAL_TIME)이 먼저 등록되어야 합니다.");
        }

        // 5. 출석 유형 시간 정보 추출 (String)
        AttendanceType aType = aFullInfo.getAttendanceType();
        String latestStr = aType.getLatestTime();

        // 6. LocalTime 파싱
        LocalTime latest;
        try {
            latest = LocalTime.parse(latestStr);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("출석 유형 시간(LatestTime)이 유효하지 않은 형식입니다: " + e.getMessage());
        }

        // 7. LocalDateTime 조합
        LocalDateTime latestTime = LocalDateTime.of(aDate, latest);

        // 최종 기록 시간 (서버 시간 사용)
        LocalDateTime recordTime = currentDateTime;

        //  핵심: 퇴근 시간대 유효성 검증 (arrival < leaving < latest)
        if (recordTime.isBefore(existingArrival) || !recordTime.isBefore(latestTime)) {
            throw new InvalidInputException(
                    String.format("현재 시각(%s)은 퇴근 인정 시간대 (~ %s)가 아닙니다. 퇴근 시간은 출근 시간 이후이고, 마감 시간(%s) 이전이어야 합니다.",
                            recordTime.toLocalTime(), latestTime.toLocalTime(), latestTime.toLocalTime())
            );
        }

        // 핵심: 최대값 갱신 로직 (DB에서 검증 안 하므로 Service에서 처리)
        LocalDateTime existingLeaving = aFullInfo.getAttendanceInfo().getLeavingTime();

        // 기존 퇴근 시간이 존재하고, 현재 기록 시간(recordTime)이 기존 시간보다 늦지 않은 경우
        if (existingLeaving != null && !recordTime.isAfter(existingLeaving)) {
            // 현재 recordTime이 기존 기록보다 빠르거나 같으므로, 업데이트할 필요가 없습니다.
            // 가장 늦은 시간으로 기록해야 하는 요구사항을 충족하지 못합니다.
            throw new InvalidInputException("퇴근 시간이 이미 더 늦은 시간으로 기록되어 갱신하지 않았습니다. 기록 시간 : " + existingLeaving);
        }

        // 10. DB 업데이트 실행 (가장 늦은 시간으로 갱신)
        aInfoMapper.updateLeavingTime(aInfoId, recordTime);

        // 11. 업데이트된 정보 재조회 및 응답 생성
        AttendanceFullInfo updatedFullInfo = aInfoMapper.selectAFullInfo(aInfoId);

        return updatedFullInfo;
    }

    public SuccessResponse calculateAInfoStatus(CalculateAttendanceInfoStatusRequest request) {

        List<Integer> aInfoIdList = request.getAInfoIdList();

        List<AttendanceFullInfo> records = aInfoMapper.selectAFullInfoList(aInfoIdList);

        if (records.isEmpty()) {
            return new SuccessResponse("해당하는 출석 기록이 없습니다. 업데이트 건수: 0");
        }

        // 3. 상태 계산 로직 실행
        for (AttendanceFullInfo fullInfo : records) {
            String newStatus = determineStatus(fullInfo);

            AttendanceInfo info = fullInfo.getAttendanceInfo();
            info.setStatus(newStatus);
            aInfoMapper.updateStatus(info);
        }

        // 5. 결과 반환
        return new SuccessResponse("출석 상태 자동 결정 완료. 업데이트 건수: " + records.size());

    }



    // --------------- 내부 로직 ---------------

    @Transactional(readOnly = true)
    private void checkExistingAFullInfo (AttendanceFullInfo findAFullInfo) {
        if (findAFullInfo == null) {
            throw new NotFoundException("해당하는 출결 정보를 찾을 수 없습니다.");
        }
    }

    /**
     * arrivalTime과 leavingTime에 대한 최소한의 유효성을 검사합니다.
     * 1. Null 종속성: arrival이 null이면 leaving도 반드시 null
     * 2. 시간 순서: arrival < leaving
     * 3. 날짜 일치: arrival/leaving의 날짜는 aDate와 같아야 함 (새로운 조건)
     * @param aFullInfo 업데이트된 AttendanceFullInfo 객체
     */
    private void checkArrivalLeavingTime(AttendanceFullInfo aFullInfo) {

        LocalDateTime arrival = aFullInfo.getAttendanceInfo().getArrivalTime();
        LocalDateTime leaving = aFullInfo.getAttendanceInfo().getLeavingTime();

        LocalDate aDate = aFullInfo.getAttendanceInfo().getADate();

        // 1. Null 종속성 검증 (arrival이 null이면, leaving도 null이어야 함)
        if (arrival == null && leaving != null) {
            throw new InvalidInputException("출근 시간이 기록되지 않은 상태에서 퇴근 시간만 기록될 수 없습니다.");
        }

        // arrival과 leaving이 모두 null이면, 검증할 필요 없음.
        if (arrival == null && leaving == null) {
            return;
        }

        // 3. 날짜 일치 검증

        // arrival 시간이 존재할 경우, 날짜가 기준 날짜와 일치하는지 검증
        if (arrival != null) {
            if (!arrival.toLocalDate().isEqual(aDate)) {
                throw new InvalidInputException("출근일의 날짜는 출석 기준 날짜(" + aDate + ")와 일치해야 합니다.");
            }
        }

        // leaving 시간이 존재할 경우, 날짜가 기준 날짜와 일치하는지 검증
        if (leaving != null) {
            // leaving 시간이 자정을 넘기는 예외 케이스(익일 퇴근)를 고려해야 할 수 있으나, 여기서는 '같아야 한다'는 요구사항에 충실하게 작성합니다.
            if (!leaving.toLocalDate().isEqual(aDate)) {
                throw new InvalidInputException("퇴근일의 날짜는 출석 기준 날짜(" + aDate + ")와 일치해야 합니다.");
            }
        }

        // 2. 시간 순서 검증 (arrival < leaving)
        // 이 시점에서 arrival과 leaving 중 적어도 하나는 non-null임이 보장됨.

        // 둘 다 non-null일 때만 순서 검증
        if (arrival != null && leaving != null) {
            // arrival >= leaving (도착 시간이 퇴근 시간보다 같거나 늦음)
            if (arrival.isAfter(leaving) || arrival.isEqual(leaving)) {
                throw new InvalidInputException("출근 시간은 퇴근 시간보다 같거나 늦을 수 없습니다.");
            }
        }
    }

    public void checkToday(LocalDateTime currentDateTime, LocalDate aDate, String isOff) {
        LocalDate serverToday = currentDateTime.toLocalDate();
        if (!aDate.equals(serverToday)) {
            String errorMessage = String.format(
                    "출석 요청 날짜가 서버의 현재 날짜(%s)와 다릅니다. 요청된 날짜: %s",
                    serverToday, aDate
            );
            throw new InvalidInputException(errorMessage);
        }

        if ("Y".equals(isOff)) {
           throw new InvalidInputException("휴일엔 출결을 등록할 수 없습니다.");
        }

    }

    public String determineStatus(AttendanceFullInfo fullInfo) {

        AttendanceInfo info = fullInfo.getAttendanceInfo();
        AttendanceType type = fullInfo.getAttendanceType();

        LocalDateTime arrival = info.getArrivalTime();
        LocalDateTime leaving = info.getLeavingTime();
        LocalDate aDate = info.getADate();

        LocalTime startTime;
        LocalTime endTime;
        try {
            // String 타입의 시간을 LocalTime으로 파싱
            startTime = LocalTime.parse(type.getStartTime());
            endTime = LocalTime.parse(type.getEndTime());
        } catch (DateTimeParseException e) {
            // 파싱 오류는 시스템 설정 오류로 간주하고 예외 처리
            throw new InvalidInputException("출석 유형의 수업 시작/종료 시간 형식이 유효하지 않습니다: " + e.getMessage());
        }

        // 1. A. 수업 시간대 확정 (LocalTime을 aDate와 결합하여 LocalDateTime으로 변환)
        LocalDateTime lessonStart = LocalDateTime.of(aDate, startTime);
        LocalDateTime lessonEnd = LocalDateTime.of(aDate, endTime);

        // ----------------------------------------------------
        // 2. A. 기록 존재 유무에 따른 명시적 결석 처리 (Nullability)
        // ----------------------------------------------------
        if (arrival == null && leaving == null) {
            return "absent"; // 출퇴근 기록이 모두 없는 경우 (요구사항 1)
        }

        // 3. B. 지각/조퇴 처리 (퇴근 기록 미비)
        if (arrival != null && leaving == null) {
            // 출근만 있고 퇴근이 없으면, 일단 'late/early'로 분류 (부분 참여로 간주)
            // (이 경우, 도착 시간이 시작 시간보다 늦으면 지각으로 간주될 수 있지만,
            // 현재 요구사항은 지각/조퇴를 구분하지 않으므로 부분 참여로 통일)
            return "late/early";
        }

        // ----------------------------------------------------
        // 4. C. 기록은 모두 존재하는 경우 (arrival != null && leaving != null)
        // ----------------------------------------------------

        // 4-1. 완전 결석 조건 (기록은 있으나 수업 시간과 겹치지 않음)
        // (L <= S) OR (E <= A)
        if (leaving.isBefore(lessonStart) || leaving.isEqual(lessonStart) ||
                lessonEnd.isBefore(arrival) || lessonEnd.isEqual(arrival)) {
            return "absent";
        }

        // 4-2. 완전 출석 조건 (수업 시간 전체를 포함)
        // (A <= S) AND (L >= E)
        if ((arrival.isBefore(lessonStart) || arrival.isEqual(lessonStart)) &&
                (leaving.isAfter(lessonEnd) || leaving.isEqual(lessonEnd))) {
            return "normal";
        }

        // 4-3. 부분 포함 조건 (그 외 모든 경우: 겹치기는 하지만 완전히 포함하지는 않음)
        return "late/early";
    }

}
