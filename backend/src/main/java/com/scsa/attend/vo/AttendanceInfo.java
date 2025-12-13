package com.scsa.attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceInfo {
    private Integer id;
    private LocalDate aDate;
    private String isOff;
    private Integer memId;
    private Integer aTypeId;
    private LocalDateTime arrivalTime;
    private LocalDateTime leavingTime;
    private String status;
    private String docPath;
    private String isApproved;
    private String isOfficial;
    private String memNote;
    private String adminNote;

}
