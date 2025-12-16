package com.scsa.attend.dto.ainfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateAttendanceInfoStatusRequest {
    @NotEmpty
    @JsonProperty("aInfoIdList") // JSON 키 이름이 "aInfoIdList"임을 Jackson에게 명확히 알려줍니다. (jackson 필드 명명 규칙 이슈)
    private List<Integer> aInfoIdList;
}
