package org.springblade.modules.ap.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.ap.dto.ApRcvDTO;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.entity.ApRcvReqEntity;
import org.springblade.modules.ap.service.IApRcvService;
import org.springblade.modules.ap.vo.ApRcvVO;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-ap/aprcv")
@Api(value = "", tags = "")
public class ApRcvController extends BladeController {

    private IApRcvService aprcvService;

    private ISupplierService supplierService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入aprcv")
    public R<ApRcvEntity> detail(ApRcvEntity aprcv) {
        ApRcvEntity detail = aprcvService.getOne(Condition.getQueryWrapper(aprcv));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入aprcv")
    public R<IPage<ApRcvEntity>> page(ApRcvEntity aprcv, Query query) {
        IPage<ApRcvEntity> pages = aprcvService.page(Condition.getPage(query), Condition.getQueryWrapper(aprcv));
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入aprcv")
    public R save(@Valid @RequestBody ApRcvEntity aprcv) {
        return R.status(aprcvService.save(aprcv));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入aprcv")
    public R update(@Valid @RequestBody ApRcvEntity aprcv) {
        aprcv.setTaxSubTotal(aprcv.getRcvActualQty().multiply(aprcv.getTaxPrice()));
        return R.status(aprcvService.updateById(aprcv));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入aprcv")
    public R submit(@Valid @RequestBody ApRcvEntity aprcv) {
        return R.status(aprcvService.saveOrUpdate(aprcv));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(aprcvService.remove(ids));
    }

    /**
     * 到货对账列表-待对账
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "apReq")
    public R<IPage<ApRcvEntity>> list(ApReq apReq, Query query) {
        IPage<ApRcvEntity> pages = aprcvService.getPage(Condition.getPage(query), apReq);
        return R.data(pages);
    }

    /**
     * 到货对账列表-待对账 - vmi
     */
    @GetMapping("/vmiList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入aprcv")
    public R<IPage<ApRcvReqEntity>> vmiList(ApReq apReq, Query query) {
        IPage<ApRcvReqEntity> pages = aprcvService.getVmiPage(Condition.getPage(query), apReq);
        return R.data(pages);
    }

    /**
     * U9删除VMI结算同步到SRM
     */
    @PostMapping("/deleteVmiSettle")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入aprcv")
    public R deleteVmiSettle(@RequestBody ApReq apReq) {
        R r = aprcvService.deleteVmiSettle(apReq);
        return r;
    }


    /**
     * 扣款新增
     */
    @PostMapping("/kksave")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入aprcv")
    public R kkSave(@Valid @RequestBody ApRcvEntity aprcv) {
        aprcv.setType("KK");
        aprcv.setStatus(IApRcvService.STATUS_INIT);
        aprcv.setRcvCode(aprcvService.genCode(aprcv.getType()));
        //扣款默认值
        aprcv.setRcvActualQty(new BigDecimal("-1"));
        aprcv.setItemCode("20020713");
        aprcv.setItemName("质量扣款");
        aprcv.setTaxSubTotal(aprcv.getRcvActualQty().multiply(aprcv.getTaxPrice()));
        aprcv.setUom("PCS");
        Supplier supplier = supplierService.getByCode(aprcv.getSupCode());
        aprcv.setTaxRate(supplier.getTaxRate());
        return R.status(aprcvService.save(aprcv));
    }

    /**
     * 扣款列表
     */
    @GetMapping("/kklist")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "apReq")
    public R<IPage<ApRcvEntity>> kkList(ApReq apReq, Query query) {
        IPage<ApRcvEntity> pages = aprcvService.kkList(Condition.getPage(query), apReq);
        return R.data(pages);
    }

    /**
     * 扣款审核
     */
    @PostMapping("/audit")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "扣款审核", notes = "apReq")
    public R audit(@Valid @RequestBody ApReq apReq) {
        return R.status(aprcvService.audit(apReq));
    }

    /**
     * 扣款审核 (批量)
     */
    @PostMapping("/auditbatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "扣款审核", notes = "apReq")
    public R auditBatch(@Valid @RequestBody List<ApReq> apReqs) {
        return R.status(aprcvService.auditBatch(apReqs));
    }

    /**
     * 扣款-状态分类统计
     */
    @GetMapping("/countlist")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "状态分类统计", notes = "apReq")
    public R<List<Map<String, Object>>> countList(ApReq apReq) {
        return R.data(aprcvService.countList(apReq));
    }


    /**
     * 批量修改
     */
    @PostMapping("/updates")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改", notes = "批量修改")
    public R updates(@Valid @RequestBody ApReq apReq) {
        return R.status(aprcvService.updates(apReq));
    }


    /**
     * 到货对账
     * @param response
     * @param selectionIds
     * @param apReq
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response,String selectionIds, ApReq apReq){
        aprcvService.export(response,selectionIds, apReq);
    }

    /**
     * 待对账导出  -VMI
     */
    @GetMapping("/vmiExport")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "待对账导出", notes = "待对账导出")
    public void vmiExport(HttpServletResponse response,String selectionIds, ApReq apReq){
        aprcvService.vmiExport(response,selectionIds, apReq);
    }



    /**
     * NCR明细
     */
    @GetMapping("/ncrdetail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "NCR明细", notes = "传入aprcv")
    public R<List<NcrEntity>> ncrDetail(ApRcvEntity aprcv) {
        return R.data(aprcvService.ncrDetail(aprcv));
    }

    /**
     * 全部导出(NCR)明细
     */
    @GetMapping("/exportncr")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "全部导出(NCR)明细", notes = "全部导出(NCR)明细")
    public void exportNcr(HttpServletResponse response, ApReq apReq){
        aprcvService.exportNcr(response, apReq);
    }


    /**
     * 审核合同 = VMI
     */
    @PostMapping("/reviewContract")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入aprcv")
    public R reviewContract(@Valid @RequestBody ApRcvDTO apRcvDTO) {
        return R.status(aprcvService.reviewContract(apRcvDTO));
    }

    /**
     * 合同详情 -打印合同
     */
    @GetMapping("/detailOfVmi")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入po")
    public List<ApRcvVO> detailOfVmi(ApRcvDTO apRcvDTO){
        List<ApRcvVO> apRcvVOS = aprcvService.getDetailOfVmi(apRcvDTO.getRcvIds());
        return apRcvVOS;
    }

}
