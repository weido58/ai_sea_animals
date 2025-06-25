package com.gec.marine.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.marine.dto.UserQueryDTO;
import com.gec.marine.entity.PageResult;
import com.gec.marine.entity.Result;
import com.gec.marine.entity.SysUser;
import com.gec.marine.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private SysUserService sysUserService;
    @GetMapping("/doUserList")
    public Result<List<SysUser>> getAllUsers(){
        List<SysUser> sysUserList = sysUserService.list();
        return Result.ok(sysUserList);
    }


    // http:/xxx/1
    // http:/xxx/2
    // http:/xxx/3
    // 根据 ID 查询用户
    @GetMapping("/doFindUserById/{id}")
    public Result<SysUser> getUserById(@PathVariable("id") Long id)
    {
        SysUser sysUser = sysUserService.getById(id);
        return Result.ok(sysUser);
    }

    // 添加用户
    @PostMapping("/doUserAddSave")
    public Result addUser(@RequestBody SysUser sysUser){

        boolean flag = sysUserService.save(sysUser);
        if(flag){
            return Result.ok();
        }else
            return Result.failed("添加用户数据失败");
    }

    //删除用户
    @DeleteMapping("/doUserRemove/{id}")
    public Result deleteUser(@PathVariable("id") Long id)
    {
        boolean flag = sysUserService.removeById(id);
        if(flag){
            return Result.ok();
        }else
            return Result.failed("删除用户数据失败");
    }

    @PutMapping("/doUserEditSave")
    //更新操作
    public Result updateUser(@RequestBody SysUser sysUser){
        boolean flag = sysUserService.updateById(sysUser);
        if(flag){
            return Result.ok();
        }else
            return Result.failed("更新用户失败");
    }

    /**
     * 分页查询用户列表（支持用户名、角色、状态条件查询）
     */
    @GetMapping("/doUserListByPage")
    public Result<PageResult<SysUser>> doUserListByPage(UserQueryDTO queryDTO) {
        try {
            PageResult<SysUser> pageResult = sysUserService.userListByPage(queryDTO);
            return Result.ok(pageResult);
        } catch (Exception e) {
            return Result.failed("查询失败：" + e.getMessage());
        }
    }
}
