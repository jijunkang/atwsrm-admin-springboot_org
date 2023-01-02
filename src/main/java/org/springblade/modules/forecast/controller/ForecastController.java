package org.springblade.modules.forecast.controller;

import io.swagger.annotations.*;
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

import org.springblade.modules.forecast.entity.ForecastEntity;
import org.springblade.modules.forecast.vo.ForecastVO;
import org.springblade.modules.forecast.wrapper.ForecastWrapper;
import org.springblade.modules.forecast.service.IForecastService;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


/**
 * 情报 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-forecast/forecast")
@Api(value = "情报", tags = "情报")
public class ForecastController extends BladeController {

	private IForecastService forecastService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入forecast")
	public R<ForecastEntity> detail(ForecastEntity forecast) {
		ForecastEntity detail = forecastService.getOne(Condition.getQueryWrapper(forecast));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "itemCode", value = "物料编号" ,paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "itemName", value = "物料名称", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入forecast")
	public R<IPage<Map<String,Object>>> list( ForecastEntity forecast, Query query) {
		 IPage<Map<String,Object>> pages = forecastService.selectYmPage(Condition.getPage(query), forecast);
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入forecast")
	public R save(@Valid @RequestBody ForecastEntity forecast) {
		return R.status(forecastService.save(forecast));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入forecast")
	public R update(@Valid @RequestBody ForecastEntity forecast) {
		return R.status(forecastService.updateById(forecast));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入forecast")
	public R submit(@Valid @RequestBody ForecastEntity forecast) {
		return R.status(forecastService.saveOrUpdate(forecast));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(forecastService.deleteLogic(Func.toLongList(ids)));
	}

}
