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
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.ap.entity.ApBillEntity;
import org.springblade.modules.ap.service.IApBillService;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-ap/apBill")
@Api(value = "", tags = "")
public class ApBillController extends BladeController {

    private IApBillService apBillService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入apBill")
    public R<ApBillEntity> detail(ApBillEntity apBill) {
        ApBillEntity detail = apBillService.getOne(Condition.getQueryWrapper(apBill));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入apBill")
    public R<IPage<ApBillEntity>> page(ApBillEntity apBill, Query query) {
        IPage<ApBillEntity> pages = apBillService.page(Condition.getPage(query), Condition.getQueryWrapper(apBill));
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入apBill")
    public R save(@Valid @RequestBody ApBillEntity apBill) {
        return R.status(apBillService.save(apBill));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入apBill")
    public R update(@Valid @RequestBody ApBillEntity apBill) {
        return R.status(apBillService.updateById(apBill));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入apBill")
    public R submit(@Valid @RequestBody ApBillEntity apBill) {
        return R.status(apBillService.saveOrUpdate(apBill));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(apBillService.deleteLogic(Func.toLongList(ids)));
    }


    /**
     * 应付请款列表
     */
    @GetMapping("/aplist")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入apEntity")
    public R<IPage<ApBillEntity>> apList(ApReq apReq, Query query) {
        IPage<ApBillEntity> pages = apBillService.getPage(Condition.getPage(query), apReq);
        return R.data(pages);
    }

    /**
     * 应付请款-详情
     */
    @GetMapping("/billdetail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入ap")
    public R<Map<String, Object>> billDetail(ApBillEntity ap) {
        return R.data(apBillService.billDetail(ap));
    }


    /**
     * 应付请款-审批
     */
    @PostMapping("/audit")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "审批", notes = "传入apReq")
    public R audit(@Valid @RequestBody ApReq apReq) throws IOException {
        return R.status(apBillService.audit(apReq));
    }

    /**
     * 应付请款-审批 （批量）
     */
    @PostMapping("/auditbatch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "审批", notes = "传入apReq")
    public R auditBatch(@Valid @RequestBody List<ApReq> apReqs) throws IOException {
        return R.status(apBillService.auditBatch(apReqs));
    }

    /**
     * 应付请款-状态分类统计
     */
    @GetMapping("/countlist")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "状态分类统计", notes = "")
    public R<List<Map<String, Object>>> countList(ApReq apReq) {
        return R.data(apBillService.countList(apReq));
    }

    /**
     * 应付请款-详情保存
     */
    @PostMapping("/yfsave")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "详情保存", notes = "传入apReq")
    public R yfSave(@Valid @RequestBody ApReq apReq) {
        return R.status(apBillService.yfSave(apReq));
    }


    /**
     * 应付请款-批量提交
     */
    @PostMapping("/submitbatch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "详情保存", notes = "传入apReq")
    public R submitBatch(@Valid @RequestBody List<ApReq> apReqs) throws IOException {
        for (ApReq apReq : apReqs) {
            apBillService.audit(apReq);
        }
        return R.success("操作成功");
    }

    /**
     * 打印数据
     */
    @GetMapping("/print")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "打印数据", notes = "")
    public R<Map<String, Object>> print(ApReq apReq) {
        return R.data(apBillService.print(apReq));
    }


    /**
     * 供应商 导出
     */
    @PostMapping("/backToRec")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public
    void backToRec(@Valid @RequestBody List<String> apCodes) throws RuntimeException{
        apBillService.backToRec(apCodes);
    }
}
