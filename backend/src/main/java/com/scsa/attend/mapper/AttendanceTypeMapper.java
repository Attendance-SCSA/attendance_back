package com.scsa.attend.mapper;

import com.scsa.attend.vo.AttendanceType;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttendanceTypeMapper {

    List<AttendanceType> selectAllATypes();
    void insertAType(AttendanceType aType);
    AttendanceType selectAType(@Param("aTypeId") Integer aTypeId);
    void updateAType(AttendanceType aType);
    void deleteAType(@NotNull Integer aTypeId);

    // ----- 내부 로직
    AttendanceType selectATypeByName(String name);


}
