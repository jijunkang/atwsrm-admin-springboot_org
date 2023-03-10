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

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.system.entity.Menu;
import org.springblade.modules.system.entity.TopMenu;
import org.springblade.modules.system.service.IMenuService;
import org.springblade.modules.system.service.ITopMenuService;
import org.springblade.modules.system.vo.CheckedTreeVO;
import org.springblade.modules.system.vo.GrantTreeVO;
import org.springblade.modules.system.vo.MenuVO;
import org.springblade.modules.system.wrapper.MenuWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.MENU_CACHE;

/**
 * ?????????
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping(AppConstant.APPLICATION_SYSTEM_NAME + "/menu")
@Api(value = "??????", tags = "??????")
public class MenuController extends BladeController {

    private IMenuService menuService;
    private ITopMenuService topMenuService;

    /**
     * ??????
     */
    @GetMapping("/detail")
    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "??????", notes = "??????menu")
    public R<MenuVO> detail(Menu menu) {
        Menu detail = menuService.getOne(Condition.getQueryWrapper(menu));
        return R.data(MenuWrapper.build().entityVO(detail));
    }

    /**
     * ??????
     */
    @GetMapping("/list")
    @ApiImplicitParams({
                               @ApiImplicitParam(name = "code", value = "????????????", paramType = "query", dataType = "string"),
                               @ApiImplicitParam(name = "name", value = "????????????", paramType = "query", dataType = "string")
                       })
    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "??????menu")
    public R<List<MenuVO>> list(@ApiIgnore @RequestParam Map<String, Object> menu) {
        List<Menu> list = menuService.list(Condition.getQueryWrapper(menu, Menu.class).lambda().orderByAsc(Menu::getSort));
        return R.data(MenuWrapper.build().listNodeVO(list));
    }

    /**
     * ??????
     */
    @GetMapping("/menu-list")
    @ApiImplicitParams({
                               @ApiImplicitParam(name = "code", value = "????????????", paramType = "query", dataType = "string"),
                               @ApiImplicitParam(name = "name", value = "????????????", paramType = "query", dataType = "string")
                       })
    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "??????menu")
    public R<List<MenuVO>> menuList(@ApiIgnore @RequestParam Map<String, Object> menu) {
        List<Menu> list = menuService.list(Condition.getQueryWrapper(menu, Menu.class).lambda()
//                                                    .eq(Menu::getAlias, "menu")
                                                    .eq(Menu::getCategory, 1)
                                                    .orderByAsc(Menu::getSort));
        return R.data(MenuWrapper.build().listNodeVO(list));
    }

    /**
     * ???????????????
     */
    @PostMapping("/submit")
    @CacheEvict(cacheNames = {MENU_CACHE}, allEntries = true)
    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "???????????????", notes = "??????menu")
    public R submit(@Valid @RequestBody Menu menu) {
        return R.status(menuService.submit(menu));
    }


    /**
     * ??????
     */
    @PostMapping("/remove")
    @CacheEvict(cacheNames = {MENU_CACHE}, allEntries = true)
    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "??????", notes = "??????ids")
    public R remove(@ApiParam(value = "????????????", required = true) @RequestParam String ids) {
        return R.status(menuService.removeMenu(ids));
    }

    /**
     * ??????????????????
     */
    @GetMapping("/routes")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<List<MenuVO>> routes(BladeUser user, Long topMenuId) {
        List<MenuVO> list = menuService.routes((user == null) ? null : user.getRoleId(), topMenuId);
        return R.data(list);
    }

    /**
     * ??????????????????
     */
    @GetMapping("/routes-ext")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<List<MenuVO>> routesExt(BladeUser user, Long topMenuId) {
        List<MenuVO> list = menuService.routesExt(user.getRoleId(), topMenuId);
        return R.data(list);
    }

    /**
     * ??????????????????
     */
    @GetMapping("/buttons")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<List<MenuVO>> buttons(BladeUser user) {
        List<MenuVO> list = menuService.buttons(user.getRoleId());
        return R.data(list);
    }

    /**
     * ????????????????????????
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "????????????", notes = "????????????")
    public R<List<MenuVO>> tree() {
        List<MenuVO> tree = menuService.tree();
        return R.data(tree);
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("/grant-tree")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R<GrantTreeVO> grantTree(BladeUser user) {
        GrantTreeVO vo = new GrantTreeVO();
        vo.setMenu(menuService.grantTree(user));
        vo.setDataScope(menuService.grantDataScopeTree(user));
        vo.setApiScope(menuService.grantApiScopeTree(user));
        return R.data(vo);
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("/role-tree-keys")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    public R<CheckedTreeVO> roleTreeKeys(String roleIds) {
        CheckedTreeVO vo = new CheckedTreeVO();
        vo.setMenu(menuService.roleTreeKeys(roleIds));
        vo.setDataScope(menuService.dataScopeTreeKeys(roleIds));
        vo.setApiScope(menuService.apiScopeTreeKeys(roleIds));
        return R.data(vo);
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("/grant-top-tree")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R<GrantTreeVO> grantTopTree(BladeUser user) {
        GrantTreeVO vo = new GrantTreeVO();
        vo.setMenu(menuService.grantTopTree(user));
        return R.data(vo);
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("/top-tree-keys")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
    public R<CheckedTreeVO> topTreeKeys(String topMenuIds) {
        CheckedTreeVO vo = new CheckedTreeVO();
        vo.setMenu(menuService.topTreeKeys(topMenuIds));
        return R.data(vo);
    }

    /**
     * ??????????????????
     */
    @GetMapping("/top-menu")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<List<TopMenu>> topMenu(BladeUser user) {
        if (Func.isEmpty(user)) {
            return null;
        }
        List<TopMenu> list = topMenuService.list();
        return R.data(list);
    }

    /**
     * ???????????????????????????
     */
    @GetMapping("auth-routes")
    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "?????????????????????")
    public R<List<Kv>> authRoutes(BladeUser user) {
        if (Func.isEmpty(user)) {
            return null;
        }
        return R.data(menuService.authRoutes(user));
    }

}
