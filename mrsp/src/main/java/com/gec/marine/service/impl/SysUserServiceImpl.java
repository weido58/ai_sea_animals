package com.gec.marine.service.impl;

import com.gec.marine.mapper.SysUserMapper;
import com.gec.marine.entity.SysUser;
import com.gec.marine.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    // 可以在此添加自定义的业务逻辑
}
