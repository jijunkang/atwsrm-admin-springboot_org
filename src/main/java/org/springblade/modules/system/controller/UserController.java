/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author:  William Wang (wxx@idwsoft.com)
 */
package org.springblade.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IUserService;
import org.springblade.modules.system.vo.UserVO;
import org.springblade.modules.system.wrapper.UserWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.USER_CACHE;

/**
 * ?????????
 *
 * @author Will
 */
@ApiIgnore
@RestController
@RequestMapping(AppConstant.APPLICATION_USER_NAME)
@AllArgsConstructor
public class UserController {

    private IUserService userService;

    /**
     * ????????????
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "????????????", notes = "??????id")
    @GetMapping("/detail")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R<UserVO> detail(User user) {
        User detail = userService.getOne(Condition.getQueryWrapper(user));
        return R.data(UserWrapper.build().entityVO(detail));
    }

    /**
     * ????????????
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "????????????", notes = "??????id")
    @GetMapping("/info")
    public R<UserVO> info(BladeUser user) {
        User detail = userService.getById(user.getUserId());
        return R.data(UserWrapper.build().entityVO(detail));
    }

    /**
     * ????????????
     */
    @GetMapping("/list")
    @ApiImplicitParams({
                               @ApiImplicitParam(name = "account", value = "?????????", paramType = "query", dataType = "string"),
                               @ApiImplicitParam(name = "realName", value = "??????", paramType = "query", dataType = "string")
                       })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "??????", notes = "??????account???realName")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R<IPage<UserVO>> list(@ApiIgnore @RequestParam Map<String, Object> user, Query query, BladeUser bladeUser) {
        QueryWrapper<User> queryWrapper = Condition.getQueryWrapper(user, User.class);
        IPage<User> pages = userService.page(Condition.getPage(query), (!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(User::getTenantId, bladeUser.getTenantId()) : queryWrapper);
        return R.data(UserWrapper.build().pageVO(pages));
    }

    /**
     * ???????????????
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "???????????????", notes = "??????User")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @CacheEvict(cacheNames = {USER_CACHE}, allEntries = true)
    public R submit(@Valid @RequestBody User user) {
        return R.status(userService.submit(user));
    }

    /**
     * ??????
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "??????", notes = "??????User")
    @CacheEvict(cacheNames = {USER_CACHE}, allEntries = true)
    public R update(@Valid @RequestBody User user) {
        return R.status(userService.updateUser(user));
    }

    /**
     * ??????
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "??????", notes = "??????id??????")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @CacheEvict(cacheNames = {USER_CACHE}, allEntries = true)
    public R remove(@RequestParam String ids) {
        return R.status(userService.removeUser(ids));
    }

    /**
     * ??????????????????
     *
     * @param userIds
     * @param roleIds
     * @return
     */
    @PostMapping("/grant")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "????????????", notes = "??????roleId????????????menuId??????")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R grant(@ApiParam(value = "userId??????", required = true) @RequestParam String userIds,
            @ApiParam(value = "roleId??????", required = true) @RequestParam String roleIds) {
        boolean temp = userService.grant(userIds, roleIds);
        return R.status(temp);
    }

    /**
     * ????????????
     *
     * @param userIds
     * @return
     */
    @PostMapping("/reset-password")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "???????????????", notes = "??????userId??????")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R resetPassword(@ApiParam(value = "userId??????", required = true) @RequestParam String userIds) {
        boolean temp = userService.resetPassword(userIds);
        return R.status(temp);
    }

    /**
     * ????????????
     *
     * @param oldPassword
     * @param newPassword
     * @param newPassword1
     * @return
     */
    @PostMapping("/update-password")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "????????????", notes = "????????????")
    public R updatePassword(BladeUser user, @ApiParam(value = "?????????", required = true) @RequestParam String oldPassword,
            @ApiParam(value = "?????????", required = true) @RequestParam String newPassword,
            @ApiParam(value = "?????????", required = true) @RequestParam String newPassword1) {
        boolean temp = userService.updatePassword(user.getUserId(), oldPassword, newPassword, newPassword1);
        return R.status(temp);
    }

    /**
     * ????????????
     *
     * @param user
     * @return
     */
    @GetMapping("/user-list")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "????????????", notes = "??????user")
    public R<List<User>> userList(User user){
        String roleId = user.getRoleId();
        user.setRoleId(null);
        QueryWrapper<User> queryWraper = Condition.getQueryWrapper(user).like(StringUtils.isNoneBlank(roleId), "role_id", "%" + roleId + "%").eq("is_deleted",0);
        List<User>         list        = userService.list(queryWraper);
        return R.data(list);
    }


}
