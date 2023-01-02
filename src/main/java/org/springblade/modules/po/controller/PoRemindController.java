package org.springblade.modules.po.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springblade.common.dto.CheckDTO;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.po.dto.PoRemindApplyDTO;
import org.springblade.modules.po.dto.PoRemindDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.po.entity.PoRemindEntity;
import org.springblade.modules.po.service.IPoRemindService;

import java.util.List;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-po/poremind")
@Api(value = "", tags = "")
public class PoRemindController extends BladeController {

	@Autowired
	private IPoRemindService poremindService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入poremind")
	public R<PoRemindEntity> detail(PoRemindEntity poremind) {
		PoRemindEntity detail = poremindService.getOne(Condition.getQueryWrapper(poremind));
		return R.data(detail);
	}

	/** 分页 代码自定义代号 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入poremind")
	public R<IPage<PoRemindEntity>> list(PoRemindEntity poremind, Query query) {
		IPage<PoRemindEntity> pages = poremindService.page(Condition.getPage(query), Condition.getQueryWrapper(poremind));
		return R.data(pages);
	}

	@GetMapping("/export")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "导出", notes = "传入poremind")
	public void export(PoRemindDTO poremind, Query query, HttpServletResponse response) throws Exception{
		 poremindService.export(poremind,query,response);
	}

	/**  指派给我的现场催单 */
	@GetMapping("/myremind")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入poremind")
	public R<IPage<PoRemindEntity>> page(PoRemindEntity poremind, Query query) {
		IPage<PoRemindEntity> pages = poremindService.myRemind(Condition.getPage(query), poremind);
		return R.data(pages);
	}

	/**  指派给我的现场催单 */
	@GetMapping("/myremindexport")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "导出", notes = "传入poremind")
	public void myRemindExport(PoRemindDTO poremind, Query query, HttpServletResponse response) throws Exception{
		poremind.setTraceCode(getUser().getAccount());
		poremindService.export(poremind,query,response);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/apply")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "申请", notes = "传入poremind")
	public R apply(@Valid @RequestBody PoRemindApplyDTO poRemind) {
		return R.status(poremindService.apply(poRemind,getUser().getUserId()));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入poremind")
	public R update(@Valid @RequestBody PoRemindEntity poremind) {
		return R.status(poremindService.updateById(poremind));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入poremind")
	public R submit(@Valid @RequestBody PoRemindEntity poremind) {
		return R.status(poremindService.saveOrUpdate(poremind));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(poremindService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 */
	@PostMapping("/check")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "审核", notes = "")
	public
	R check(@Valid @RequestBody CheckDTO checkDto){
		return R.status(poremindService.check(checkDto));
	}


	@PostMapping("/checkbatch")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "审核", notes = "")
	public
	R checkBatch(@Valid @RequestBody List<CheckDTO> checkDtos){
		return R.status(poremindService.check(checkDtos));
	}

	/**
	 * 弃用
	 */
	@PostMapping("/complete")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "完成催单，弃用", notes = "")
	public
	R complete(@Valid @RequestBody CheckDTO checkDto){
		return R.status(poremindService.complete(checkDto));
	}

}
