package com.scsa.attend.dto.ainfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAttendanceInfoMultiRequest {
    @NotEmpty
    private List<Integer> memIdList;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull
    private LocalDate endDate;

    // 이 요청의 updateData를 파싱할 때, arrivalTime과 leavingTime 필드는 무시하도록 강제
    @JsonIgnoreProperties({"arrivalTime", "leavingTime"}) // "arrivalTime", "leavingTime"은 다건 변경 요청으로는 변경 불가
    @NotNull
    private EditAttendanceInfoByAdminRequest updateData;
}
