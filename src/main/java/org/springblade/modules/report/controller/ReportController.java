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
import org.springblade.modules.report.vo.SupplierOutputQZVo;
import org.springblade.modules.report.vo.SupplierOutputVo;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfQZVO;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfDjVO;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfPtphVO;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfSupplierVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * ?????? ?????????
 *
 * 2022/1/13 ?????? ????????????????????????Controller
 * @author zlw
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-report/report")
@Api(value = "??????", tags = "??????")
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
     * VMI???????????????
     */
    @GetMapping("/vmiConsume")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "CaiGouScheduleReq")
    public
    R<IPage<VmiReport>> vmiConsumeReport(VmiReportReq vmiReportReq, Query query) throws RuntimeException{
        IPage<VmiReport> pages = reportService.getVmiConsumeReport(Condition.getPage(query), vmiReportReq);
        return R.data(pages);
    }

    /**
     * ???????????????
     */
    @GetMapping("/deliverReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "DeliverReportReq")
    public
    R<IPage<DeliverReport>> deliverReport(DeliverReportReq deliverReportReq, Query query) throws RuntimeException{
        IPage<DeliverReport> pages = reportService.getDeliverReport(Condition.getPage(query), deliverReportReq);
        return R.data(pages);
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/deliverReportExport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "DeliverReportReq")
    public void deliverReportExport(DeliverReportReq deliverReportReq , HttpServletResponse response) {
        reportService.deliverReportExport(deliverReportReq, response);
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/zjOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "DeliverReportReq")
    public
    R<IPage<ZJItemOtdReport>> zjOtdReport(ZJItemOtdReportReq zjItemOtdReportReq, Query query) throws RuntimeException{
        IPage<ZJItemOtdReport> pages = reportService.getZJItemOtdReport(Condition.getPage(query), zjItemOtdReportReq);
        return R.data(pages);
    }

    /**
     * ???????????????????????? - ??????
     */
    @GetMapping("/keyItemDailyReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "DeliverReportReq")
    public
    R<IPage<ItemDailyReport>> keyItemDailyReport(KeyItemReportReq keyItemReportReq, Query query) throws RuntimeException{
        IPage<ItemDailyReport> page = reportService.getKeyItemDailyReport(Condition.getPage(query),keyItemReportReq);
        return R.data(page);
    }

    /**
     * ????????????????????? - ??????
     */
    @GetMapping("/exportKeyItem")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportKeyItem(KeyItemReportReq keyItemReportReq, HttpServletResponse response) {
        reportService.exportKeyItem(keyItemReportReq, response);
    }

    /**
     * ??????????????????????????? - ??????
     */
    @GetMapping("/notKeyItemDailyReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "DeliverReportReq")
    public
    R<IPage<ItemDailyReport>> notKeyItemDailyReport(KeyItemReportReq keyItemReportReq, Query query) throws RuntimeException{
        IPage<ItemDailyReport> page = reportService.getNotKeyItemDailyReport(Condition.getPage(query),keyItemReportReq);
        return R.data(page);
    }

    /**
     * ???????????????????????? - ??????
     */
    @GetMapping("/wwItemDailyReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "DeliverReportReq")
    public
    R<IPage<ItemDailyReport>> wwItemDailyReport(KeyItemReportReq keyItemReportReq, Query query) throws RuntimeException{
        IPage<ItemDailyReport> page = reportService.getWWItemDailyReport(Condition.getPage(query),keyItemReportReq);
        return R.data(page);
    }


    /**
     * ????????????????????????
     */
    @GetMapping("/itemDailyDetailReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouScheduleOffset(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = reportService.getItemDailyDetailReport(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }


    /**
     * ??????????????????????????????
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
     * ??????????????????
     * @param poReceive
     * @param query
     * @return
     * @Desc maily
     */
    @GetMapping("/getList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "??????poReceive")
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
     * ????????????????????????
     */
    @GetMapping("/qzReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "CaiGouScheduleReq")
    public
    R<IPage<QZReport>> caiGouScheduleOffset(QZReq qzReq, Query query) throws RuntimeException{
        IPage<QZReport> pages = reportService.getQZReport(Condition.getPage(query), qzReq);
        return R.data(pages);
    }

    /**
     * ??????????????????????????????
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
     * ?????????????????????
     */
    @GetMapping("/orderOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "CaiGouScheduleReq")
    public
    R<IPage<OrderOtdReport>> orderOtdReport(OrderOtdReq orderOtdReq, Query query) throws RuntimeException{
        IPage<OrderOtdReport> pages = reportService.getOrderOtdReport(Condition.getPage(query), orderOtdReq);
        return R.data(pages);
    }



    /**
     * ????????????????????? ??????
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
     * ??????????????? ??????
     */
    @GetMapping("/orderOtdStatistics")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "CaiGouScheduleReq")
    public
    R<IPage<OrderOtdReport>> orderOtdStatistics(OrderOtdReq orderOtdReq, Query query) throws RuntimeException{
        IPage<OrderOtdReport> pages = reportService.getOrderOtdReport(Condition.getPage(query), orderOtdReq);
        return R.data(pages);
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/autoOrderOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "CaiGouScheduleReq")
    public
    R<IPage<AutoOrderOtdReport>> autoOrderOtdReport(AutoOrderOtdReq autoOrderOtdReq, Query query) throws RuntimeException{
        IPage<AutoOrderOtdReport> pages = reportService.getAutoOrderOtdReport(Condition.getPage(query), autoOrderOtdReq);
        return R.data(pages);
    }


    /**
     * ????????????????????? ??????
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
     * ?????????????????????
     */
    @GetMapping("/orderAmountOtdReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "orderAmountOtdReport")
    public
    R<IPage<OrderAmountOtdReport>> orderAmountOtdReport(OrderAmountOtdReportReq orderAmountOtdReportReq, Query query) throws RuntimeException{
        IPage<OrderAmountOtdReport> pages = reportService.getOrderAmountOtdReport(Condition.getPage(query), orderAmountOtdReportReq);
        return R.data(pages);
    }


    /**
     * ????????????????????? ??????
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
     * ??????????????????
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
     * ??????????????????  ??????
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
     * ??????????????????  ??????
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
     * ??????????????????  ??????
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
     * ??????????????????  ??????
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
     * Jit????????????
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
     * Jit????????????  ??????
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
     * Jit????????????  ??????
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
     * Jit????????????  ??????
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
     * Jit????????????  ??????
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
     * ?????????????????????????????????
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
     * ????????????job
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
     * ?????????????????????????????????
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
     * ????????????????????????
     * @param
     * @param
     */
    @GetMapping("/getPtphOutputEcharts")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getPtphOutputEcharts")
    public OutPutEchrtsOfPtphVO getPtphOutputEcharts(SupplierScheduleReq supplierScheduleReq) {
        return reportService.getPtphOutputEcharts(supplierScheduleReq);
    }

    /**
     * ??????????????????
     * @param
     * @param
     */
    @GetMapping("/getDjOutputEcharts")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getDjOutputEcharts")
    public OutPutEchrtsOfDjVO getDjOutputEcharts(SupplierScheduleReq supplierScheduleReq) {
        return reportService.getDjOutputEcharts(supplierScheduleReq);
    }

    /**
     * ????????????????????????
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
     * ????????????????????????  ??????
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
     * ????????????????????????  ??????
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
     * ????????????????????????  ??????
     * @param
     * @param
     */
    @GetMapping("/exportMouldManagementWholeReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "exportMouldManagementWholeReport")
    void exportMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWhole, HttpServletResponse response) {
        reportService.exportMouldManagementWholeReport(mouldManagementWhole,response);
    }

    /**
     * ????????????job(??????)
     * @return
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "supplierOutputReportQZJob")
    @Scheduled(cron="0 0 7 * * ?")
    void supplierOutputReportQZJob() {
        reportService.supplierOutputReportQZJob();
    }

    /**
     * ?????????????????????????????????
     * @param
     */
    @GetMapping("/qzOutputReport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "qzOutputReport")
    R<List<SupplierOutputQZVo>> getQZOutputReport(SupplierOutputVo supplierOutputVo, Query query) {
        List<SupplierOutputQZVo> pages = reportService.getQZOutputReport(Condition.getPage(query), supplierOutputVo);
        return R.data(pages);
    }

    /**
     * ?????????????????????????????????
     * @param
     * @return
     */
    @GetMapping("/getQZOutputEcharts")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "getsupplierOutputEcharts")
    public OutPutEchrtsOfQZVO getQZOutputEcharts(SupplierScheduleReq supplierScheduleReq) {
        return reportService.getQZOutputEcharts(supplierScheduleReq);
    }

}
