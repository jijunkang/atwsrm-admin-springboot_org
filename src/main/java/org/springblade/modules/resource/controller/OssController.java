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
package org.springblade.modules.resource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.resource.builder.OssBuilder;
import org.springblade.modules.resource.entity.Oss;
import org.springblade.modules.resource.entity.OssVO;
import org.springblade.modules.resource.service.IOssService;
import org.springblade.modules.resource.wrapper.OssWrapper;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * ?????????
 *
 * @author xianboss
 * @since 2019-05-26
 */
@RestController
@AllArgsConstructor
@RequestMapping(AppConstant.APPLICATION_RESOURCE_NAME + "/oss")
@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
@ApiIgnore
@Api(value = "??????????????????", tags = "??????????????????")
public class OssController extends BladeController {

	private IOssService ossService;

	/**
	 * ??????
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "??????", notes = "??????oss")
	public R<OssVO> detail(Oss oss) {
		Oss detail = ossService.getOne(Condition.getQueryWrapper(oss));
		return R.data(OssWrapper.build().entityVO(detail));
	}

	/**
	 * ??????
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "??????", notes = "??????oss")
	public R<IPage<OssVO>> list(Oss oss, Query query) {
		IPage<Oss> pages = ossService.page(Condition.getPage(query), Condition.getQueryWrapper(oss));
		return R.data(OssWrapper.build().pageVO(pages));
	}

	/**
	 * ???????????????
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "??????", notes = "??????oss")
	public R<IPage<OssVO>> page(OssVO oss, Query query) {
		IPage<OssVO> pages = ossService.selectOssPage(Condition.getPage(query), oss);
		return R.data(pages);
	}

	/**
	 * ??????
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "??????", notes = "??????oss")
	public R save(@Valid @RequestBody Oss oss) {
		return R.status(ossService.save(oss));
	}

	/**
	 * ??????
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "??????", notes = "??????oss")
	public R update(@Valid @RequestBody Oss oss) {
		return R.status(ossService.updateById(oss));
	}

	/**
	 * ???????????????
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "???????????????", notes = "??????oss")
	public R submit(@Valid @RequestBody Oss oss) {
		return R.status(ossService.saveOrUpdate(oss));
	}


	/**
	 * ??????
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "????????????", notes = "??????ids")
	public R remove(@ApiParam(value = "????????????", required = true) @RequestParam String ids) {
		return R.status(ossService.deleteLogic(Func.toLongList(ids)));
	}


	/**
	 * ??????
	 */
	@PostMapping("/enable")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "????????????", notes = "??????id")
	public R enable(@ApiParam(value = "??????", required = true) @RequestParam Long id) {
		CacheUtil.evict(SYS_CACHE, OssBuilder.OSS_CODE, SecureUtil.getTenantId());
		return R.status(ossService.enable(id));
	}

}
