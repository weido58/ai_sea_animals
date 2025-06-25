package com.gec.marine.service;

import com.gec.marine.dto.UserQueryDTO;
import com.gec.marine.entity.PageResult;
import com.gec.marine.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserService extends IService<SysUser> {

    public PageResult<SysUser> userListByPage(UserQueryDTO queryDTO);

}
