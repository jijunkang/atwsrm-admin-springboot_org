package org.springblade.modules.outpr.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.vo.OutSupItemPriceVO;
import org.springblade.modules.outpr.service.IOutSupItemPriceService;

import java.util.List;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-outpr/outsupitemprice")
@Api(value = "", tags = "")
public class OutSupItemPriceController extends BladeController {

	private IOutSupItemPriceService outsupitempriceService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入outsupitemprice")
	public R<OutSupItemPriceEntity> detail(OutSupItemPriceEntity outsupitemprice) {
		OutSupItemPriceEntity detail = outsupitempriceService.getOne(Condition.getQueryWrapper(outsupitemprice));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入outsupitemprice")
	public R<IPage<OutSupItemPriceEntity>> page(OutSupItemPriceEntity outsupitemprice, Query query) {
		IPage<OutSupItemPriceEntity> pages = outsupitempriceService.page(Condition.getPage(query), Condition.getQueryWrapper(outsupitemprice).orderByDesc("update_time"));
		return R.data(pages);
	}

	@GetMapping("/getprocesspricelist")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "列表", notes = "传入OutSupItemPriceEntity")
	public R<List<OutSupItemPriceVO>> getProcessPriceList(OutSupItemPriceEntity outsupitemprice) {
		if(outsupitemprice.getPrItemId() == null){
			return R.data(null);
		}
		List<OutSupItemPriceVO> list = outsupitempriceService.getProcessPriceList(Condition.getQueryWrapper(outsupitemprice));
		return R.data(list);
	}
//
//	/**
//	 * 新增 代码自定义代号
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入outsupitemprice")
//	public R save(@Valid @RequestBody OutSupItemPriceEntity outsupitemprice) {
//		return R.status(outsupitempriceService.save(outsupitemprice));
//	}
//
//	/**
//	 * 修改 代码自定义代号
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入outsupitemprice")
//	public R update(@Valid @RequestBody OutSupItemPriceEntity outsupitemprice) {
//		return R.status(outsupitempriceService.updateById(outsupitemprice));
//	}
//
//	/**
//	 * 新增或修改 代码自定义代号
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入outsupitemprice")
//	public R submit(@Valid @RequestBody OutSupItemPriceEntity outsupitemprice) {
//		return R.status(outsupitempriceService.saveOrUpdate(outsupitemprice));
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
//		return R.status(outsupitempriceService.deleteLogic(Func.toLongList(ids)));
//	}

}
