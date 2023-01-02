package org.springblade.modules.material.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.common.constant.CommonConstant;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.material.entity.MaterialEntity;
import org.springblade.modules.material.vo.MaterialVO;
import org.springblade.modules.material.wrapper.MaterialWrapper;
import org.springblade.modules.material.service.IMaterialService;


/**
 * 原材料尺寸表 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-material/material")
@Api(value = "原材料尺寸表", tags = "原材料尺寸表")
public class MaterialController extends BladeController {

	private IMaterialService materialService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入material")
	public R<MaterialEntity> detail(MaterialEntity material) {
		MaterialEntity detail = materialService.getOne(Condition.getQueryWrapper(material));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入material")
	public R<IPage<MaterialEntity>> list(MaterialEntity material, Query query) {
		IPage<MaterialEntity> pages = materialService.page(Condition.getPage(query), Condition.getQueryWrapper(material));
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入material")
	public R save(@Valid @RequestBody MaterialEntity material) {
		return R.status(materialService.save(material));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入material")
	public R update(@Valid @RequestBody MaterialEntity material) {
		return R.status(materialService.updateById(material));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入material")
	public R submit(@Valid @RequestBody MaterialEntity material) {
		return R.status(materialService.saveOrUpdate(material));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(materialService.deleteLogic(Func.toLongList(ids)));
	}

}
