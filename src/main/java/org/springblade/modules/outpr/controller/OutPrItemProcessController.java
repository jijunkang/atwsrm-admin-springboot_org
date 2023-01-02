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

import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.vo.OutPrItemProcessVO;
import org.springblade.modules.outpr.wrapper.OutPrItemProcessWrapper;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-outpr/outpritemprocess")
@Api(value = "", tags = "")
public class OutPrItemProcessController extends BladeController {

	private IOutPrItemProcessService outpritemprocessService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入outpritemprocess")
	public R<OutPrItemProcessEntity> detail(OutPrItemProcessEntity outpritemprocess) {
		OutPrItemProcessEntity detail = outpritemprocessService.getOne(Condition.getQueryWrapper(outpritemprocess));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入outpritemprocess")
	public R<IPage<OutPrItemProcessEntity>> page(OutPrItemProcessEntity outpritemprocess, Query query) {
		IPage<OutPrItemProcessEntity> pages = outpritemprocessService.page(Condition.getPage(query), Condition.getQueryWrapper(outpritemprocess).orderByDesc("update_time"));
		return R.data(pages);
	}

//	/**
//	 * 新增 代码自定义代号
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入outpritemprocess")
//	public R save(@Valid @RequestBody OutPrItemProcessEntity outpritemprocess) {
//		return R.status(outpritemprocessService.save(outpritemprocess));
//	}
//
//	/**
//	 * 修改 代码自定义代号
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入outpritemprocess")
//	public R update(@Valid @RequestBody OutPrItemProcessEntity outpritemprocess) {
//		return R.status(outpritemprocessService.updateById(outpritemprocess));
//	}
//
//	/**
//	 * 新增或修改 代码自定义代号
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入outpritemprocess")
//	public R submit(@Valid @RequestBody OutPrItemProcessEntity outpritemprocess) {
//		return R.status(outpritemprocessService.saveOrUpdate(outpritemprocess));
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
//		return R.status(outpritemprocessService.deleteLogic(Func.toLongList(ids)));
//	}

}
