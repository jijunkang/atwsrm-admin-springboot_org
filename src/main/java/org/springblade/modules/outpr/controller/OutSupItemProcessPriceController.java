package org.springblade.modules.outpr.controller;

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

import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.vo.OutSupItemProcessPriceVO;
import org.springblade.modules.outpr.wrapper.OutSupItemProcessPriceWrapper;
import org.springblade.modules.outpr.service.IOutSupItemProcessPriceService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-outpr/outsupitemprocessprice")
@Api(value = "", tags = "")
public class OutSupItemProcessPriceController extends BladeController {

	private IOutSupItemProcessPriceService outsupitemprocesspriceService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入outsupitemprocessprice")
	public R<OutSupItemProcessPriceEntity> detail(OutSupItemProcessPriceEntity outsupitemprocessprice) {
		OutSupItemProcessPriceEntity detail = outsupitemprocesspriceService.getOne(Condition.getQueryWrapper(outsupitemprocessprice));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入outsupitemprocessprice")
	public R<IPage<OutSupItemProcessPriceEntity>> page(OutSupItemProcessPriceEntity outsupitemprocessprice, Query query) {
		IPage<OutSupItemProcessPriceEntity> pages = outsupitemprocesspriceService.page(Condition.getPage(query), Condition.getQueryWrapper(outsupitemprocessprice).orderByDesc("update_time"));
		return R.data(pages);
	}
//
//	/**
//	 * 新增 代码自定义代号
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入outsupitemprocessprice")
//	public R save(@Valid @RequestBody OutSupItemProcessPriceEntity outsupitemprocessprice) {
//		return R.status(outsupitemprocesspriceService.save(outsupitemprocessprice));
//	}
//
//	/**
//	 * 修改 代码自定义代号
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入outsupitemprocessprice")
//	public R update(@Valid @RequestBody OutSupItemProcessPriceEntity outsupitemprocessprice) {
//		return R.status(outsupitemprocesspriceService.updateById(outsupitemprocessprice));
//	}
//
//	/**
//	 * 新增或修改 代码自定义代号
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入outsupitemprocessprice")
//	public R submit(@Valid @RequestBody OutSupItemProcessPriceEntity outsupitemprocessprice) {
//		return R.status(outsupitemprocesspriceService.saveOrUpdate(outsupitemprocessprice));
//	}
//
//
//	/**
//	 * 删除 代码自定义代号
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 7)
//	@ApiOperation(value = "逻辑删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(outsupitemprocesspriceService.deleteLogic(Func.toLongList(ids)));
//	}

}
