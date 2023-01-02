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
package org.springblade.modules.supitem.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.pr.service.IU9PrService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.modules.supitem.entity.SupItem;
import org.springblade.modules.supitem.vo.SupItemVO;
import org.springblade.modules.supitem.wrapper.SupItemWrapper;
import org.springblade.modules.supitem.service.ISupItemService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author Blade
 * @since 2019-12-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-supitem/supitem")
@Api(value = "", tags = "接口")
public class SupItemController extends BladeController {

	private ISupItemService supItemService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入supItem")
	public R<SupItemVO> detail(SupItem supItem) {
		SupItem detail = supItemService.getOne(Condition.getQueryWrapper(supItem));
		return R.data(SupItemWrapper.build().entityVO(detail));
	}


    /**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入supItem")
	public R<IPage<SupItemVO>> list(SupItem supItem, Query query) {
		IPage<SupItem> pages = supItemService.page(Condition.getPage(query), Condition.getQueryWrapper(supItem));
		return R.data(SupItemWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入supItem")
	public R<IPage<SupItemVO>> page(SupItemVO supItem, Query query) {
		IPage<SupItemVO> pages = supItemService.selectSupItemPage(Condition.getPage(query), supItem);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入supItem")
	public R save(@Valid @RequestBody SupItem supItem) {
		return R.status(supItemService.save(supItem));
	}



	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入supItem")
	public R update(@Valid @RequestBody SupItem supItem) {
		return R.status(supItemService.updateById(supItem));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入supItem")
	public R submit(@Valid @RequestBody SupItem supItem) {
		return R.status(supItemService.saveOrUpdate(supItem));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(supItemService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 导入
	 */
	@PostMapping("/importexcel")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "导入", notes = "MultipartFile")
	public
	R importExcel(@Valid @RequestParam MultipartFile file){
		try{
			return R.status(supItemService.importExcel(file));
		}catch(Exception e){
			e.printStackTrace();
			return R.fail("读取excel文件失败");
		}
	}
}
