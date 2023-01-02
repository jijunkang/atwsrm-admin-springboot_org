package org.springblade.modules.aps.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

import org.springblade.modules.aps.entity.DeliveryModifyAuditEntity;
import org.springblade.modules.aps.service.IDeliveryModifyAuditService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-aps/deliveryModifyAudit")
@Api(value = "", tags = "")
public class DeliveryModifyAuditController extends BladeController {

	private IDeliveryModifyAuditService deliveryModifyAuditService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入deliveryModifyAudit")
	public R<DeliveryModifyAuditEntity> detail(DeliveryModifyAuditEntity deliveryModifyAudit) {
		DeliveryModifyAuditEntity detail = deliveryModifyAuditService.getOne(Condition.getQueryWrapper(deliveryModifyAudit));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入deliveryModifyAudit")
	public R<IPage<DeliveryModifyAuditEntity>> page(DeliveryModifyAuditEntity deliveryModifyAudit, Query query) {
		IPage<DeliveryModifyAuditEntity> pages = deliveryModifyAuditService.page(Condition.getPage(query), Condition.getQueryWrapper(deliveryModifyAudit));
		return R.data(pages);
	}

  /**
	 * 列表 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "列表", notes = "传入deliveryModifyAudit")
	public R<List<DeliveryModifyAuditEntity>> list(DeliveryModifyAuditEntity deliveryModifyAudit) {
		List<DeliveryModifyAuditEntity> list = deliveryModifyAuditService.list(Condition.getQueryWrapper(deliveryModifyAudit));
		return R.data(list);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入deliveryModifyAudit")
	public R save(@Valid @RequestBody DeliveryModifyAuditEntity deliveryModifyAudit) {
		return R.status(deliveryModifyAuditService.save(deliveryModifyAudit));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入deliveryModifyAudit")
	public R update(@Valid @RequestBody DeliveryModifyAuditEntity deliveryModifyAudit) {
		return R.status(deliveryModifyAuditService.updateById(deliveryModifyAudit));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入deliveryModifyAudit")
	public R submit(@Valid @RequestBody DeliveryModifyAuditEntity deliveryModifyAudit) {
		return R.status(deliveryModifyAuditService.saveOrUpdate(deliveryModifyAudit));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deliveryModifyAuditService.deleteLogic(Func.toLongList(ids)));
	}

}
