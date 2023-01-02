package org.springblade.modules.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.po.dto.PoReceiveDTO;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.mapper.PoMapper;
import org.springblade.modules.po.mapper.PoReceiveMapper;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoReceiveService;
import org.springblade.modules.po.vo.PoItemVO;
import org.springblade.modules.report.dto.*;
import org.springblade.modules.report.entity.*;
import org.springblade.modules.report.service.IReportService;
import org.springblade.modules.report.vo.SupplierOutputVo;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.OtdReport;
import org.springblade.modules.supplier.vo.OmsEchrtsOfSupplierVO;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfSupplierVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * 报表 控制器
 *
 * 2022/1/13 新建 专门用来做报表的Controller
 * @author zlw
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-report/report")
@Api(value = "报表", tags = "报表")
public class ReportController extends BladeController {

    @Autowired
    private IReportService reportService;

    @Autowired
    private IPoReceiveService poReceiveService;
    @Autowired
    private IPoItemService poItemService;
    @Autowired
    private PoItemMapper poItemMapper;
    @Autowired
    private PoMapper poMapper;
    @Autowired
    private PoReceiveMapper poReceiveMapper;

    /**
     * VMI物料消耗表
     */
    @GetMapping("/vmiConsume")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<VmiReport>> vmiConsumeReport(VmiReportReq vmiReportReq, Query query) throws RuntimeException{
        IPage<VmiReport> pages = reportService.getVmiConsumeReport(Condition.getPage(query), vmiReportReq);
        return R.data(pages);
    }

    /**
     * 送货单报表
     */
    @GetMapping("/deliverReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "DeliverReportReq")
    public
    R<IPage<DeliverReport>> deliverReport(DeliverReportReq deliverReportReq, Query query) throws RuntimeException{
        IPage<DeliverReport> pages = reportService.getDeliverReport(Condition.getPage(query), deliverReportReq);
        return R.data(pages);
    }

    /**
     * 送货单报表导出
     */
    @GetMapping("/deliverReportExport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "DeliverReportReq")
    public void deliverReportExport(DeliverReportReq deliverReportReq , HttpServletResponse response) {
        reportService.deliverReportExport(deliverReportReq, response);
    }

    /**
     * 铸件及时率报表
     */
    @GetMapping("/zjOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "DeliverReportReq")
    public
    R<IPage<ZJItemOtdReport>> zjOtdReport(ZJItemOtdReportReq zjItemOtdReportReq, Query query) throws RuntimeException{
        IPage<ZJItemOtdReport> pages = reportService.getZJItemOtdReport(Condition.getPage(query), zjItemOtdReportReq);
        return R.data(pages);
    }

    /**
     * 关键物料日报报表 - 主表
     */
    @GetMapping("/keyItemDailyReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "DeliverReportReq")
    public
    R<IPage<ItemDailyReport>> keyItemDailyReport(KeyItemReportReq keyItemReportReq, Query query) throws RuntimeException{
        IPage<ItemDailyReport> page = reportService.getKeyItemDailyReport(Condition.getPage(query),keyItemReportReq);
        return R.data(page);
    }

    /**
     * 定拍点物料导出 - 导出
     */
    @GetMapping("/exportKeyItem")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportKeyItem(KeyItemReportReq keyItemReportReq, HttpServletResponse response) {
        reportService.exportKeyItem(keyItemReportReq, response);
    }

    /**
     * 非关键物料日报报表 - 主表
     */
    @GetMapping("/notKeyItemDailyReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "DeliverReportReq")
    public
    R<IPage<ItemDailyReport>> notKeyItemDailyReport(KeyItemReportReq keyItemReportReq, Query query) throws RuntimeException{
        IPage<ItemDailyReport> page = reportService.getNotKeyItemDailyReport(Condition.getPage(query),keyItemReportReq);
        return R.data(page);
    }

    /**
     * 委外物料日报报表 - 主表
     */
    @GetMapping("/wwItemDailyReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "DeliverReportReq")
    public
    R<IPage<ItemDailyReport>> wwItemDailyReport(KeyItemReportReq keyItemReportReq, Query query) throws RuntimeException{
        IPage<ItemDailyReport> page = reportService.getWWItemDailyReport(Condition.getPage(query),keyItemReportReq);
        return R.data(page);
    }


    /**
     * 日报报表详细界面
     */
    @GetMapping("/itemDailyDetailReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouScheduleOffset(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = reportService.getItemDailyDetailReport(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }


    /**
     * 日报报表详细界面导出
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/exportItemDailyDetailReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportCaiGouAllOffset(CaiGouScheduleReq caiGouScheduleReq , HttpServletResponse response) {
        reportService.exportItemDailyDetailReport(caiGouScheduleReq, response);
    }

    /**
     * 送货单号详情
     * @param poReceive
     * @param query
     * @return
     * @Desc maily
     */
    @GetMapping("/getList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入poReceive")
    public
    R<List<PoItemVO>> getList(PoReceiveDTO poReceive, Query query) {
        List<PoItemVO> voList = Lists.newArrayList();
        List<PoReceiveEntity> poReceiveEntityIPage = poReceiveService.list(
            Condition.getQueryWrapper(new PoReceiveEntity())
                .in(poReceive.getStatuss()!=null && poReceive.getStatuss().equals(poReceiveService.STATUS_UPDATE) && poReceive.getStatus()==null,"status",poReceiveService.STATUS_ORDER,poReceiveService.STATUS_OUT_TOCHECK,poReceiveService.STATUS_OUT_CHECK,poReceiveService.STATUS_TOHANDLE)
                .eq(poReceive.getStatus()!=null ,"status",poReceive.getStatus())
                .le(poReceive.getStatus()==null && poReceive.getStatuss()==null,"status",poReceiveService.STATUS_CANCEL)
                .like(StringUtil.isNotBlank(poReceive.getSupName()), "sup_name", poReceive.getSupName())
                .like(StringUtil.isNotBlank(poReceive.getSupCode()), "sup_code", poReceive.getSupCode())
                .like(StringUtil.isNotBlank(poReceive.getRcvCode()), "rcv_code", poReceive.getRcvCode()));

        for(PoReceiveEntity entity :poReceiveEntityIPage){
            PoItemEntity pi = poItemService.getById(entity.getPiId());
            PoEntity po = poMapper.getPoInfoByPoCode(pi.getPoCode());
            System.out.println("\"-------------------------------------------------------\" = " + "-------------------------------------------------------");
            System.out.println("pi = " + pi);
            System.out.println("po = " + po);
            System.out.println("\"-------------------------------------------------------\" = " + "-------------------------------------------------------");
            PoItemVO vo = BeanUtil.copy(pi,PoItemVO.class);
            vo.setTemplateType(po.getTemplateType());
            if(pi != null){
                vo.setRcvNum(new BigDecimal(entity.getRcvNum().toString()));
                vo.setHeatCode(entity.getHeatCode());
                vo.setDoRemark(entity.getRemark());
                Integer rcvNumAll = poItemMapper.getRcvAllNumByPiId(entity.getPiId().toString());
                vo.setNotSendNum(pi.getTcNum().add(pi.getFillGoodsNum().subtract(new BigDecimal(rcvNumAll))));
            }

            if(poReceiveService.OUT.equals(entity.getIsOutCheck())){
                Integer unqualifiedNum = poReceiveMapper.getUnqualifiedNum(entity.getRcvCode(),pi.getPoCode(),pi.getPoLn(),entity.getHeatCode());
                if(unqualifiedNum==null){
                    vo.setUnqualifiedNum(0);
                } else {
                    vo.setUnqualifiedNum(unqualifiedNum);
                }
                vo.setIsOutCheck(poReceiveService.OUT);
            } else {
                vo.setUnqualifiedNum(0);
                vo.setIsOutCheck(poReceiveService.INNER);
            }
            voList.add(vo);
        }
        return R.data(voList);
    }


    /**
     * 球座报表详细界面
     */
    @GetMapping("/qzReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<QZReport>> caiGouScheduleOffset(QZReq qzReq, Query query) throws RuntimeException{
        IPage<QZReport> pages = reportService.getQZReport(Condition.getPage(query), qzReq);
        return R.data(pages);
    }

    /**
     * 球座报表详细界面导出
     * @param
     * @param response
     */
    @GetMapping("/exportQZ")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportQZ(QZReq qzReq, HttpServletResponse response) {
        reportService.exportQZ(qzReq, response);
    }


    /**
     * 下单及时率报表
     */
    @GetMapping("/orderOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<OrderOtdReport>> orderOtdReport(OrderOtdReq orderOtdReq, Query query) throws RuntimeException{
        IPage<OrderOtdReport> pages = reportService.getOrderOtdReport(Condition.getPage(query), orderOtdReq);
        return R.data(pages);
    }



    /**
     * 下单及时率报表 导出
     * @param
     * @param response
     */
    @GetMapping("/exportOrderOtd")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportQZ(OrderOtdReq orderOtdReq, HttpServletResponse response) {
        reportService.exportOrderOtd(orderOtdReq, response);
    }


    /**
     * 下单及时率 统计
     */
    @GetMapping("/orderOtdStatistics")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<OrderOtdReport>> orderOtdStatistics(OrderOtdReq orderOtdReq, Query query) throws RuntimeException{
        IPage<OrderOtdReport> pages = reportService.getOrderOtdReport(Condition.getPage(query), orderOtdReq);
        return R.data(pages);
    }

    /**
     * 自动下单率报表
     */
    @GetMapping("/autoOrderOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<AutoOrderOtdReport>> autoOrderOtdReport(AutoOrderOtdReq autoOrderOtdReq, Query query) throws RuntimeException{
        IPage<AutoOrderOtdReport> pages = reportService.getAutoOrderOtdReport(Condition.getPage(query), autoOrderOtdReq);
        return R.data(pages);
    }


    /**
     * 自动下单率报表 导出
     * @param
     * @param response
     */
    @GetMapping("/exportAutoOrderOtd")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAutoOrderOtd(AutoOrderOtdReq autoOrderOtdReq, HttpServletResponse response) {
        reportService.exportAutoOrderOtd(autoOrderOtdReq,response);
    }


    /**
     * 采购额统计报表
     */
    @GetMapping("/orderAmountOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "orderAmountOtdReport")
    public
    R<IPage<OrderAmountOtdReport>> orderAmountOtdReport(OrderAmountOtdReportReq orderAmountOtdReportReq, Query query) throws RuntimeException{
        IPage<OrderAmountOtdReport> pages = reportService.getOrderAmountOtdReport(Condition.getPage(query), orderAmountOtdReportReq);
        return R.data(pages);
    }


    /**
     * 采购额统计报表 导出
     * @param
     * @param response
     */
    @GetMapping("/exportOrderAmountOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "exportOrderAmountOtdReport")
    public void exportOrderAmountOtdReport(OrderAmountOtdReportReq orderAmountOtdReportReq, HttpServletResponse response) {
        reportService.exportOrderAmountOtdReport(orderAmountOtdReportReq,response);
    }

    /**
     * 模具管理报表
     * @param
     * @param
     */
    @GetMapping("/getMouldManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getMouldManagementReport")
    R<IPage<MouldManagementEntity>> getMouldManagementReport(MouldManagementEntity mouldManagementEntity, Query query) {
        IPage<MouldManagementEntity> pages = reportService.getMouldManagementReport(Condition.getPage(query), mouldManagementEntity);
        return R.data(pages);
    }


    /**
     * 模具管理报表  导出
     * @param
     * @param
     */
    @GetMapping("/exportMouldManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "exportMouldManagementReport")
    void exportMouldManagementReport(MouldManagementEntity mouldManagement, HttpServletResponse response) {
        reportService.exportMouldManagementReport(mouldManagement,response);
    }


    /**
     * 模具管理报表  删除
     * @param
     * @param
     */
    @PostMapping("/deleteMouldManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "deleteMouldManagementReport")
    void deleteMouldManagementReport(@RequestBody List<MouldManagementEntity> mouldManagementEntities) {
        R.status(reportService.deleteMouldManagementReport(mouldManagementEntities));
    }

    /**
     * 模具管理报表  新增
     * @param
     * @param
     */
    @PostMapping("/insertMouldManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "insertMouldManagementReport")
    void insertMouldManagementReport(@RequestBody List<MouldManagementEntity> mouldManagementEntities) {
        R.status(reportService.insertMouldManagementReport(mouldManagementEntities));
    }

    /**
     * 模具管理报表  更新
     * @param
     * @param
     */
    @PostMapping("/updateMouldManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "updateMouldManagementReport")
    void updateMouldManagementReport(@RequestBody List<MouldManagementEntity> mouldManagementEntities) {
        R.status(reportService.updateMouldManagementReport(mouldManagementEntities));
    }


    /**
     * Jit清单报表
     * @param
     * @param
     */
    @GetMapping("/getJitManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getJitManagementReport")
    R<IPage<JitManagementEntity>> getJitManagementReport(JitManagementEntity JitManagementEntity, Query query) {
        IPage<JitManagementEntity> pages = reportService.getJitManagementReport(Condition.getPage(query), JitManagementEntity);
        return R.data(pages);
    }


    /**
     * Jit清单报表  导出
     * @param
     * @param
     */
    @GetMapping("/exportJitManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "exportJitManagementReport")
    void exportJitManagementReport(JitManagementEntity jitManagement, HttpServletResponse response) {
        reportService.exportJitManagementReport(jitManagement,response);
    }


    /**
     * Jit清单报表  删除
     * @param
     * @param
     */
    @PostMapping("/deleteJitManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "deleteJitManagementReport")
    void deleteJitManagementReport(@RequestBody List<JitManagementEntity> jitManagementEntities) {
        R.status(reportService.deleteJitManagementReport(jitManagementEntities));
    }

    /**
     * Jit清单报表  新增
     * @param
     * @param
     */
    @PostMapping("/insertJitManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "insertJitManagementReport")
    void insertJitManagementReport(@RequestBody List<JitManagementEntity> jitManagementEntities) {
        R.status(reportService.insertJitManagementReport(jitManagementEntities));
    }

    /**
     * Jit清单报表  更新
     * @param
     * @param
     */
    @PostMapping("/updateJitManagementReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "updateJitManagementReport")
    void updateJitManagementReport(@RequestBody List<JitManagementEntity> jitManagementEntities) {
        R.status(reportService.updateJitManagementReport(jitManagementEntities));
    }

    /**
     * 铸件供应商产能溢出报表
     * @param
     * @param
     */
    @GetMapping("/supplierOutputReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "supplierOutputReport")
    R<IPage<SupplierOutputVo>> getsupplierOutputReport(SupplierOutputVo supplierOutputVo, Query query) {
        IPage<SupplierOutputVo> pages = reportService.getsupplierOutputReport(Condition.getPage(query), supplierOutputVo);
        return R.data(pages);
    }

    /**
     * 计算产能job
     * @return
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "supplierOutputReportjob")
    @Scheduled(cron="0 0 7 * * ?")
    //@PostConstruct
    void supplierOutputReportjob() {
        reportService.supplierOutputReportjob();
    }

    /**
     * 铸件供应商产能溢出图表
     * @param
     * @param
     */
    @GetMapping("/getsupplierOutputEcharts")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getsupplierOutputEcharts")
    public OutPutEchrtsOfSupplierVO getsupplierOutputEcharts(SupplierScheduleReq supplierScheduleReq) {
        return reportService.getsupplierOutputEcharts(supplierScheduleReq);
    }

    /**
     * 铸件模具管理报表
     * @param
     * @param
     */
    @GetMapping("/getMouldManagementWholeReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getMouldManagementWholeReport")
    R<IPage<MouldManagementWholeEntity>> getMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWholeEntity, Query query) {
        IPage<MouldManagementWholeEntity> pages = reportService.getMouldManagementWholeReport(Condition.getPage(query), mouldManagementWholeEntity);
        return R.data(pages);
    }


    /**
     * 铸件模具管理报表  新增
     * @param
     * @param
     */
    @PostMapping("/insertMouldManagementWholeReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "insertMouldManagementWholeReport")
    void insertMouldManagementWholeReport(@RequestBody MouldManagementWholeEntity mouldManagementWhole) {
        R.status(reportService.insertMouldManagementWholeReport(mouldManagementWhole));
    }

    /**
     * 铸件模具管理报表  更新
     * @param
     * @param
     */
    @PostMapping("/updateMouldManagementWholeReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "updateMouldManagementWholeReport")
    void updateMouldManagementWholeReport(@RequestBody MouldManagementWholeEntity mouldManagementWhole) {
        R.status(reportService.updateMouldManagementWholeReport(mouldManagementWhole));
    }

    /**
     * 铸件模具管理报表  导出
     * @param
     * @param
     */
    @GetMapping("/exportMouldManagementWholeReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "exportMouldManagementWholeReport")
    void exportMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWhole, HttpServletResponse response) {
        reportService.exportMouldManagementWholeReport(mouldManagementWhole,response);
    }


}
