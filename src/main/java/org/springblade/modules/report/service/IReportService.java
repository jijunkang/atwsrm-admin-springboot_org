package org.springblade.modules.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.modules.pr.dto.SubmitPriceReq;
import org.springblade.modules.report.dto.*;
import org.springblade.modules.report.entity.*;
import org.springblade.modules.report.vo.SupplierOutputVo;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.vo.OmsEchrtsOfSupplierVO;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfSupplierVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 大数据库报表 服务类
 *
 * @author xianboss
 */
public interface IReportService {
    IPage<VmiReport> getVmiConsumeReport(IPage<VmiReport> page, VmiReportReq vmiReportReq);

    IPage<DeliverReport> getDeliverReport(IPage<DeliverReport> page, DeliverReportReq deliverReportReq);

    void deliverReportExport(DeliverReportReq deliverReportReq, HttpServletResponse response);

    IPage<ZJItemOtdReport> getZJItemOtdReport(IPage<ZJItemOtdReport> page, ZJItemOtdReportReq zjItemOtdReportReq);

    IPage<ItemDailyReport> getKeyItemDailyReport(IPage<ItemDailyReport> page, KeyItemReportReq keyItemReportReq);

    IPage<ItemDailyReport> getWWItemDailyReport(IPage<ItemDailyReport> page, KeyItemReportReq keyItemReportReq);

    IPage<ItemDailyReport> getNotKeyItemDailyReport(IPage<ItemDailyReport> page, KeyItemReportReq keyItemReportReq);

    IPage<CaiGouSchedule> getItemDailyDetailReport(IPage<ItemDailyReport> page, CaiGouScheduleReq caiGouScheduleReq);

    IPage<QZReport> getQZReport(IPage<QZReport> page, QZReq qzReq);

    IPage<OrderOtdReport> getOrderOtdReport(IPage<OrderOtdReport> page, OrderOtdReq orderOtdReq);
    void exportOrderOtd(OrderOtdReq orderOtdReq, HttpServletResponse response);

    void exportQZ(QZReq qzReq, HttpServletResponse response);

    void exportItemDailyDetailReport(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response);

    void exportKeyItem(KeyItemReportReq keyItemReportReq, HttpServletResponse response);
    IPage<AutoOrderOtdReport> getAutoOrderOtdReport(IPage<AutoOrderOtdReport> page, AutoOrderOtdReq autoOrderOtdReq);

    void exportAutoOrderOtd(AutoOrderOtdReq autoOrderOtdReq, HttpServletResponse response);

    IPage<OrderAmountOtdReport> getOrderAmountOtdReport(IPage<OrderAmountOtdReport> page,OrderAmountOtdReportReq orderAmountOtdReportReq);

    void exportOrderAmountOtdReport(OrderAmountOtdReportReq orderAmountOtdReportReq, HttpServletResponse response);


    IPage<MouldManagementEntity> getMouldManagementReport(IPage<MouldManagementEntity> page, MouldManagementEntity mouldManagementEntity);

    void exportMouldManagementReport(MouldManagementEntity mouldManagement, HttpServletResponse response);

    Boolean deleteMouldManagementReport(List<MouldManagementEntity> mouldManagementEntities);


    Boolean insertMouldManagementReport(List<MouldManagementEntity> mouldManagementEntities);


    Boolean updateMouldManagementReport(List<MouldManagementEntity> mouldManagementEntities);


    IPage<MouldManagementWholeEntity> getMouldManagementWholeReport(IPage<MouldManagementWholeEntity> page, MouldManagementWholeEntity MouldManagementWholeEntity);

    void exportMouldManagementWholeReport(MouldManagementWholeEntity MouldManagementWhole, HttpServletResponse response);

    Boolean deleteMouldManagementWholeReport(List<MouldManagementWholeEntity> MouldManagementWholeEntities);
    Boolean insertMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWhole);
    Boolean updateMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWhole);

    IPage<JitManagementEntity> getJitManagementReport(IPage<JitManagementEntity> page, JitManagementEntity JitManagementEntity);

    void exportJitManagementReport(JitManagementEntity JitManagement, HttpServletResponse response);

    Boolean deleteJitManagementReport(List<JitManagementEntity> JitManagementEntities);


    Boolean insertJitManagementReport(List<JitManagementEntity> JitManagementEntities);


    Boolean updateJitManagementReport(List<JitManagementEntity> JitManagementEntities);

    IPage<SupplierOutputVo> getsupplierOutputReport(IPage<SupplierOutputVo> page, SupplierOutputVo supplierOutputVo);

    void supplierOutputReportjob();

    OutPutEchrtsOfSupplierVO getsupplierOutputEcharts(SupplierScheduleReq supplierScheduleReq);
}
