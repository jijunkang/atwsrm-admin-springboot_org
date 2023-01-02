package org.springblade.modules.brand.controller;

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

import org.springblade.modules.brand.entity.BrandEntity;
import org.springblade.modules.brand.vo.BrandVO;
import org.springblade.modules.brand.wrapper.BrandWrapper;
import org.springblade.modules.brand.service.IBrandService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-brand/brand")
@Api(value = "", tags = "")
public class BrandController extends BladeController {

	private IBrandService brandService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入brand")
	public R<BrandEntity> detail(BrandEntity brand) {
		BrandEntity detail = brandService.getOne(Condition.getQueryWrapper(brand));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入brand")
	public R<IPage<BrandEntity>> list(BrandEntity brand, Query query) {
		IPage<BrandEntity> pages = brandService.page(Condition.getPage(query), Condition.getQueryWrapper(brand));
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入brand")
	public R save(@Valid @RequestBody BrandEntity brand) {
		return R.status(brandService.save(brand));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入brand")
	public R update(@Valid @RequestBody BrandEntity brand) {
		return R.status(brandService.updateById(brand));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入brand")
	public R submit(@Valid @RequestBody BrandEntity brand) {
		return R.status(brandService.saveOrUpdate(brand));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(brandService.deleteLogic(Func.toLongList(ids)));
	}

}
