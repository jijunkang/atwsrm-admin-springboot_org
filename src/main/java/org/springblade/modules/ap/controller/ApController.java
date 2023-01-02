package org.springblade.modules.ap.controller;

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
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.dto.SubReq;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.ap.entity.ApEntity;
import org.springblade.modules.ap.service.IApService;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-ap/ap")
@Api(value = "", tags = "")
public class ApController extends BladeController {

	private IApService apService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入ap")
	public R<ApEntity> detail(ApEntity ap) {
		ApEntity detail = apService.getOne(Condition.getQueryWrapper(ap));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入ap")
	public R<IPage<ApEntity>> list(ApEntity ap, Query query) {
		IPage<ApEntity> pages = apService.page(Condition.getPage(query), Condition.getQueryWrapper(ap));
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入ap")
	public R save(@Valid @RequestBody ApEntity ap) {
		return R.status(apService.save(ap));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入ap")
	public R update(@Valid @RequestBody ApEntity ap) {
		return R.status(apService.updateById(ap));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入ap")
	public R submit(@Valid @RequestBody ApEntity ap) {
		return R.status(apService.saveOrUpdate(ap));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(apService.deleteLogic(Func.toLongList(ids)));
	}


    /**
     * 对账提交
     */
    @PostMapping("/dzsubmit")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "对账提交", notes = "传入apReq")
    public R dzSubmit(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.dzSubmit(apReq));
    }


    /**
     * 对账提交 - VMI
     */
    @PostMapping("/dzSubmitVmi")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "对账提交", notes = "传入apReq")
    public R dzSubmitVmi(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.dzSubmitVmi(apReq));
    }


    /**
     * 到货对账列表-已对账 详情  - VMI
     */
    @GetMapping("/apdetailVmi")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入ap")
    public R<Map<String, Object>> apDetailVmi(ApEntity ap) {
        return R.data(apService.dzDetailVmi(ap));
    }


    /**
     * 到货对账列表-已对账
     */
    @GetMapping("/aplist")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入apEntity")
    public R<IPage<ApEntity>> apList(ApReq apReq, Query query) {
        IPage<ApEntity> pages = apService.getPage(Condition.getPage(query), apReq);
        return R.data(pages);
    }

    /**
     * 到货对账列表-已对账 -VMI
     */
    @GetMapping("/apVmiList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入apEntity")
    public R<IPage<ApEntity>> apVmiList(ApReq apReq, Query query) {
        IPage<ApEntity> pages = apService.getVmiPage(Condition.getPage(query), apReq);
        return R.data(pages);
    }

    /**
     * 到货对账列表-已对账 详情
     */
    @GetMapping("/apdetail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入ap")
    public R<Map<String, Object>> apDetail(ApEntity ap) {
        return R.data(apService.dzDetail(ap));
    }


    /**
     * 到货对账-状态分类统计
     */
    @GetMapping("/countlist")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "状态分类统计", notes = "")
    public R<List<Map<String, Object>>> countList(ApReq apReq) {
        return R.data(apService.countList(apReq));
    }

    /**
     * 到货对账-状态分类统计 - vmi
     */
    @GetMapping("/countVmiList")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "状态分类统计", notes = "")
    public R<List<Map<String, Object>>> countVmiList(ApReq apReq) {
        return R.data(apService.countVmiList(apReq));
    }



    /**
     * 审核通过/驳回
     */
    @PostMapping("/suborback")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "审核通过/驳回", notes = "传入subReq")
    public R subOrBack(@Valid @RequestBody SubReq subReq) {
        return R.status(apService.subOrBack(subReq));
    }

    /**
     * 审核通过/驳回（批量）
     */
    @PostMapping("/suborbackbatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "审核通过/驳回（批量）", notes = "传入subReq")
    public R subOrBackBatch(@Valid @RequestBody List<SubReq> subReqs) {
        return R.status(apService.subOrBackBatch(subReqs));
    }


    /**
     * 对账详情-收货明细新增
     */
    @PostMapping("/dtsubmit")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "对账详情-收货明细新增", notes = "传入apReq")
    public R dtSubmit(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.dtSubmit(apReq));
    }

    /**
     * 对账详情-收货明细移除
     */
    @PostMapping("/dtremove")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "对账编辑", notes = "传入apReq")
    public R dtRemove(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.dtRemove(apReq));
    }

    /**
     * 对账详情-保存发票
     */
    @PostMapping("/subinvoice")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "对账编辑", notes = "传入apReq")
    public R subInvoice(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.subInvoice(apReq));
    }

    /**
     * 对账详情-导出
     *
     * @param apReq ApReq
     * @param response HttpServletResponse
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "对账详情-导出", notes = "export")
    public void export(ApReq apReq, HttpServletResponse response){
        apService.export(apReq, response);
    }


    /**
     * 批量生成应付单
     */
    @PostMapping("/saveaps")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "批量生成应付单", notes = "传入apReq")
    public R saveAps(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.saveAps(apReq));
    }


    /**
     * 应付请款-状态分类统计
     */
    @GetMapping("/yfcountlist")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "状态分类统计", notes = "")
    public R<List<Map<String, Object>>> yfCountList(ApReq apReq) {
        return R.data(apService.yfCountList(apReq));
    }

    /**
     * 审批
     */
    @PostMapping("/audit")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "审批", notes = "传入apReq")
    public R audit(@Valid @RequestBody ApReq apReq) throws IOException {
        return R.status(apService.audit(apReq));
    }


    /**
     * 应付款单-详情保存
     */
    @PostMapping("/yfsave")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "应付款单-详情保存", notes = "传入apReq")
    public R yfSave(@Valid @RequestBody ApReq apReq) {
        return R.status(apService.yfSave(apReq));
    }

    /**
     * 打印数据
     */
    @GetMapping("/print")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "打印数据", notes = "")
    public R<Map<String, Object>> print(ApReq apReq) {
        return R.data(apService.print(apReq));
    }


    /**
     * 预付记录
     */
    @GetMapping("/yfrecord")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "预付记录", notes = "")
    public R<Map<String, Object>> yfRecord(ApReq apReq) {
        return R.data(apService.yfRecord(apReq));
    }

}
