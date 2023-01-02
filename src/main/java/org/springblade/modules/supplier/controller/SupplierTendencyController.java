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
package org.springblade.modules.supplier.controller;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.modules.supplier.entity.SupplierTendency;
import org.springblade.modules.supplier.vo.SupplierTendencyVO;
import org.springblade.modules.supplier.wrapper.SupplierTendencyWrapper;
import org.springblade.modules.supplier.service.ISupplierTendencyService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.Map;

/**
 * 供应商动态信息 控制器
 *
 * @author Blade
 * @since 2019-11-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-supplier-tendency/suppliertendency")
@Api(value = "供应商动态信息", tags = "供应商动态信息接口")
public class SupplierTendencyController extends BladeController {

	private ISupplierTendencyService supplierTendencyService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入supplierTendency")
	public R<SupplierTendencyVO> detail(SupplierTendency supplierTendency) {
		SupplierTendency detail = supplierTendencyService.getOne(Condition.getQueryWrapper(supplierTendency));
		return R.data(SupplierTendencyWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 供应商动态信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入supplierTendency")
	public R<IPage<SupplierTendencyVO>> page(SupplierTendency supplierTendency, Query query) {
		IPage<SupplierTendency> pages = supplierTendencyService.page(Condition.getPage(query), Condition.getQueryWrapper(supplierTendency));
		return R.data(SupplierTendencyWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 供应商动态信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入supplierTendency")
	public R<IPage<Map<String,Object>>> list( SupplierTendency supplierTendency, Query query) {
		IPage<Map<String,Object>> pages = supplierTendencyService.selectSupplierTendencyPage(Condition.getPage(query), supplierTendency);
		return R.data(pages);
	}

	/**
	 * 新增 供应商动态信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入supplierTendency")
	public R save(@Valid @RequestBody SupplierTendency supplierTendency) {
		return R.status(supplierTendencyService.save(supplierTendency));
	}

	/**
	 * 修改 供应商动态信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入supplierTendency")
	public R update(@Valid @RequestBody SupplierTendency supplierTendency) {
		return R.status(supplierTendencyService.updateById(supplierTendency));
	}

	/**
	 * 新增或修改 供应商动态信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入supplierTendency")
	public R submit(@Valid @RequestBody SupplierTendency supplierTendency) {
		return R.status(supplierTendencyService.saveOrUpdate(supplierTendency));
	}


	/**
	 * 删除 供应商动态信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(supplierTendencyService.deleteLogic(Func.toLongList(ids)));
	}


}
