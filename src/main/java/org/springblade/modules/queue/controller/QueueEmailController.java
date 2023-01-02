package org.springblade.modules.queue.controller;

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

import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.vo.QueueEmailVO;
import org.springblade.modules.queue.wrapper.QueueEmailWrapper;
import org.springblade.modules.queue.service.IQueueEmailService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-queue/queueEmail")
@Api(value = "", tags = "")
public class QueueEmailController extends BladeController {

	private IQueueEmailService queueEmailService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入queueEmail")
	public R<QueueEmailEntity> detail(QueueEmailEntity queueEmail) {
		QueueEmailEntity detail = queueEmailService.getOne(Condition.getQueryWrapper(queueEmail));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入queueEmail")
	public R<IPage<QueueEmailEntity>> list(QueueEmailEntity queueEmail, Query query) {
		IPage<QueueEmailEntity> pages = queueEmailService.page(Condition.getPage(query), Condition.getQueryWrapper(queueEmail));
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入queueEmail")
	public R save(@Valid @RequestBody QueueEmailEntity queueEmail) {
		return R.status(queueEmailService.save(queueEmail));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入queueEmail")
	public R update(@Valid @RequestBody QueueEmailEntity queueEmail) {
		return R.status(queueEmailService.updateById(queueEmail));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入queueEmail")
	public R submit(@Valid @RequestBody QueueEmailEntity queueEmail) {
		return R.status(queueEmailService.saveOrUpdate(queueEmail));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(queueEmailService.deleteLogic(Func.toLongList(ids)));
	}

}
