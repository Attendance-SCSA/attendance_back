package com.scsa.attend.dto.ainfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("aInfoIdList") // JSON 키 이름이 "aInfoIdList"임을 Jackson에게 명확히 알려줍니다. (jackson 필드 명명 규칙 이슈)
    private List<Integer> aInfoIdList;

    // 이 요청의 updateData를 파싱할 때, arrivalTime과 leavingTime 필드는 무시하도록 강제
    // @JsonIgnoreProperties({"arrivalTime", "leavingTime"}) // "arrivalTime", "leavingTime"은 다건 변경 요청으로는 변경 불가
    // 출퇴근 기록 시간도 변경 가능 필요
    @NotNull
    private EditAttendanceInfoByAdminRequest updateData;
}
