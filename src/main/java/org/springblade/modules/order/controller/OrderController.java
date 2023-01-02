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
package org.springblade.modules.order.controller;

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
import org.springblade.modules.order.entity.Order;
import org.springblade.modules.order.vo.OrderVO;
import org.springblade.modules.order.wrapper.OrderWrapper;
import org.springblade.modules.order.service.IOrderService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author xianboss
 * @since 2019-10-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("order/order")
@Api(value = "", tags = "接口")
public class OrderController extends BladeController {

	private IOrderService orderService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入order")
	public R<OrderVO> detail(Order order) {
		Order detail = orderService.getOne(Condition.getQueryWrapper(order));
		return R.data(OrderWrapper.build().entityVO(detail));
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入order")
	public R<IPage<OrderVO>> list(Order order, Query query) {
		IPage<Order> pages = orderService.page(Condition.getPage(query), Condition.getQueryWrapper(order));
		return R.data(OrderWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入order")
	public R<IPage<OrderVO>> page(OrderVO order, Query query) {
		IPage<OrderVO> pages = orderService.selectOrderPage(Condition.getPage(query), order);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入order")
	public R save(@Valid @RequestBody Order order) {
		return R.status(orderService.save(order));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入order")
	public R update(@Valid @RequestBody Order order) {
		return R.status(orderService.updateById(order));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入order")
	public R submit(@Valid @RequestBody Order order) {
		return R.status(orderService.saveOrUpdate(order));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(orderService.deleteLogic(Func.toLongList(ids)));
	}


}
