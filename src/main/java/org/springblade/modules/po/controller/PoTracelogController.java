package org.springblade.modules.po.controller;

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

import org.springblade.modules.po.entity.PoTracelogEntity;
import org.springblade.modules.po.vo.PoTracelogVO;
import org.springblade.modules.po.wrapper.PoTracelogWrapper;
import org.springblade.modules.po.service.IPoTracelogService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-po/potracelog")
@Api(value = "", tags = "")
public class PoTracelogController extends BladeController {

	private IPoTracelogService poTracelogService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入poTracelog")
	public R<PoTracelogEntity> detail(PoTracelogEntity poTracelog) {
		PoTracelogEntity detail = poTracelogService.getOne(Condition.getQueryWrapper(poTracelog));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入poTracelog")
	public R<IPage<PoTracelogEntity>> list(PoTracelogEntity poTracelog, Query query) {
		IPage<PoTracelogEntity> pages = poTracelogService.page(Condition.getPage(query), Condition.getQueryWrapper(poTracelog));
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入poTracelog")
	public R save(@Valid @RequestBody PoTracelogEntity poTracelog) {
		return R.status(poTracelogService.save(poTracelog));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入poTracelog")
	public R update(@Valid @RequestBody PoTracelogEntity poTracelog) {
		return R.status(poTracelogService.updateById(poTracelog));
	}

	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(poTracelogService.deleteLogic(Func.toLongList(ids)));
	}

}
