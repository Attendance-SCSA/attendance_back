package com.scsa.attend.dto.ainfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.scsa.attend.exception.InvalidInputException;
import com.scsa.attend.vo.AttendanceFullInfo;
import com.scsa.attend.vo.AttendanceInfo;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EditAttendanceInfoByAdminRequest {

    private String isOff;
    @JsonProperty("aTypeId")
    private Integer aTypeId;

    // NULL 허용 + presence flag
    private LocalDateTime arrivalTime;
    private boolean arrivalTimePresent;

    private LocalDateTime leavingTime;
    private boolean leavingTimePresent;

    private String status;
    private boolean statusPresent;

    private String isApproved;
    private boolean isApprovedPresent;

    private String isOfficial;
    private boolean isOfficialPresent;

    private String adminNote;
    private boolean adminNotePresent;

    // ---------------- setters ----------------

    @JsonSetter("isOff")
    public void setIsOff(String isOff) {
        if (!isOff.matches("^[YNyn]$")) {
            throw new InvalidInputException("휴무 상태 값이 유효하지 않습니다.");
        }
        this.isOff = isOff;
    }

    @JsonSetter("aTypeId")
    public void setATypeId(Integer aTypeId) {
        if (aTypeId <= 0) { // 예시: ID는 양수여야 함
            throw new InvalidInputException("aTypeId는 유효한 값이어야 합니다.");
        }
        this.aTypeId = aTypeId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSetter("arrivalTime")
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
        this.arrivalTimePresent = true;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSetter("leavingTime")
    public void setLeavingTime(LocalDateTime leavingTime) {
        this.leavingTime = leavingTime;
        this.leavingTimePresent = true;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        if (status != null && !status.matches("^(late/early|absent|normal)$")) {
            throw new InvalidInputException("출석 상태 값이 유효하지 않습니다.");
        }
        this.status = status;
        this.statusPresent = true;
    }

    @JsonSetter("isApproved")
    public void setIsApproved(String isApproved) {
        if (isApproved != null && !isApproved.matches("^(denied|approved)$")) {
            throw new InvalidInputException("승인 여부 값이 유효하지 않습니다.");
        }
        this.isApproved = isApproved;
        this.isApprovedPresent = true;
    }

    @JsonSetter("isOfficial")
    public void setIsOfficial(String isOfficial) {
        if (isOfficial != null && !isOfficial.matches("^[YNyn]$")) {
            throw new InvalidInputException("공결 여부 값이 유효하지 않습니다.");
        }
        this.isOfficial = isOfficial;
        this.isOfficialPresent = true;
    }

    @JsonSetter("adminNote")
    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
        this.adminNotePresent = true;
    }

    public void updateAFullInfo(AttendanceFullInfo aFullInfo) {
        AttendanceInfo info = aFullInfo.getAttendanceInfo();

        if (isOff != null) info.setIsOff(isOff);
        if (aTypeId != null) info.setATypeId(aTypeId);
        if (arrivalTimePresent) info.setArrivalTime(arrivalTime);
        if (leavingTimePresent) info.setLeavingTime(leavingTime);
        if (statusPresent) info.setStatus(status);
        if (isApprovedPresent) info.setIsApproved(isApproved);
        if (isOfficialPresent) info.setIsOfficial(isOfficial);
        if (adminNotePresent) info.setAdminNote(adminNote);

    }

}
