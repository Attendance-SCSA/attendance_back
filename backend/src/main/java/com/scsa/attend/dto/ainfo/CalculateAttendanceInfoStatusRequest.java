package com.scsa.attend.dto.ainfo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateAttendanceInfoStatusRequest {
    @NotNull
    private LocalDate targetDate;
}
