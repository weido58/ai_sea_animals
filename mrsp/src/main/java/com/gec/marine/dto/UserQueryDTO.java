package com.gec.marine.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private String username;    // 用户名查询条件
    private String role;        // 角色查询条件
    private Integer status;     // 状态查询条件
    private Integer pageNum = 1;    // 当前页码，默认第1页
    private Integer pageSize = 10;  // 每页显示数量，默认10条
}
