package com.scsa.attend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String loginId;
    private String loginPwd;
    private String name;
    private String company;
    private String role;
    private Date startDay;
    private Date endDay;
}
