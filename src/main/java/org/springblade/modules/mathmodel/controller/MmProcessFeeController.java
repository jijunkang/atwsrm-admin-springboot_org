package org.springblade.modules.mathmodel.controller;

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
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.mathmodel.vo.MmProcessFeeVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.util.Map;

import org.springblade.modules.mathmodel.entity.MmProcessFeeEntity;
import org.springblade.modules.mathmodel.service.IMmProcessFeeService;
import org.springframework.web.multipart.MultipartFile;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/mmProcessFee")
@Api(value = "", tags = "")
public class MmProcessFeeController extends BladeController {

	private IMmProcessFeeService mmProcessFeeService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入mmProcessFee")
	public R<MmProcessFeeEntity> detail(MmProcessFeeEntity mmProcessFee) {
        MmProcessFeeEntity detail = mmProcessFeeService.getOne(Condition.getQueryWrapper(mmProcessFee));
        MmProcessFeeVO vo = BeanUtil.copy(detail, MmProcessFeeVO.class);
        vo.setHistories(mmProcessFeeService.getByHistoryId(detail.getId()));
        return R.data(vo);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入mmProcessFee")
	public R<IPage<MmProcessFeeEntity>> page(MmProcessFeeEntity mmProcessFee, Query query) {
		IPage<MmProcessFeeEntity> pages = mmProcessFeeService.page(Condition.getPage(query), mmProcessFeeService.getQueryWrapper(mmProcessFee));
		return R.data(pages);
	}

  /**
	 * 列表 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "列表", notes = "传入mmProcessFee")
	public R<List<MmProcessFeeEntity>> list(MmProcessFeeEntity mmProcessFee) {
		List<MmProcessFeeEntity> list = mmProcessFeeService.list(Condition.getQueryWrapper(mmProcessFee));
		return R.data(list);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入mmProcessFee")
	public R save(@Valid @RequestBody MmProcessFeeEntity mmProcessFee) {
		return R.status(mmProcessFeeService.save(mmProcessFee));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入mmProcessFee")
	public R update(@Valid @RequestBody MmProcessFeeEntity mmProcessFee) {
		return R.status(mmProcessFeeService.update(mmProcessFee));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入mmProcessFee")
	public R submit(@Valid @RequestBody MmProcessFeeEntity mmProcessFee) {
		return R.status(mmProcessFeeService.saveOrUpdate(mmProcessFee));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(mmProcessFeeService.deleteLogic(Func.toLongList(ids)));
	}


    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入mmProcessFee")
    public void export(MmProcessFeeEntity mmProcessFee, Query query, HttpServletResponse response) throws Exception {
        mmProcessFeeService.export(mmProcessFee, query, response);
    }

    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = mmProcessFeeService.importExcel(file);
        if(Boolean.parseBoolean(map.get("flag").toString())){
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }

}
