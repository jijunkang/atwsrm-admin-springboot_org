package org.springblade.modules.aps.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.aps.vo.ApsReportExdevVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

import org.springblade.modules.aps.entity.ApsReportExdevEntity;
import org.springblade.modules.aps.service.IApsReportExdevService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-aps/apsReportExdev")
@Api(value = "", tags = "")
public class ApsReportExdevController extends BladeController {

	private IApsReportExdevService apsReportExdevService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入apsReportExdev")
	public R<ApsReportExdevEntity> detail(ApsReportExdevEntity apsReportExdev) {
		ApsReportExdevEntity detail = apsReportExdevService.getOne(Condition.getQueryWrapper(apsReportExdev));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入apsReportExdev")
	public R<IPage<ApsReportExdevVO>> page(ApsReportExdevEntity apsReportExdev, Query query) {
		IPage<ApsReportExdevVO> pages = apsReportExdevService.getPage(query, apsReportExdev);
		return R.data(pages);
	}

  /**
	 * 列表 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "列表", notes = "传入apsReportExdev")
	public R<List<ApsReportExdevEntity>> list(ApsReportExdevEntity apsReportExdev) {
		List<ApsReportExdevEntity> list = apsReportExdevService.list(Condition.getQueryWrapper(apsReportExdev));
		return R.data(list);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入apsReportExdev")
	public R save(@Valid @RequestBody ApsReportExdevEntity apsReportExdev) {
		return R.status(apsReportExdevService.save(apsReportExdev));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入apsReportExdev")
	public R update(@Valid @RequestBody ApsReportExdevEntity apsReportExdev) {
		return R.status(apsReportExdevService.updateById(apsReportExdev));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入apsReportExdev")
	public R submit(@Valid @RequestBody ApsReportExdevEntity apsReportExdev) {
		return R.status(apsReportExdevService.saveOrUpdate(apsReportExdev));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(apsReportExdevService.deleteLogic(Func.toLongList(ids)));
	}

    /**
     * 导出
     *
     * @param apsReportExdev ApsReportExdevEntity
     * @param response HttpServletResponse
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "导出", notes = "export")
    public void export(ApsReportExdevEntity apsReportExdev, HttpServletResponse response){
        apsReportExdevService.export(apsReportExdev, response);
    }
}
