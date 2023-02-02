package org.springblade.modules.report.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.utils.CommonUtil;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.pr.entity.ItemInfoEntityOfQZ;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;
import org.springblade.modules.report.dto.*;
import org.springblade.modules.report.entity.*;
import org.springblade.modules.report.mapper.ReportMapper;
import org.springblade.modules.report.service.IReportService;
import org.springblade.modules.report.vo.KeyItemFixedExcel;
import org.springblade.modules.report.vo.SupplierOutputQZVo;
import org.springblade.modules.report.vo.SupplierOutputVo;
import org.springblade.modules.supplier.dto.CaiGouScheduleExcel;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;

import org.springblade.modules.supplier.vo.OutPutEchrtsOfDjVO;
import org.springblade.modules.supplier.vo.OutPutEchrtsOfPtphVO;

import org.springblade.modules.supplier.vo.OutPutEchrtsOfQZVO;

import org.springblade.modules.supplier.vo.OutPutEchrtsOfSupplierVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


import static cn.hutool.core.date.DateUtil.offsetMonth;

import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfQiuZuo;

import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfZhuDuanJian;
import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * 报表 服务实现类
 *
 * @author Will
 */
@Service
public
class IReportServiceImpl implements IReportService {

    @Value("${oracle.url}")
    private String oracleUrl;

    @Value("${oracle.user}")

    private String oracleUser;

    @Value("${oracle.password}")
    private String oraclePassword;

    @Value("${oracle.driver}")
    private String oracleDriver;

    @Autowired
    private ReportMapper reportMapper;


    @Override
    public IPage<VmiReport> getVmiConsumeReport(IPage<VmiReport> page, VmiReportReq vmiReportReq) {

        String supCode = SecureUtil.getTenantId();

        List<VmiReport> vmiReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();

            sqlListBuilder.append("select * from (select s.*,ROWNUM AS rowno from atwrpt.vu_srm_vmi_consume s where 1=1");
            sqlCountBuilder.append("select count(*) total from atwrpt.vu_srm_vmi_consume where 1=1");

            // 供应商编码
            if (vmiReportReq.getSupCode() != null && !vmiReportReq.getSupCode().equals("")) {
                sqlListBuilder.append(" and supCode like '%").append(vmiReportReq.getSupCode()).append("%'");
                sqlCountBuilder.append(" and supCode like '%").append(vmiReportReq.getSupCode()).append("%'");
            }
            // 供应商名称
            if (vmiReportReq.getSupName() != null && !vmiReportReq.getSupName().equals("")) {
                sqlListBuilder.append(" and supName like '%").append(vmiReportReq.getSupName()).append("%'");
                sqlCountBuilder.append(" and supName like '%").append(vmiReportReq.getSupName()).append("%'");
            }
            // 物料编码
            if (vmiReportReq.getItemCode() != null && !vmiReportReq.getItemCode().equals("")) {
                sqlListBuilder.append(" and itemCode like '%").append(vmiReportReq.getItemCode()).append("%'");
                sqlCountBuilder.append(" and itemCode like '%").append(vmiReportReq.getItemCode()).append("%'");
            }
            // 物料名称
            if (vmiReportReq.getItemName() != null && !vmiReportReq.getItemName().equals("")) {
                sqlListBuilder.append(" and itemName like '%").append(vmiReportReq.getItemName()).append("%'");
                sqlCountBuilder.append(" and itemName like '%").append(vmiReportReq.getItemName()).append("%'");
            }

            // 分页
            long pageIndex = page.getCurrent();
            long pageSize = page.getSize();
            int start = new Long((pageIndex - 1) * pageSize).intValue();
            int end = new Long((pageIndex - 1) * pageSize + pageSize).intValue();
            sqlListBuilder.append(") where rowno> ").append(start).append(" and rowno<= ").append(end);

            int total = 0;

            //3.执行
            @Cleanup ResultSet resultSetCount = stmt.executeQuery(sqlCountBuilder.toString());
            while (resultSetCount.next()) {
                total = resultSetCount.getInt("total");
            }

            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                VmiReport vmiReport = new VmiReport();
                vmiReport.setSupCode(resultSetList.getString("supCode"));
                vmiReport.setSupName(resultSetList.getString("supName"));
                vmiReport.setItemCode(resultSetList.getString("itemCode"));
                vmiReport.setItemName(resultSetList.getString("itemName"));
                vmiReport.setPoNum(resultSetList.getString("purQty"));// 采购总数量
                vmiReport.setStoreTotalNum(resultSetList.getString("rcvQty"));// 入库总数量
                vmiReport.setStoreNum(resultSetList.getString("storeQty"));// 库存数量
                vmiReport.setBalNum(resultSetList.getString("balQty"));// 结算数量
                vmiReport.setUsedButNotBalNum(new BigDecimal(vmiReport.getStoreTotalNum()).subtract(new BigDecimal(vmiReport.getStoreNum())).subtract(new BigDecimal(vmiReport.getBalNum())).toString());// 已用但未结算数量
                vmiReport.setNotBalNum(new BigDecimal(vmiReport.getStoreTotalNum()).subtract(new BigDecimal(vmiReport.getBalNum())).toString());// 未结算数量
                vmiReports.add(vmiReport);
            }

            page.setRecords(vmiReports);
            page.setTotal(total);

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return page;
        }
    }

    @Override
    public IPage<DeliverReport> getDeliverReport(IPage<DeliverReport> page, DeliverReportReq deliverReportReq) {
        List<DeliverReport> deliverReports = new ArrayList<>();

        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();

            sqlListBuilder.append("select * from (");
            sqlListBuilder.append(" select s.*,r.sup_code supCode,r.sup_name supName,r.rcv_num rcvNum,nvl(r.create_time_record,create_time) doTime,r.status doStatus,row_number() over(order by docode desc) AS rowno from atwrpt.vu_sndoc_rpt s");
            sqlListBuilder.append(" left join atwsrm.atw_po_receive r on s.docode = r.rcv_code and s.piid = r.pi_id and nvl(s.heatcode,0)=nvl(r.heat_code,0)  and r.status<>40");
            sqlListBuilder.append(" where 1=1 and r.is_deleted=0 ");

            sqlCountBuilder.append("select count(*) total from (");
            sqlCountBuilder.append(" select s.*,r.sup_code supCode,r.sup_name supName,r.rcv_num rcvNum,ROWNUM AS rowno from atwrpt.vu_sndoc_rpt s");
            sqlCountBuilder.append(" left join atwsrm.atw_po_receive r on s.docode = r.rcv_code and s.piid = r.pi_id and nvl(s.heatcode,0)=nvl(r.heat_code,0) and r.status<>40");
            sqlCountBuilder.append(" where 1=1 and r.is_deleted=0 ");


            // 送货单号
            if (deliverReportReq.getDoCode() != null && !deliverReportReq.getDoCode().equals("")) {
                sqlListBuilder.append(" and s.DOCODE like '%").append(deliverReportReq.getDoCode()).append("%'");
                sqlCountBuilder.append(" and s.DOCODE like '%").append(deliverReportReq.getDoCode()).append("%'");
            }
            // 供应商编码
            if (deliverReportReq.getSupCode() != null && !deliverReportReq.getSupCode().equals("")) {
                sqlListBuilder.append(" and r.sup_code like '%").append(deliverReportReq.getSupCode()).append("%'");
                sqlCountBuilder.append(" and r.sup_code like '%").append(deliverReportReq.getSupCode()).append("%'");
            }
            // 供应商名称
            if (deliverReportReq.getSupName() != null && !deliverReportReq.getSupName().equals("")) {
                sqlListBuilder.append(" and r.sup_name like '%").append(deliverReportReq.getSupName()).append("%'");
                sqlCountBuilder.append(" and r.sup_name like '%").append(deliverReportReq.getSupName()).append("%'");
            }
            // 物料编码
            if (deliverReportReq.getItemCode() != null && !deliverReportReq.getItemCode().equals("")) {
                sqlListBuilder.append(" and s.itemCode like '%").append(deliverReportReq.getItemCode()).append("%'");
                sqlCountBuilder.append(" and s.itemCode like '%").append(deliverReportReq.getItemCode()).append("%'");
            }
            // 物料名称
            if (deliverReportReq.getItemName() != null && !deliverReportReq.getItemName().equals("")) {
                sqlListBuilder.append(" and s.itemName like '%").append(deliverReportReq.getItemName()).append("%'");
                sqlCountBuilder.append(" and s.itemName like '%").append(deliverReportReq.getItemName()).append("%'");
            }
            // 订单单号
            if (deliverReportReq.getPoCode() != null && !deliverReportReq.getPoCode().equals("")) {
                sqlListBuilder.append(" and s.pocode like '%").append(deliverReportReq.getPoCode()).append("%'");
                sqlCountBuilder.append(" and s.pocode like '%").append(deliverReportReq.getPoCode()).append("%'");
            }
            // 炉批号
            if (deliverReportReq.getHeatCode() != null && !deliverReportReq.getHeatCode().equals("")) {
                sqlListBuilder.append(" and s.heatCode like '%").append(deliverReportReq.getHeatCode()).append("%'");
                sqlCountBuilder.append(" and s.heatCode like '%").append(deliverReportReq.getHeatCode()).append("%'");
            }
            // 报检单号
            if (deliverReportReq.getSnCode() != null && !deliverReportReq.getSnCode().equals("")) {
                sqlListBuilder.append(" and s.sncode like '%").append(deliverReportReq.getSnCode()).append("%'");
                sqlCountBuilder.append(" and s.sncode like '%").append(deliverReportReq.getSnCode()).append("%'");
            }
            // 入库单号
            if (deliverReportReq.getStoreCode() != null && !deliverReportReq.getStoreCode().equals("")) {
                sqlListBuilder.append(" and s.docno like '%").append(deliverReportReq.getStoreCode()).append("%'");
                sqlCountBuilder.append(" and s.docno like '%").append(deliverReportReq.getStoreCode()).append("%'");
            }

            // 分页
            long pageIndex = page.getCurrent();
            long pageSize = page.getSize();
            int start = new Long((pageIndex - 1) * pageSize).intValue();
            int end = new Long((pageIndex - 1) * pageSize + pageSize).intValue();
            sqlListBuilder.append(") where rowno> ").append(start).append(" and rowno<= ").append(end);
            sqlCountBuilder.append(")");

            int total = 0;

            //3.执行
            @Cleanup ResultSet resultSetCount = stmt.executeQuery(sqlCountBuilder.toString());
            while (resultSetCount.next()) {
                total = resultSetCount.getInt("total");
            }

            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                DeliverReport deliverReport = new DeliverReport();

                deliverReport.setDoCode(resultSetList.getString("doCode"));
                deliverReport.setSupCode(resultSetList.getString("supCode"));
                deliverReport.setSupName(resultSetList.getString("supName"));
                deliverReport.setPoCode(resultSetList.getString("poCode"));
                deliverReport.setPoLn(resultSetList.getString("poLn"));
                deliverReport.setItemCode(resultSetList.getString("itemCode"));
                deliverReport.setItemName(resultSetList.getString("itemName"));
                deliverReport.setDoNum(resultSetList.getString("rcvNum"));
                deliverReport.setHeatCode(resultSetList.getString("heatCode"));
                deliverReport.setDoTime(resultSetList.getString("doTime"));
                deliverReport.setDoStatus(resultSetList.getString("doStatus"));
                deliverReport.setSnTime(resultSetList.getString("mtime"));
                deliverReport.setSnCode(resultSetList.getString("snCode"));
                deliverReport.setSnLn(resultSetList.getString("snLn"));
                deliverReport.setSnNum(resultSetList.getString("rcvNum"));
                deliverReport.setStoreStatus(resultSetList.getString("u9Flag"));
                deliverReport.setStoreCode(resultSetList.getString("docNo"));
                deliverReport.setStoreLn(resultSetList.getString("docLn"));
                deliverReport.setStoreQty(resultSetList.getString("storeQty"));
                deliverReport.setStoreTime(resultSetList.getString("createDon"));
                deliverReports.add(deliverReport);
            }

            page.setRecords(deliverReports);
            page.setTotal(total);

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return page;
        }
    }


    @Override
    public void deliverReportExport(DeliverReportReq deliverReportReq, HttpServletResponse response) {
        List<DeliverReport> deliverReports = new ArrayList<>();

        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();

            sqlListBuilder.append("select * from (");
            sqlListBuilder.append(" select s.*,r.sup_code supCode,r.sup_name supName,r.rcv_num rcvNum,nvl(r.create_time_record,create_time) doTime,r.status doStatus,row_number() over(order by docode desc) AS rowno from atwrpt.vu_sndoc_rpt s");
            sqlListBuilder.append(" left join atwsrm.atw_po_receive r on s.docode = r.rcv_code and s.piid = r.pi_id and nvl(s.heatcode,0)=nvl(r.heat_code,0)  and r.status<>40");
            sqlListBuilder.append(" where 1=1 and r.is_deleted=0 ");

            sqlCountBuilder.append("select count(*) total from (");
            sqlCountBuilder.append(" select s.*,r.sup_code supCode,r.sup_name supName,r.rcv_num rcvNum,ROWNUM AS rowno from atwrpt.vu_sndoc_rpt s");
            sqlCountBuilder.append(" left join atwsrm.atw_po_receive r on s.docode = r.rcv_code and s.piid = r.pi_id and nvl(s.heatcode,0)=nvl(r.heat_code,0) and r.status<>40");
            sqlCountBuilder.append(" where 1=1 and r.is_deleted=0 ");


            // 送货单号
            if (deliverReportReq.getDoCode() != null && !deliverReportReq.getDoCode().equals("")) {
                sqlListBuilder.append(" and s.DOCODE like '%").append(deliverReportReq.getDoCode()).append("%'");
                sqlCountBuilder.append(" and s.DOCODE like '%").append(deliverReportReq.getDoCode()).append("%'");
            }
            // 供应商编码
            if (deliverReportReq.getSupCode() != null && !deliverReportReq.getSupCode().equals("")) {
                sqlListBuilder.append(" and r.sup_code like '%").append(deliverReportReq.getSupCode()).append("%'");
                sqlCountBuilder.append(" and r.sup_code like '%").append(deliverReportReq.getSupCode()).append("%'");
            }
            // 供应商名称
            if (deliverReportReq.getSupName() != null && !deliverReportReq.getSupName().equals("")) {
                sqlListBuilder.append(" and r.sup_name like '%").append(deliverReportReq.getSupName()).append("%'");
                sqlCountBuilder.append(" and r.sup_name like '%").append(deliverReportReq.getSupName()).append("%'");
            }
            // 物料编码
            if (deliverReportReq.getItemCode() != null && !deliverReportReq.getItemCode().equals("")) {
                sqlListBuilder.append(" and s.itemCode like '%").append(deliverReportReq.getItemCode()).append("%'");
                sqlCountBuilder.append(" and s.itemCode like '%").append(deliverReportReq.getItemCode()).append("%'");
            }
            // 物料名称
            if (deliverReportReq.getItemName() != null && !deliverReportReq.getItemName().equals("")) {
                sqlListBuilder.append(" and s.itemName like '%").append(deliverReportReq.getItemName()).append("%'");
                sqlCountBuilder.append(" and s.itemName like '%").append(deliverReportReq.getItemName()).append("%'");
            }
            // 订单单号
            if (deliverReportReq.getPoCode() != null && !deliverReportReq.getPoCode().equals("")) {
                sqlListBuilder.append(" and s.pocode like '%").append(deliverReportReq.getPoCode()).append("%'");
                sqlCountBuilder.append(" and s.pocode like '%").append(deliverReportReq.getPoCode()).append("%'");
            }
            // 炉批号
            if (deliverReportReq.getHeatCode() != null && !deliverReportReq.getHeatCode().equals("")) {
                sqlListBuilder.append(" and s.heatCode like '%").append(deliverReportReq.getHeatCode()).append("%'");
                sqlCountBuilder.append(" and s.heatCode like '%").append(deliverReportReq.getHeatCode()).append("%'");
            }
            // 报检单号
            if (deliverReportReq.getSnCode() != null && !deliverReportReq.getSnCode().equals("")) {
                sqlListBuilder.append(" and s.sncode like '%").append(deliverReportReq.getSnCode()).append("%'");
                sqlCountBuilder.append(" and s.sncode like '%").append(deliverReportReq.getSnCode()).append("%'");
            }
            // 入库单号
            if (deliverReportReq.getStoreCode() != null && !deliverReportReq.getStoreCode().equals("")) {
                sqlListBuilder.append(" and s.docno like '%").append(deliverReportReq.getStoreCode()).append("%'");
                sqlCountBuilder.append(" and s.docno like '%").append(deliverReportReq.getStoreCode()).append("%'");
            }

            // 分页
            sqlListBuilder.append(")");
            sqlCountBuilder.append(")");

            int total = 0;

            //3.执行
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                DeliverReport deliverReport = new DeliverReport();

                deliverReport.setDoCode(resultSetList.getString("doCode"));
                deliverReport.setSupCode(resultSetList.getString("supCode"));
                deliverReport.setSupName(resultSetList.getString("supName"));
                deliverReport.setPoCode(resultSetList.getString("poCode"));
                deliverReport.setPoLn(resultSetList.getString("poLn"));
                deliverReport.setItemCode(resultSetList.getString("itemCode"));
                deliverReport.setItemName(resultSetList.getString("itemName"));
                deliverReport.setDoNum(resultSetList.getString("rcvNum"));
                deliverReport.setHeatCode(resultSetList.getString("heatCode"));
                deliverReport.setDoTime(resultSetList.getString("doTime"));
                int doStatus = Integer.valueOf(resultSetList.getString("doStatus"));
                String statusValue = "";
                switch (doStatus) {
                    case 20:
                        statusValue = "送货中";
                        break;
                    case 21:
                        statusValue = "厂内已报检";
                        break;
                    case 22:
                        statusValue = "厂外已报检";
                        break;
                    case 23:
                        statusValue = "厂内已检验";
                        break;
                    case 24:
                        statusValue = "厂外已检验";
                        break;
                    case 25:
                        statusValue = "处理中";
                        break;
                    case 26:
                        statusValue = "已点收";
                        break;
                    case 27:
                        statusValue = "虚拟已入库";
                        break;
                    case 30:
                        statusValue = "已关闭";
                        break;
                    case 40:
                        statusValue = "已作废";
                        break;
                }
                deliverReport.setDoStatus(statusValue);
                deliverReport.setSnTime(resultSetList.getString("mtime"));
                deliverReport.setSnCode(resultSetList.getString("snCode"));
                deliverReport.setSnLn(resultSetList.getString("snLn"));
                deliverReport.setSnNum(resultSetList.getString("rcvNum"));
                deliverReport.setStoreStatus(resultSetList.getString("u9Flag"));
                deliverReport.setStoreCode(resultSetList.getString("docNo"));
                deliverReport.setStoreLn(resultSetList.getString("docLn"));
                deliverReport.setStoreQty(resultSetList.getString("storeQty"));
                deliverReport.setStoreTime(resultSetList.getString("createDon"));
                deliverReports.add(deliverReport);
            }

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            ExcelUtils.defaultExport(deliverReports, DeliverReport.class, "送货单报表" + DateUtil.formatDate(new Date()), response);
        }
    }

    @Override
    public IPage<ZJItemOtdReport> getZJItemOtdReport(IPage<ZJItemOtdReport> page, ZJItemOtdReportReq zjItemOtdReportReq) {
        List<ZJItemOtdReport> zjItemOtdReports = new ArrayList<>();

        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        if (zjItemOtdReportReq.getReqDateStart() != null && !zjItemOtdReportReq.getReqDateStart().isEmpty()) {

        }

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();
            StringBuilder sqlCountHgBuilder = new StringBuilder();

            sqlListBuilder.append(" select * from (");
            sqlListBuilder.append(" select p.*,row_number() over(order by item_code desc) AS rowno from (");
            sqlListBuilder.append(" select t.item_code,t.item_name,t.fenlei,t.xilie,t.chicun,t.xingshi,t.bangji,t.falan,t.caizhi,t.hgnum,t.zhnum from");
            sqlListBuilder.append(" (select rpt.*,count(1) over (partition by item_code) zhnum,sum(case when isotd='Y' then 1 else 0 end ) over (partition by item_code) hgnum from atwrpt.vu_zj_item_otd_rpt rpt ");
            if (zjItemOtdReportReq.getReqDateStart() != null && !zjItemOtdReportReq.getReqDateStart().isEmpty()) {
                sqlListBuilder
                    .append(" where substr(reqdate,1,10)  >= '").append(zjItemOtdReportReq.getReqDateStart()).append("'")
                    .append(" AND substr(reqdate,1,10)  <= '").append(zjItemOtdReportReq.getReqDateEnd()).append("'");
            }
            sqlListBuilder.append(" )t ");
            sqlListBuilder.append(" group by t.item_code,t.item_name,t.fenlei,t.xilie,t.chicun,t.xingshi,t.bangji,t.falan,t.caizhi,t.hgnum,t.zhnum ");
            sqlListBuilder.append(" ) p ");
            sqlListBuilder.append(" where 1=1 ");

            sqlCountBuilder.append(" select count(*) total from (");
            sqlCountBuilder.append(" select p.*,row_number() over(order by item_code desc) AS rowno from (");
            sqlCountBuilder.append(" select t.item_code,t.item_name,t.fenlei,t.xilie,t.chicun,t.xingshi,t.bangji,t.falan,t.caizhi,t.hgnum,t.zhnum from");
            sqlCountBuilder.append(" (select rpt.*,count(1) over (partition by item_code) zhnum,sum(case when isotd='Y' then 1 else 0 end ) over (partition by item_code) hgnum from atwrpt.vu_zj_item_otd_rpt rpt ");
            if (zjItemOtdReportReq.getReqDateStart() != null && !zjItemOtdReportReq.getReqDateStart().isEmpty()) {
                sqlCountBuilder
                    .append(" where substr(reqdate,1,10)  >= '").append(zjItemOtdReportReq.getReqDateStart()).append("'")
                    .append(" AND substr(reqdate,1,10)  <= '").append(zjItemOtdReportReq.getReqDateEnd()).append("'");
            }
            sqlCountBuilder.append(" )t ");
            sqlCountBuilder.append(" group by t.item_code,t.item_name,t.fenlei,t.xilie,t.chicun,t.xingshi,t.bangji,t.falan,t.caizhi,t.hgnum,t.zhnum ");
            sqlCountBuilder.append(" ) p ");
            sqlCountBuilder.append(" where 1=1 ");

            sqlCountHgBuilder.append(" select sum(hgnum) hgTotal,sum(zhnum) zhTotal from (");
            sqlCountHgBuilder.append(" select p.*,row_number() over(order by item_code desc) AS rowno from (");
            sqlCountHgBuilder.append(" select t.item_code,t.item_name,t.fenlei,t.xilie,t.chicun,t.xingshi,t.bangji,t.falan,t.caizhi,t.hgnum,t.zhnum from");
            sqlCountHgBuilder.append(" (select rpt.*,count(1) over (partition by item_code) zhnum,sum(case when isotd='Y' then 1 else 0 end ) over (partition by item_code) hgnum from atwrpt.vu_zj_item_otd_rpt rpt ");
            if (zjItemOtdReportReq.getReqDateStart() != null && !zjItemOtdReportReq.getReqDateStart().isEmpty()) {
                sqlCountHgBuilder
                    .append(" where substr(reqdate,1,10)  >= '").append(zjItemOtdReportReq.getReqDateStart()).append("'")
                    .append(" AND substr(reqdate,1,10)  <= '").append(zjItemOtdReportReq.getReqDateEnd()).append("'");
            }
            sqlCountHgBuilder.append(" )t ");
            sqlCountHgBuilder.append(" group by t.item_code,t.item_name,t.fenlei,t.xilie,t.chicun,t.xingshi,t.bangji,t.falan,t.caizhi,t.hgnum,t.zhnum ");
            sqlCountHgBuilder.append(" ) p ");
            sqlCountHgBuilder.append(" where 1=1 ");

            // 物料号
            if (zjItemOtdReportReq.getItemCode() != null && !zjItemOtdReportReq.getItemCode().equals("")) {
                sqlListBuilder.append(" and item_code like '%").append(zjItemOtdReportReq.getItemCode()).append("%'");
                sqlCountBuilder.append(" and item_code like '%").append(zjItemOtdReportReq.getItemCode()).append("%'");
                sqlCountHgBuilder.append(" and item_code like '%").append(zjItemOtdReportReq.getItemCode()).append("%'");
            }

            // 物料描述
            if (zjItemOtdReportReq.getItemName() != null && !zjItemOtdReportReq.getItemName().equals("")) {
                sqlListBuilder.append(" and item_name like '%").append(zjItemOtdReportReq.getItemName()).append("%'");
                sqlCountBuilder.append(" and item_name like '%").append(zjItemOtdReportReq.getItemName()).append("%'");
                sqlCountHgBuilder.append(" and item_name like '%").append(zjItemOtdReportReq.getItemName()).append("%'");
            }

            // 物料分类
            if (zjItemOtdReportReq.getItemize() != null && !zjItemOtdReportReq.getItemize().equals("")) {
                sqlListBuilder.append(" and fenlei like '%").append(zjItemOtdReportReq.getItemize()).append("%'");
                sqlCountBuilder.append(" and fenlei like '%").append(zjItemOtdReportReq.getItemize()).append("%'");
                sqlCountHgBuilder.append(" and fenlei like '%").append(zjItemOtdReportReq.getItemize()).append("%'");
            }

            // 尺寸
            if (zjItemOtdReportReq.getItemSize() != null && !zjItemOtdReportReq.getItemSize().equals("")) {
                if (zjItemOtdReportReq.getItemSize().indexOf("~") > -1) {
                    String minSize = zjItemOtdReportReq.getItemSize().split("~")[0];
                    String maxSize = zjItemOtdReportReq.getItemSize().split("~")[1];
                    sqlListBuilder.append(" and chicun >= ").append(minSize).append(" and chicun <= ").append(maxSize);
                    sqlCountBuilder.append(" and chicun >= ").append(minSize).append(" and chicun <= ").append(maxSize);
                    sqlCountHgBuilder.append(" and chicun >= ").append(minSize).append(" and chicun <= ").append(maxSize);
                } else {
                    sqlListBuilder.append(" and chicun = '").append(zjItemOtdReportReq.getItemSize()).append("'");
                    sqlCountBuilder.append(" and chicun = '").append(zjItemOtdReportReq.getItemSize()).append("'");
                    sqlCountHgBuilder.append(" and chicun = '").append(zjItemOtdReportReq.getItemSize()).append("'");
                }
            }

            // 形式
            if (zjItemOtdReportReq.getForm() != null && !zjItemOtdReportReq.getForm().equals("")) {
                sqlListBuilder.append(" and xingshi = '").append(zjItemOtdReportReq.getForm()).append("'");
                sqlCountBuilder.append(" and xingshi = '").append(zjItemOtdReportReq.getForm()).append("'");
                sqlCountHgBuilder.append(" and xingshi = '").append(zjItemOtdReportReq.getForm()).append("'");
            }

            // 磅级
            if (zjItemOtdReportReq.getPound() != null && !zjItemOtdReportReq.getPound().equals("")) {
                if (zjItemOtdReportReq.getPound().indexOf("~") > -1) {
                    String minPound = zjItemOtdReportReq.getPound().split("~")[0];
                    String maxPound = zjItemOtdReportReq.getPound().split("~")[1];
                    sqlListBuilder.append(" and bangji >= ").append(minPound).append(" and bangji <= ").append(maxPound);
                    sqlCountBuilder.append(" and bangji >= ").append(minPound).append(" and bangji <= ").append(maxPound);
                    sqlCountHgBuilder.append(" and bangji >= ").append(minPound).append(" and bangji <= ").append(maxPound);
                } else {
                    sqlListBuilder.append(" and bangji = '").append(zjItemOtdReportReq.getPound()).append("'");
                    sqlCountBuilder.append(" and bangji = '").append(zjItemOtdReportReq.getPound()).append("'");
                    sqlCountHgBuilder.append(" and bangji = '").append(zjItemOtdReportReq.getPound()).append("'");
                }
            }

            // 法兰结构
            if (zjItemOtdReportReq.getFlange() != null && !zjItemOtdReportReq.getFlange().equals("")) {
                sqlListBuilder.append(" and falan = '").append(zjItemOtdReportReq.getFlange()).append("'");
                sqlCountBuilder.append(" and falan = '").append(zjItemOtdReportReq.getFlange()).append("'");
                sqlCountHgBuilder.append(" and falan = '").append(zjItemOtdReportReq.getFlange()).append("'");
            }

            // 系列
            if (zjItemOtdReportReq.getSeries() != null && !zjItemOtdReportReq.getSeries().equals("")) {
                sqlListBuilder.append(" and xilie = '").append(zjItemOtdReportReq.getSeries()).append("'");
                sqlCountBuilder.append(" and xilie = '").append(zjItemOtdReportReq.getSeries()).append("'");
                sqlCountHgBuilder.append(" and xilie = '").append(zjItemOtdReportReq.getSeries()).append("'");
            }

            // 材质
            if (zjItemOtdReportReq.getMaterial() != null && !zjItemOtdReportReq.getMaterial().equals("")) {
                sqlListBuilder.append(" and caizhi = '").append(zjItemOtdReportReq.getMaterial()).append("'");
                sqlCountBuilder.append(" and caizhi = '").append(zjItemOtdReportReq.getMaterial()).append("'");
                sqlCountHgBuilder.append(" and caizhi = '").append(zjItemOtdReportReq.getMaterial()).append("'");
            }

            // 分页
            long pageIndex = page.getCurrent();
            long pageSize = page.getSize();
            int start = new Long((pageIndex - 1) * pageSize).intValue();
            int end = new Long((pageIndex - 1) * pageSize + pageSize).intValue();

            sqlListBuilder.append(") e where rowno >").append(start).append(" and rowno<= ").append(end);
            sqlCountBuilder.append(") e");
            sqlCountHgBuilder.append(") e");

            int total = 0;
            int hgTotal = 0;
            int zhTotal = 0;

            //3.执行
            @Cleanup ResultSet resultSetCount = stmt.executeQuery(sqlCountBuilder.toString());
            while (resultSetCount.next()) {
                total = resultSetCount.getInt("total");
            }

            @Cleanup ResultSet resultSetHgCount = stmt.executeQuery(sqlCountHgBuilder.toString());
            while (resultSetHgCount.next()) {
                hgTotal = resultSetHgCount.getInt("hgTotal");
                zhTotal = resultSetHgCount.getInt("zhTotal");
            }
            String totalOtd = "";
            String totalOtdSon = String.valueOf(hgTotal);
            String totalOtdMother = String.valueOf(zhTotal);
            if (Integer.valueOf(zhTotal).equals(Integer.valueOf(hgTotal))) {
                totalOtd = "100%";
            } else {
                totalOtd = new BigDecimal(hgTotal).multiply(new BigDecimal("100").divide(new BigDecimal(zhTotal), BigDecimal.ROUND_HALF_UP, 1)).setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "%";
            }

            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                ZJItemOtdReport zjItemOtdReport = new ZJItemOtdReport();
                zjItemOtdReport.setItemCode(resultSetList.getString("item_code"));
                zjItemOtdReport.setItemName(resultSetList.getString("item_name"));
                zjItemOtdReport.setItemize(resultSetList.getString("fenlei"));
                zjItemOtdReport.setItemSize(resultSetList.getString("chicun"));
                zjItemOtdReport.setForm(resultSetList.getString("xingshi"));
                zjItemOtdReport.setPound(resultSetList.getString("bangji"));
                zjItemOtdReport.setFlange(resultSetList.getString("falan"));
                zjItemOtdReport.setSeries(resultSetList.getString("xilie"));
                zjItemOtdReport.setMaterial(resultSetList.getString("caizhi"));
                String hgNum = resultSetList.getString("hgNum");
                String zhNum = resultSetList.getString("zhNum");

                String otd = "";
                if (Integer.valueOf(hgNum).equals(Integer.valueOf(zhNum))) {
                    otd = "100%";
                } else {
                    otd = new BigDecimal(hgNum).multiply(new BigDecimal("100").divide(new BigDecimal(zhNum), 1, BigDecimal.ROUND_HALF_UP)).setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "%";
                }
                zjItemOtdReport.setOtd(otd);
                zjItemOtdReport.setTotalOtd(totalOtd);
                zjItemOtdReport.setTotalOtdSon(totalOtdSon);
                zjItemOtdReport.setTotalOtdMother(totalOtdMother);
                zjItemOtdReports.add(zjItemOtdReport);
            }
            page.setRecords(zjItemOtdReports);
            page.setTotal(total);

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return page;
        }
    }


    @Scheduled(cron = "0 30 05 ? * *")
    //@PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void handleOtdNew() throws Exception {
        System.out.println("*********************处理OTD数据 开始 ************************************");
        // 获取所有交期及时率信息
        List<AllOtdReport> allOtdReports = getAllOtdBasicInfo();

        List<AllOtdReport> items = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (AllOtdReport item : allOtdReports) {
            // String pointedDate = item.getCheckUpdateDate();
            String pointedDate = item.getReqDate(); // 可变交期 (可以是修改交期 也可以 是 )
            String pointedDateOfSeven = ""; // 可变交期 (可以是修改交期 也可以 是 )

            String apsEndDate = item.getApsEndDate().substring(0, 10);
            String apsEndFlag = "";
            if (item.getApsEndDate().substring(0, 10).compareTo(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) == 0) {
                apsEndFlag = "0";
            } else {
                apsEndFlag = "1";
            }

            String nowDate = format.format(new Date());

            if (pointedDate != null && !pointedDate.isEmpty()) {
                Date date = new Date(format.parse(pointedDate).getTime()); // 可变交期
                Date dateOFSeven = new Date(format.parse(pointedDate).getTime() + 24 * 3600 * 1000 * 7); // 可变交期 时间加 7 天
                pointedDate = format.format(date);
                pointedDateOfSeven = format.format(dateOFSeven);
            }

            // 没有填写 可变交期 的数据一律为不合格
            if (pointedDate == null || pointedDate.isEmpty()) {
                item.setIsOtd("N");
                item.setIsOtdSeven("N");
            } else {
                // 如果APS已经结束了
                if (apsEndFlag.equals("1")) {
                    // 可变交期 >= APS齐套结束的时间    ==>  及时
                    if (pointedDate.compareTo(apsEndDate) >= 0) {
                        item.setIsOtd("Y");
                    } else {
                        item.setIsOtd("N");
                    }

                    // 可变交期+7 >= APS齐套结束的时间    ==>  及时
                    if (pointedDateOfSeven.compareTo(apsEndDate) >= 0) {
                        item.setIsOtdSeven("Y");
                    } else {
                        item.setIsOtdSeven("N");
                    }

                } else { // 若APS还未结束
                    // 可变交期 <= APS齐套最后一次出现的时间    ==>  不及时
                    if (pointedDate.compareTo(apsEndDate) < 0) {
                        item.setIsOtd("N");
                    } else {
                        // 可变交期 >= APS齐套最后一次出现的时间    ==>  待定
                        item.setIsOtd("WAIT");
                    }

                    // 可变交期+7 <= APS齐套最后一次出现的时间    ==>  不及时
                    if (pointedDateOfSeven.compareTo(apsEndDate) < 0) {
                        item.setIsOtdSeven("N");
                    } else {
                        // 可变交期 >= APS齐套最后一次出现的时间（今天）    ==>  待定
                        item.setIsOtdSeven("WAIT");
                    }
                }
            }

            // 关于铸件的特殊处理： 计划交期-需求日期>30天的，取计划交期-17天与aps最后一次做比较
            if (item.getPerson() != null && item.getPerson().equals("史瑞鹏") && (item.getIsOtd().equals("N") || item.getIsOtdSeven().equals("N"))) {
                Date planDate = format.parse(item.getPlanDate().substring(0, 10));
                Date reqDate = format.parse(item.getReqDate().substring(0, 10));
                Date pointedZJDate = CommonUtil.getDateBefore(planDate, 17); // 计划交期-17天


                Date pointedZJDateSeven = CommonUtil.getDateAfter(pointedZJDate, 7); // 计划交期-17天  + 7
                if (CommonUtil.daysBetween(reqDate, planDate) > 30) {
                    // 如果APS已经结束了
                    if (apsEndFlag.equals("1")) {
                        // 计划交期-17天【 铸件的特殊 需求时间 】 >= aps最后一次  ===> 及时
                        if (format.format(pointedZJDate).compareTo(apsEndDate) >= 0) {
                            item.setIsOtd("Y");
                        }

                        // 计划交期-17天【 铸件的特殊 需求时间 】 + 7  >= aps最后一次  ===> 及时
                        if (format.format(pointedZJDateSeven).compareTo(apsEndDate) >= 0) {
                            item.setIsOtdSeven("Y");
                        }
                    }
                }
            }

            items.add(item);
        }
        handleSRMOtd(items);
        System.out.println("*********************处理OTD数据 结束 ,共处理了 " + items.size() + " 条************************************");
    }

    // 2022-04-14 年之前的逻辑
    public void handleOtdOld() {
        System.out.println("*********************处理OTD数据 开始 ************************************");
        int handleNum = 0;
        // 获取所有交期及时率信息
        List<AllOtdReport> allOtdReports = getAllOtdBasicInfo();

        HashMap<String, List<AllOtdReport>> itemMap = new HashMap<>();
        // 111.按照料号来组成MAp （itemCode，关于该itemcode的所有行）
        allOtdReports.stream().forEach(otdReport -> {
            String key = otdReport.getItemCode();
            if (itemMap.containsKey(key)) {
                itemMap.get(key).add(otdReport);
            } else {
                itemMap.put(key, new ArrayList<>());
                itemMap.get(key).add(otdReport);
            }
        });
        // 根据该itemcode遍历hashmap
        Iterator it = itemMap.entrySet().iterator();
        String itemCode = "";
        while (it.hasNext()) {
            // 需求物料编号
            Map.Entry entry = (Map.Entry) it.next();
            itemCode = (String) entry.getKey();
            List<AllOtdReport> itemOtdList = (List<AllOtdReport>) entry.getValue();

            // 222.再根据单个itemcode所有的项目，再进行按照 需求时间的 hashMAP (同一料号的需求时间，list)
            HashMap<String, List<AllOtdReport>> singleItemCodeHashMap = new HashMap<>();
            itemOtdList.stream().forEach(singleOtdReport -> {
                String singKey = singleOtdReport.getCheckUpdateTime();
                if (singleItemCodeHashMap.containsKey(singKey)) {
                    singleItemCodeHashMap.get(singKey).add(singleOtdReport);
                } else {
                    singleItemCodeHashMap.put(singKey, new ArrayList<>());
                    singleItemCodeHashMap.get(singKey).add(singleOtdReport);
                }
            });

            Iterator singleIt = singleItemCodeHashMap.entrySet().iterator();
            String checkUpdateDateKey = "";
            while (singleIt.hasNext()) { // ******同一个料号、同一个需求时间******
                // 需求时间
                Map.Entry singleEntry = (Map.Entry) singleIt.next();
                checkUpdateDateKey = (String) singleEntry.getKey();
                // 同一个料号、同一个需求时间 的 行数
                List<AllOtdReport> checkUpdateDateOtdList = (List<AllOtdReport>) singleEntry.getValue();
                // 获取到报检未入库的个数 和 需求总个数
                Integer rcvNum = 0;
                Integer needNum = 0;
                try {
                    rcvNum = getSNInfoByItemCodeAndReqTime(itemCode, checkUpdateDateKey);
                } catch (Exception e) {
                    rcvNum = 0;
                }
                for (AllOtdReport allOtdReport : checkUpdateDateOtdList) {
                    needNum = needNum + Integer.valueOf(allOtdReport.getReqNum());
                }

                if (checkUpdateDateKey == null || checkUpdateDateKey.isEmpty()) { // 如果没填写修改审核交期，则直接不合格
                    for (AllOtdReport allOtdReport : checkUpdateDateOtdList) {
                        allOtdReport.setIsOtd("N");
                    }
                    handleSRMOtd(checkUpdateDateOtdList);
                    handleNum = handleNum + checkUpdateDateOtdList.size();
                } else {
                    // 如果收货总个数 >= 需求总个数
                    if (rcvNum >= needNum) {
                        for (AllOtdReport allOtdReport : checkUpdateDateOtdList) {
                            allOtdReport.setIsOtd("Y");
                        }
                        handleSRMOtd(checkUpdateDateOtdList);
                        handleNum = handleNum + checkUpdateDateOtdList.size();
                    } else {
                        // 如果收货总个数是0，直接都是不合格，开始下一轮循环
                        if (rcvNum == 0) {
                            for (AllOtdReport allOtdReport : checkUpdateDateOtdList) {
                                allOtdReport.setIsOtd("N");
                            }
                            handleSRMOtd(checkUpdateDateOtdList);
                            handleNum = handleNum + checkUpdateDateOtdList.size();
                        } else {
                            // 按照需求数量，倒序（从大到小）
                            checkUpdateDateOtdList.sort(new Comparator<AllOtdReport>() {
                                @Override
                                public int compare(AllOtdReport o1, AllOtdReport o2) {
                                    return Integer.valueOf(o2.getReqNum()) - Integer.valueOf(o1.getReqNum());
                                }
                            });

                            for (AllOtdReport allOtdReport : checkUpdateDateOtdList) {
                                // 单个的需求量
                                Integer sReqNum = Integer.valueOf(allOtdReport.getReqNum());

                                if (rcvNum <= sReqNum) { // 剩余总收货量还不足以 该项目的 需求量，则判断这个项目不及时
                                    allOtdReport.setIsOtd("N");
                                } else { // 剩余总收货量 足够 该项目的 需求量，则判断这个项目及时，并减掉 需求数量
                                    rcvNum = rcvNum - sReqNum;
                                    allOtdReport.setIsOtd("Y");
                                }
                            }
                            handleSRMOtd(checkUpdateDateOtdList);
                            handleNum = handleNum + checkUpdateDateOtdList.size();
                        }
                    }
                }
            }
        }
        System.out.println("*********************处理OTD数据 结束 ,共处理了 " + handleNum + " 条************************************");
    }

    /**
     * 获取所有交期及时率信息
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AllOtdReport> getAllOtdBasicInfo() {
        List<AllOtdReport> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        // 获取当月的第一天
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH, 1);
        firstCalendar.add(Calendar.MONTH, 0);
        String firstDay = new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        // 获取下个月的最后一天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        String nextFirstDay = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        int year = Integer.parseInt(nextFirstDay.split("-")[0]);  //年
        int month = Integer.parseInt(nextFirstDay.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        // cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.MONTH, month); //设置当前月的上一个月
        // 获取某月最大天数
        //int lastDay = cal.getActualMaximum(Calendar.DATE);
        int lastDay = cal.getMinimum(Calendar.DATE); //获取月份中的最小值，即第一天
        // 设置日历中月份的最大天数
        //cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //上月的第一天减去1就是当月的最后一天
        // 格式化日期
        String endDay = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // 计算需求日期 是 当前月份的 数据
            sqlListBuilder.append("select r.subprjno, r.plandate, r.check_update_date, r.updtime, r.status, r.pr_code, r.biz_type, r.itemcode, r.subneednum, r.usageqty, r.needtime, r.itemname, r.sup_code, r.sup_name, s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where  r.sup_code is not null and r.needTime is not null and  r.plandate>=to_date('")
                .append(firstDay)
                .append("','yyyy-MM-dd') and r.plandate<=to_date('")
                .append(endDay)
                .append("','yyyy-MM-dd') group by r.subprjno, r.plandate, r.check_update_date, r.updtime, r.status, r.pr_code, r.biz_type, r.itemcode, r.subneednum, r.usageqty, r.needtime, r.itemname, r.sup_code, r.sup_name, s.place_name");

            //3.执行
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                AllOtdReport allOtdReport = new AllOtdReport();
                allOtdReport.setSubProCode(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setPrCode(resultSetList.getString("pr_code"));
                allOtdReport.setBizType(resultSetList.getString("biz_type"));
                allOtdReport.setCheckUpdateTime(resultSetList.getString("check_update_date"));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));

                String usageQty = resultSetList.getString("usageQty");
                if (usageQty == null) {
                    usageQty = "0";
                }
                String subReqNum = resultSetList.getString("subneednum");
                allOtdReport.setReqNum(new BigDecimal(usageQty).multiply(new BigDecimal(subReqNum)).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                allOtdReport.setReqDate(resultSetList.getString("needtime"));
                allOtdReport.setPerson(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return allOtdReports;
        }
    }

    /**
     * 获取指定交期之内的交货情况
     *
     * @param itemCode
     * @param reqTime
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer getSNInfoByItemCodeAndReqTime(String itemCode, String reqTime) throws Exception {

        List<DeliverReport> deliverReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;
        Integer rcvNum = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(format.parse(reqTime).getTime() + 24 * 3600 * 1000 * 3); // 需求时间加 3 天
        Date originlDate = new Date(format.parse(reqTime).getTime() - 24 * 3600 * 1000);// 需求时间减 1 天（用来减少误差，入库在前面一天的也算）
        reqTime = format.format(date);

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();

            //
            sqlListBuilder.append("select * from atwrpt.vu_sndoc_rpt where itemcode='")
                .append(itemCode).append("'")
                .append(" and mtime < =to_date('")
                .append(reqTime).append("'").append(",'yyyy-mm-dd')");
            //3.执行
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                DeliverReport deliverReport = new DeliverReport();
                deliverReport.setItemCode(resultSetList.getString("itemCode"));
                deliverReport.setItemName(resultSetList.getString("itemName"));
                deliverReport.setSnTime(resultSetList.getString("mtime"));
                deliverReport.setSnNum(resultSetList.getString("rcvQty"));
                deliverReport.setStoreStatus(resultSetList.getString("u9Flag"));
                deliverReport.setStoreQty(resultSetList.getString("storeQty"));
                deliverReport.setStoreTime(resultSetList.getString("storetime"));
                deliverReports.add(deliverReport);
            }

            if (deliverReports.size() == 0) {
                return rcvNum;
            } else {
                Date reqTimeDate = originlDate;

                for (DeliverReport deliverReport : deliverReports) {
                    if (deliverReport.getStoreStatus() != null && deliverReport.getStoreStatus().equals("Y")) {// 已入库的
                        Date storeTimeDate = format.parse(deliverReport.getStoreTime());

                        // 入库时间 在 需求时间(原始的) 之后的，算个数
                        if (storeTimeDate != null && storeTimeDate.compareTo(reqTimeDate) > 0) {
                            rcvNum = rcvNum + Integer.valueOf(deliverReport.getSnNum());
                        }
                    } else {// 未入库的
                        rcvNum = rcvNum + Integer.valueOf(deliverReport.getSnNum());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return rcvNum;
        }
    }

    /**
     * 插入SRM本地的OTD表
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleSRMOtd(List<AllOtdReport> reqTimeOtdList) {
        reqTimeOtdList.stream().forEach(otdReport -> {
            Integer count = reportMapper.SelectIsOtdCount(otdReport.getSubProCode(), otdReport.getItemCode());
            if (count == 0) {
                reportMapper.InsertOtd(otdReport.getSubProCode(), otdReport.getItemCode(), otdReport.getItemName(), otdReport.getSupCode(), otdReport.getSupName(), otdReport.getReqDate(), otdReport.getIsOtd(), otdReport.getBizType(), otdReport.getPrCode(), otdReport.getPlanDate(), otdReport.getPerson(), otdReport.getCheckUpdateTime(), otdReport.getApsEndDate(), otdReport.getIsOtdSeven());
            } else {
                reportMapper.updateOtd(otdReport.getSubProCode(), otdReport.getItemCode(), otdReport.getIsOtd(), otdReport.getPlanDate(), otdReport.getPerson(), otdReport.getCheckUpdateTime(), otdReport.getApsEndDate(), otdReport.getReqDate(), otdReport.getIsOtdSeven());
            }
        });
    }

    /**
     * 关键物料报表
     *
     * @param page
     * @param keyItemReportReq
     * @return
     */
    @Override
    public IPage<ItemDailyReport> getKeyItemDailyReport(IPage<ItemDailyReport> page, KeyItemReportReq keyItemReportReq) {

        // 获取所有关键物料信息
        List<KeyScheduleReport> keyItems = reportMapper.getAllKeyItemOfAfterToday(keyItemReportReq);
        // 时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date pointerReqDate = null;
        try {
            if (keyItemReportReq.getPointedReqDate() != null && !keyItemReportReq.getPointedReqDate().isEmpty()) {
                pointerReqDate = format.parse(keyItemReportReq.getPointedReqDate());
            }
        } catch (Exception e) {
            throw new RuntimeException("时间比较出错");
        }

        // 时间
        Date date = new Date();//获取当前日期
        Date dateOfThree = CommonUtil.getDateAfter(date, 3);
        Date dateOfFour = CommonUtil.getDateAfter(date, 4);
        Date dateOfSeven = CommonUtil.getDateAfter(date, 7);

        Map<String, List<String>> affectProList = new HashMap<>();
        affectProList.put("FX", new ArrayList<>());
        affectProList.put("ZJ", new ArrayList<>());
        affectProList.put("DJ", new ArrayList<>());
        affectProList.put("QKJ", new ArrayList<>());
        affectProList.put("WGF", new ArrayList<>());
        affectProList.put("QGZXQ", new ArrayList<>());

        // 各物料分类
        ItemDailyReport FX = getKeyTypeItem("阀芯");
        ItemDailyReport ZJ = getKeyTypeItem("铸件");
        ItemDailyReport DJ = getKeyTypeItem("锻件");
        ItemDailyReport QKJ = getKeyTypeItem("气控件");
        ItemDailyReport WGF = getKeyTypeItem("外购阀");
        ItemDailyReport QGZXQ = getKeyTypeItem("气缸/执行器");
        List<String> proNoListFX = new ArrayList<>();
        List<String> proNoListZJ = new ArrayList<>();
        List<String> proNoListDJ = new ArrayList<>();
        List<String> proNoListQKJ = new ArrayList<>();
        List<String> proNoListWGF = new ArrayList<>();
        List<String> proNoListQGZXQ = new ArrayList<>();
        for (KeyScheduleReport keyItem : keyItems) {
            String itemCode = keyItem.getItemCode();
            String proNo = keyItem.getProNo();
            boolean isAdd = false;
            if (itemCode.substring(0, 4).equals("1251") || itemCode.substring(0, 4).equals("1252") || itemCode.substring(0, 4).equals("1303") || itemCode.substring(0, 4).equals("1226") || itemCode.substring(0, 4).equals("1249")) {
                if (!proNoListFX.contains(proNo)) {
                    proNoListFX.add(proNo);
                    isAdd = true;
                }
                int before = FX.getTcsOfAS() + FX.getTcsOfBS() + FX.getTcsOfCS();
                FX = handleKeyItem(keyItem, FX, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = FX.getTcsOfAS() + FX.getTcsOfBS() + FX.getTcsOfCS();
                if (after > before) {
                    affectProList.get("FX").add(keyItem.getProNo());
                }
            } else if (itemCode.substring(0, 4).equals("1501") || itemCode.substring(0, 4).equals("1511")) {
                if (!proNoListZJ.contains(proNo)) {
                    proNoListZJ.add(proNo);
                    isAdd = true;
                }
                int before = ZJ.getTcsOfAS() + ZJ.getTcsOfBS() + ZJ.getTcsOfCS();
                ZJ = handleKeyItem(keyItem, ZJ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = ZJ.getTcsOfAS() + ZJ.getTcsOfBS() + ZJ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("ZJ").add(keyItem.getProNo());
                }
            } else if (itemCode.substring(0, 4).equals("1502") || itemCode.substring(0, 4).equals("1505")) {
                if (!proNoListDJ.contains(proNo)) {
                    proNoListDJ.add(proNo);
                    isAdd = true;
                }
                int before = DJ.getTcsOfAS() + DJ.getTcsOfBS() + DJ.getTcsOfCS();
                DJ = handleKeyItem(keyItem, DJ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = DJ.getTcsOfAS() + DJ.getTcsOfBS() + DJ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("DJ").add(keyItem.getProNo());
                }
            } else if (itemCode.substring(0, 4).equals("1401")) {
                if (!proNoListQKJ.contains(proNo)) {
                    proNoListQKJ.add(proNo);
                    isAdd = true;
                }
                int before = QKJ.getTcsOfAS() + QKJ.getTcsOfBS() + QKJ.getTcsOfCS();
                QKJ = handleKeyItem(keyItem, QKJ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = QKJ.getTcsOfAS() + QKJ.getTcsOfBS() + QKJ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("QKJ").add(keyItem.getProNo());
                }
            } else if (itemCode.substring(0, 2).equals("10") || itemCode.substring(0, 2).equals("11")) {
                if (!proNoListWGF.contains(proNo)) {
                    proNoListWGF.add(proNo);
                    isAdd = true;
                }
                int before = WGF.getTcsOfAS() + WGF.getTcsOfBS() + WGF.getTcsOfCS();
                WGF = handleKeyItem(keyItem, WGF, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = WGF.getTcsOfAS() + WGF.getTcsOfBS() + WGF.getTcsOfCS();
                if (after > before) {
                    affectProList.get("WGF").add(keyItem.getProNo());
                }
            } else if (itemCode.substring(0, 4).equals("1403")) {
                if (!proNoListQGZXQ.contains(proNo)) {
                    proNoListQGZXQ.add(proNo);
                    isAdd = true;
                }
                int before = QGZXQ.getTcsOfAS() + QGZXQ.getTcsOfBS() + QGZXQ.getTcsOfCS();
                QGZXQ = handleKeyItem(keyItem, QGZXQ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = QGZXQ.getTcsOfAS() + QGZXQ.getTcsOfBS() + QGZXQ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("QGZXQ").add(keyItem.getProNo());
                }
            }
        }

        List<ItemDailyReport> keyItemReportList = new ArrayList<>();
        Integer fxAffectPlanNum = affectProList.get("FX") == null ? 0 : affectProList.get("FX").stream().distinct().collect(Collectors.toList()).size();
        FX.setAffectProNum(fxAffectPlanNum);
        Integer zjAffectPlanNum = affectProList.get("ZJ") == null ? 0 : affectProList.get("ZJ").stream().distinct().collect(Collectors.toList()).size();
        ZJ.setAffectProNum(zjAffectPlanNum);
        Integer djAffectPlanNum = affectProList.get("DJ") == null ? 0 : affectProList.get("DJ").stream().distinct().collect(Collectors.toList()).size();
        DJ.setAffectProNum(djAffectPlanNum);
        Integer qkjAffectPlanNum = affectProList.get("QKJ") == null ? 0 : affectProList.get("QKJ").stream().distinct().collect(Collectors.toList()).size();
        QKJ.setAffectProNum(qkjAffectPlanNum);
        Integer wgfAffectPlanNum = affectProList.get("WGF") == null ? 0 : affectProList.get("WGF").stream().distinct().collect(Collectors.toList()).size();
        WGF.setAffectProNum(wgfAffectPlanNum);
        Integer qgzxqAffectPlanNum = affectProList.get("QGZXQ") == null ? 0 : affectProList.get("QGZXQ").stream().distinct().collect(Collectors.toList()).size();
        QGZXQ.setAffectProNum(qgzxqAffectPlanNum);

        keyItemReportList.add(FX);
        keyItemReportList.add(ZJ);
        keyItemReportList.add(DJ);
        keyItemReportList.add(QKJ);
        keyItemReportList.add(WGF);
        keyItemReportList.add(QGZXQ);
        page.setRecords(keyItemReportList);

        return page;
    }


    /**
     * 委外物料报表
     *
     * @param page
     * @param keyItemReportReq
     * @return
     */
    @Override
    public IPage<ItemDailyReport> getWWItemDailyReport(IPage<ItemDailyReport> page, KeyItemReportReq keyItemReportReq) {
        // 获取所有委外物料信息
        List<KeyScheduleReport> keyItems = reportMapper.getAllWWItemOfAfterToday(keyItemReportReq);
        // 时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date pointerReqDate = null;
        try {
            if (keyItemReportReq.getPointedReqDate() != null && !keyItemReportReq.getPointedReqDate().isEmpty()) {
                pointerReqDate = format.parse(keyItemReportReq.getPointedReqDate());
            }
        } catch (Exception e) {
            throw new RuntimeException("时间比较出错");
        }

        // 时间
        Date date = new Date();//获取当前日期
        Date dateOfThree = CommonUtil.getDateAfter(date, 3);
        Date dateOfFour = CommonUtil.getDateAfter(date, 4);
        Date dateOfSeven = CommonUtil.getDateAfter(date, 7);

        Map<String, List<String>> affectProList = new HashMap<>();
        affectProList.put("WWQPQ", new ArrayList<>());
        affectProList.put("XQG", new ArrayList<>());
        affectProList.put("SL", new ArrayList<>());
        affectProList.put("PG", new ArrayList<>());
        affectProList.put("PTPH", new ArrayList<>());
        affectProList.put("QGZXQ", new ArrayList<>());

        affectProList.put("WXQPQ", new ArrayList<>());
        affectProList.put("G50G51", new ArrayList<>());
        affectProList.put("ZJDJ", new ArrayList<>());

        // 各物料分类
        ItemDailyReport WWQPQ = getKeyTypeItem("QPQ未回 - 全程");
        ItemDailyReport XQG = getKeyTypeItem("线切割 - 全程");
        ItemDailyReport SL = getKeyTypeItem("深冷 - 全程");
        ItemDailyReport PG = getKeyTypeItem("抛光 - 全程");
        ItemDailyReport PTPH = getKeyTypeItem("喷涂/喷焊 - 全程");

        ItemDailyReport WXQPQ = getKeyTypeItem("QPQ未回 - 工序");
        ItemDailyReport G50G51 = getKeyTypeItem("G50/G51未回 - 工序");
        ItemDailyReport ZJDJ = getKeyTypeItem("铸件/锻件 - 工序");

        List<String> proNoListWWQPQ = new ArrayList<>();
        List<String> proNoListXQG = new ArrayList<>();
        List<String> proNoListSL = new ArrayList<>();
        List<String> proNoListPG = new ArrayList<>();
        List<String> proNoListPTPH = new ArrayList<>();

        List<String> proNoListWXQPQ = new ArrayList<>();
        List<String> proNoListG50G51 = new ArrayList<>();
        List<String> proNoListZJDJ = new ArrayList<>();

        for (KeyScheduleReport keyItem : keyItems) {
            String itemName = keyItem.getItemName();
            String bizType = keyItem.getBizType();
            String proNo = keyItem.getProNo();
            boolean isAdd = false;
            if (itemName.indexOf("QPQ") > -1 && bizType.equals("1")) {
                if (!proNoListWWQPQ.contains(proNo)) {
                    proNoListWWQPQ.add(proNo);
                    isAdd = true;
                }

                int before = WWQPQ.getTcsOfAS() + WWQPQ.getTcsOfBS() + WWQPQ.getTcsOfCS();
                WWQPQ = handleKeyItem(keyItem, WWQPQ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = WWQPQ.getTcsOfAS() + WWQPQ.getTcsOfBS() + WWQPQ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("WWQPQ").add(keyItem.getProNo());
                }

            } else if (itemName.indexOf("线切割") > -1 && bizType.equals("1")) {
                if (!proNoListXQG.contains(proNo)) {
                    proNoListXQG.add(proNo);
                    isAdd = true;
                }

                int before = XQG.getTcsOfAS() + XQG.getTcsOfBS() + XQG.getTcsOfCS();
                XQG = handleKeyItem(keyItem, XQG, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = XQG.getTcsOfAS() + XQG.getTcsOfBS() + XQG.getTcsOfCS();
                if (after > before) {
                    affectProList.get("XQG").add(keyItem.getProNo());
                }

            } else if (itemName.indexOf("LT") > -1 && bizType.equals("1")) {
                if (!proNoListSL.contains(proNo)) {
                    proNoListSL.add(proNo);
                    isAdd = true;
                }
                int before = SL.getTcsOfAS() + SL.getTcsOfBS() + SL.getTcsOfCS();
                SL = handleKeyItem(keyItem, SL, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = SL.getTcsOfAS() + SL.getTcsOfBS() + SL.getTcsOfCS();
                if (after > before) {
                    affectProList.get("SL").add(keyItem.getProNo());
                }
            } else if (itemName.indexOf("抛光") > -1 && bizType.equals("1")) {
                if (!proNoListPG.contains(proNo)) {
                    proNoListPG.add(proNo);
                    isAdd = true;
                }

                int before = PG.getTcsOfAS() + PG.getTcsOfBS() + PG.getTcsOfCS();
                PG = handleKeyItem(keyItem, PG, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = PG.getTcsOfAS() + PG.getTcsOfBS() + PG.getTcsOfCS();
                if (after > before) {
                    affectProList.get("PG").add(keyItem.getProNo());
                }

            } else if ((itemName.indexOf("PT") > -1 || itemName.indexOf("PH") > -1) && bizType.equals("1")) {
                if (!proNoListPTPH.contains(proNo)) {
                    proNoListPTPH.add(proNo);
                    isAdd = true;
                }

                int before = PTPH.getTcsOfAS() + PTPH.getTcsOfBS() + PTPH.getTcsOfCS();
                PTPH = handleKeyItem(keyItem, PTPH, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = PTPH.getTcsOfAS() + PTPH.getTcsOfBS() + PTPH.getTcsOfCS();
                if (after > before) {
                    affectProList.get("PTPH").add(keyItem.getProNo());
                }

            } else if (itemName.indexOf("QPQ") > -1 && bizType.equals("0")) {
                if (!proNoListWXQPQ.contains(proNo)) {
                    proNoListWXQPQ.add(proNo);
                    isAdd = true;
                }

                int before = WXQPQ.getTcsOfAS() + WXQPQ.getTcsOfBS() + WXQPQ.getTcsOfCS();
                WXQPQ = handleKeyItem(keyItem, WXQPQ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = WXQPQ.getTcsOfAS() + WXQPQ.getTcsOfBS() + WXQPQ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("WXQPQ").add(keyItem.getProNo());
                }

            } else if ((itemName.indexOf("G50") > -1 || itemName.indexOf("G51") > -1) && bizType.equals("0")) {
                if (!proNoListG50G51.contains(proNo)) {
                    proNoListG50G51.add(proNo);
                    isAdd = true;
                }

                int before = G50G51.getTcsOfAS() + G50G51.getTcsOfBS() + G50G51.getTcsOfCS();
                G50G51 = handleKeyItem(keyItem, WXQPQ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = G50G51.getTcsOfAS() + G50G51.getTcsOfBS() + G50G51.getTcsOfCS();
                if (after > before) {
                    affectProList.get("G50G51").add(keyItem.getProNo());
                }

            } else {
                if (!proNoListZJDJ.contains(proNo)) {
                    proNoListZJDJ.add(proNo);
                    isAdd = true;
                }
                int before = ZJDJ.getTcsOfAS() + ZJDJ.getTcsOfBS() + ZJDJ.getTcsOfCS();
                ZJDJ = handleKeyItem(keyItem, ZJDJ, date, dateOfThree, dateOfFour, dateOfSeven, isAdd, pointerReqDate);
                int after = ZJDJ.getTcsOfAS() + ZJDJ.getTcsOfBS() + ZJDJ.getTcsOfCS();
                if (after > before) {
                    affectProList.get("ZJDJ").add(keyItem.getProNo());
                }
            }
        }

        List<ItemDailyReport> keyItemReportList = new ArrayList<>();
        Integer WWQPQAffectPlanNum = affectProList.get("WWQPQ") == null ? 0 : affectProList.get("WWQPQ").stream().distinct().collect(Collectors.toList()).size();
        WWQPQ.setAffectProNum(WWQPQAffectPlanNum);
        Integer XQGAffectPlanNum = affectProList.get("XQG") == null ? 0 : affectProList.get("XQG").stream().distinct().collect(Collectors.toList()).size();
        XQG.setAffectProNum(XQGAffectPlanNum);
        Integer SLAffectPlanNum = affectProList.get("SL") == null ? 0 : affectProList.get("SL").stream().distinct().collect(Collectors.toList()).size();
        SL.setAffectProNum(SLAffectPlanNum);
        Integer PGAffectPlanNum = affectProList.get("PG") == null ? 0 : affectProList.get("PG").stream().distinct().collect(Collectors.toList()).size();
        PG.setAffectProNum(PGAffectPlanNum);
        Integer PTPHAffectPlanNum = affectProList.get("PTPH") == null ? 0 : affectProList.get("PTPH").stream().distinct().collect(Collectors.toList()).size();
        PTPH.setAffectProNum(PTPHAffectPlanNum);
        Integer WXQPQAffectPlanNum = affectProList.get("WXQPQ") == null ? 0 : affectProList.get("WXQPQ").stream().distinct().collect(Collectors.toList()).size();
        WXQPQ.setAffectProNum(WXQPQAffectPlanNum);
        Integer G50G51AffectPlanNum = affectProList.get("G50G51") == null ? 0 : affectProList.get("G50G51").stream().distinct().collect(Collectors.toList()).size();
        G50G51.setAffectProNum(G50G51AffectPlanNum);
        Integer ZJDJAffectPlanNum = affectProList.get("ZJDJ") == null ? 0 : affectProList.get("ZJDJ").stream().distinct().collect(Collectors.toList()).size();
        ZJDJ.setAffectProNum(ZJDJAffectPlanNum);

        keyItemReportList.add(WWQPQ);
        keyItemReportList.add(XQG);
        keyItemReportList.add(SL);
        keyItemReportList.add(PG);
        keyItemReportList.add(PTPH);
        keyItemReportList.add(WXQPQ);
        keyItemReportList.add(G50G51);
        keyItemReportList.add(ZJDJ);
        page.setRecords(keyItemReportList);

        return page;
    }


    /**
     * 非关键物料报表
     *
     * @param page
     * @param keyItemReportReq
     * @return
     */
    @Override
    public IPage<ItemDailyReport> getNotKeyItemDailyReport(IPage<ItemDailyReport> page, KeyItemReportReq keyItemReportReq) {
        // 获取所有非关键物料信息
        List<KeyScheduleReport> keyItems = reportMapper.getAllNotKeyItemOfAfterToday(keyItemReportReq);
        // 时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date pointerReqDate = null;
        try {
            if (keyItemReportReq.getPointedReqDate() != null && !keyItemReportReq.getPointedReqDate().isEmpty()) {
                pointerReqDate = format.parse(keyItemReportReq.getPointedReqDate());
            }
        } catch (Exception e) {
            throw new RuntimeException("时间比较出错");
        }

        // 时间
        Date date = new Date();//获取当前日期
        Date dateOfOne = CommonUtil.getDateAfter(date, 1);
        Date dateOfTwo = CommonUtil.getDateAfter(date, 2);
        Map<String, List<String>> affectProList = new HashMap<>();
        affectProList.put("notKey", new ArrayList<>());

        // 各物料分类
        ItemDailyReport notKey = getKeyTypeItem("非关键物料");

        List<String> proNoListNotKey = new ArrayList<>();
        for (KeyScheduleReport keyItem : keyItems) {

            String proNo = keyItem.getProNo();
            boolean isAdd = false;

            if (!proNoListNotKey.contains(proNo)) {
                proNoListNotKey.add(proNo);
                isAdd = true;
            }
            int before = notKey.getTcsOfAS() + notKey.getTcsOfBS() + notKey.getTcsOfCS();
            notKey = handleNotKeyItem(keyItem, notKey, date, dateOfOne, dateOfTwo, isAdd, pointerReqDate);
            int after = notKey.getTcsOfAS() + notKey.getTcsOfBS() + notKey.getTcsOfCS();
            if (after > before) {
                affectProList.get("notKey").add(keyItem.getProNo());
            }
        }
        List<ItemDailyReport> keyItemReportList = new ArrayList<>();
        Integer WWQPQAffectPlanNum = affectProList.get("notKey") == null ? 0 : affectProList.get("notKey").stream().distinct().collect(Collectors.toList()).size();
        notKey.setAffectProNum(WWQPQAffectPlanNum);

        keyItemReportList.add(notKey);
        page.setRecords(keyItemReportList);
        return page;
    }

    private ItemDailyReport getKeyTypeItem(String type) {
        ItemDailyReport keyItemReport = new KeyItemReportReq();
        keyItemReport.setAffectTcsNum(0);
        keyItemReport.setAffectPlanNum(0);
        keyItemReport.setPcsOfAS(0);
        keyItemReport.setPcsOfAX(0);
        keyItemReport.setPcsOfBS(0);
        keyItemReport.setPcsOfBX(0);
        keyItemReport.setPcsOfCS(0);
        keyItemReport.setPcsOfCX(0);
        keyItemReport.setTcsOfAS(0);
        keyItemReport.setTcsOfAX(0);
        keyItemReport.setTcsOfBS(0);
        keyItemReport.setTcsOfBX(0);
        keyItemReport.setTcsOfCS(0);
        keyItemReport.setTcsOfCX(0);
        keyItemReport.setItemType(type);
        return keyItemReport;
    }

    private ItemDailyReport handleKeyItem(KeyScheduleReport keyItem, ItemDailyReport keyItemReport, Date date, Date dateOfThree, Date dateOfFour, Date dateOfSeven, boolean isAdd, Date pointedDate) {
        Date checkUpdateTime = keyItem.getCheckUpdateDate();
        Date reqDate = keyItem.getReqDate();
        int tcs = Integer.parseInt(keyItem.getProNum() == null ? "0" : keyItem.getProNum().toString());

        int affectPlanNum = keyItemReport.getAffectPlanNum();
        int affectTcsNum = keyItemReport.getAffectTcsNum();
        int pcsOfAS = keyItemReport.getPcsOfAS();
        int pcsOfAX = keyItemReport.getPcsOfAX();
        int pcsOfBS = keyItemReport.getPcsOfBS();
        int pcsOfBX = keyItemReport.getPcsOfBX();
        int pcsOfCS = keyItemReport.getPcsOfCS();
        int pcsOfCX = keyItemReport.getPcsOfCX();
        int tcsOfAS = keyItemReport.getTcsOfAS();
        int tcsOfAX = keyItemReport.getTcsOfAX();
        int tcsOfBS = keyItemReport.getTcsOfBS();
        int tcsOfBX = keyItemReport.getTcsOfBX();
        int tcsOfCS = keyItemReport.getTcsOfCS();
        int tcsOfCX = keyItemReport.getTcsOfCX();

        int day = 0;
        try {
            if (pointedDate == null) {
                day = CommonUtil.daysBetween(reqDate, checkUpdateTime);
            } else {
                day = CommonUtil.daysBetween(pointedDate, checkUpdateTime);
            }
        } catch (Exception e) {
            throw new RuntimeException("时间比较出错");
        }

        // 比较 修改审核交期 - 需求交期
        if (day < 1) {
            return keyItemReport;
        } else if (day >= 1 && day <= 3) { // 1-3天
            pcsOfAS++;
        } else if (day >= 4 && day <= 7) {
            pcsOfBS++;
        } else {
            pcsOfCS++;
        }

        if (isAdd) {
            if (day < 1) {
                return keyItemReport;
            } else if (day >= 1 && day <= 3) { // 1-3天
                tcsOfAS = tcsOfAS + tcs;
            } else if (day >= 4 && day <= 7) {
                tcsOfBS = tcsOfBS + tcs;
            } else {
                tcsOfCS = tcsOfCS + tcs;
            }
            keyItemReport.setAffectTcsNum(keyItemReport.getAffectTcsNum() + Integer.valueOf(keyItem.getProNum() == null ? "0" : keyItem.getProNum().toString()));
        }

        keyItemReport.setAffectPlanNum(keyItemReport.getAffectPlanNum() + 1);
        keyItemReport.setPcsOfAS(pcsOfAS);
        keyItemReport.setPcsOfBS(pcsOfBS);
        keyItemReport.setPcsOfCS(pcsOfCS);
        keyItemReport.setTcsOfAS(tcsOfAS);
        keyItemReport.setTcsOfBS(tcsOfBS);
        keyItemReport.setTcsOfCS(tcsOfCS);

        return keyItemReport;
    }

    private ItemDailyReport handleNotKeyItem(KeyScheduleReport keyItem, ItemDailyReport keyItemReport, Date date, Date dateOfOne, Date dateOfTwo, boolean isAdd, Date pointedDate) {
        Date checkUpdateTime = keyItem.getCheckUpdateDate();
        Date reqDate = keyItem.getReqDate();
        int tcs = Integer.parseInt(keyItem.getProNum() == null ? "0" : keyItem.getProNum().toString());

        int affectPlanNum = keyItemReport.getAffectPlanNum();
        int affectTcsNum = keyItemReport.getAffectTcsNum();
        int pcsOfAS = keyItemReport.getPcsOfAS();
        int pcsOfBS = keyItemReport.getPcsOfBS();
        int pcsOfCS = keyItemReport.getPcsOfCS();
        int tcsOfAS = keyItemReport.getTcsOfAS();
        int tcsOfBS = keyItemReport.getTcsOfBS();
        int tcsOfCS = keyItemReport.getTcsOfCS();

        int day = 0;
        try {
            if (pointedDate == null) {
                day = CommonUtil.daysBetween(reqDate, checkUpdateTime);
            } else {
                day = CommonUtil.daysBetween(pointedDate, checkUpdateTime);
            }
        } catch (Exception e) {
            throw new RuntimeException("时间比较出错");
        }

        // 比较 修改审核交期 - 需求交期
        if (day < 1) {
            return keyItemReport;
        } else if (day == 1) { // 1天
            pcsOfAS++;
        } else if (day == 2) {
            pcsOfBS++;
        } else {
            pcsOfCS++;
        }

        if (isAdd) {
            if (day < 1) {
                return keyItemReport;
            } else if (day == 1) { // 1天
                tcsOfAS = tcsOfAS + tcs;
            } else if (day == 2) {
                tcsOfBS = tcsOfBS + tcs;
            } else {
                tcsOfCS = tcsOfCS + tcs;
            }
            keyItemReport.setAffectTcsNum(keyItemReport.getAffectTcsNum() + Integer.valueOf(keyItem.getProNum() == null ? "0" : keyItem.getProNum().toString()));
        }
//        // 比较 修改审核交期
//        if (checkUpdateTime.compareTo(dateOfOne) == 0) { // 1天
//            pcsOfAS++;
//            tcsOfAS = tcsOfAS + tcs;
//        } else if (checkUpdateTime.compareTo(dateOfTwo)  == 0){ //2天
//            pcsOfBS++;
//            tcsOfBS = tcsOfBS + tcs;
//        } else {
//            pcsOfCS++;
//            tcsOfCS = tcsOfCS + tcs;
//        }
//
//        // 比较 需求交期
//        if (reqDate.compareTo(dateOfOne) == 0) { // 1天
//            pcsOfAX++;
//            tcsOfAX = tcsOfAX + tcs;
//        } else if (reqDate.compareTo(dateOfTwo)  == 0){ //2天
//            pcsOfBX++;
//            tcsOfBX = tcsOfBX + tcs;
//        } else {
//            pcsOfCX++;
//            tcsOfCX = tcsOfCX + tcs;
//        }
        keyItemReport.setAffectPlanNum(keyItemReport.getAffectPlanNum() + 1);
        keyItemReport.setPcsOfAS(pcsOfAS);
        keyItemReport.setPcsOfBS(pcsOfBS);
        keyItemReport.setPcsOfCS(pcsOfCS);
        keyItemReport.setTcsOfAS(tcsOfAS);
        keyItemReport.setTcsOfBS(tcsOfBS);
        keyItemReport.setTcsOfCS(tcsOfCS);

        return keyItemReport;
    }


    @Override
    public IPage<CaiGouSchedule> getItemDailyDetailReport(IPage<ItemDailyReport> page, CaiGouScheduleReq caiGouScheduleReq) {
        IPage<CaiGouSchedule> supplierSchedules = reportMapper.getItemDailyDetailReport(page, caiGouScheduleReq);

        for (CaiGouSchedule schedule : supplierSchedules.getRecords()) {
            Integer doStatus = schedule.getDoStatus();
            if (doStatus == null) {
                //schedule.setArrivalStatus("未送货");
                schedule.setCheckStatus("19");
                schedule.setStoreStatus("未入库");
            } else {
                String statusStroe = ""; // po的入库状态
                String rcvCode = schedule.getRcvCode(); // 最近一次DO单号

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("未入库");
                } else {
                    // 订单数量
                    if (schedule.getOrderNum() != null && schedule.getStoreNum() != null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "未入库";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "部分入库";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "已入库";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setCheckStatus(doStatus.toString());
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        return supplierSchedules;
    }

    @Override
    public void exportItemDailyDetailReport(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response) {

        List<CaiGouSchedule> caiGouSchedules = reportMapper.getItemDailyDetailList(caiGouScheduleReq);

        for (CaiGouSchedule schedule : caiGouSchedules) {
            Integer doStatus = schedule.getDoStatus();
            if (doStatus == null) {
                schedule.setArrivalStatus("未送货");
                schedule.setCheckStatus("未送货");
                schedule.setStoreStatus("未入库");
            } else {
                String statusValue = "";
                String statusStroe = "";
                switch (doStatus) {
                    case 20:
                        statusValue = "送货中";
                        break;
                    case 21:
                        statusValue = "厂内已报检";
                        break;
                    case 22:
                        statusValue = "厂外已报检";
                        break;
                    case 23:
                        statusValue = "厂内已检验";
                        break;
                    case 24:
                        statusValue = "厂外已检验";
                        break;
                    case 25:
                        statusValue = "处理中";
                        break;
                    case 26:
                        statusValue = "已点收";
                        break;
                    case 27:
                        statusValue = "虚拟已入库";
                        break;
                    case 30:
                        statusValue = "已关闭";
                        break;
                    case 40:
                        statusValue = "已作废";
                        break;
                }
                schedule.setCheckStatus(statusValue);
                schedule.setArrivalStatus(statusValue);

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("未入库");
                } else {
                    // 订单数量
                    if (schedule.getOrderNum() != null && schedule.getStoreNum() != null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "未入库";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "部分入库";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "已入库";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        List<CaiGouScheduleExcel> caiGouScheduleExcels = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (CaiGouSchedule caiGouSchedule : caiGouSchedules) {
            CaiGouScheduleExcel caiGouScheduleExcel = BeanUtil.copy(caiGouSchedule, CaiGouScheduleExcel.class);
            caiGouScheduleExcel.setAgreeDate(caiGouSchedule.getAgreeDate() == null ? "" : sdf.format(caiGouSchedule.getAgreeDate()));
            caiGouScheduleExcel.setPlanDate(caiGouSchedule.getPlanDate() == null ? "" : sdf.format(caiGouSchedule.getPlanDate()));
            caiGouScheduleExcel.setReqDate(caiGouSchedule.getReqDate() == null ? "" : sdf.format(caiGouSchedule.getReqDate()));
            caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getWwpoDate() == null ? "" : sdf.format(caiGouSchedule.getWwpoDate()));
            caiGouScheduleExcel.setNowReqDate(caiGouSchedule.getNowReqDate() == null ? "" : sdf.format(caiGouSchedule.getNowReqDate()));
            caiGouScheduleExcel.setCheckUpdateDate(caiGouSchedule.getCheckUpdateDate() == null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDate()));
            caiGouScheduleExcel.setCheckStatus(caiGouSchedule.getCheckStatus());
            caiGouScheduleExcel.setStoreStatus(caiGouSchedule.getStoreStatus());
            caiGouScheduleExcels.add(caiGouScheduleExcel);
        }

        ExcelUtils.defaultExport(caiGouScheduleExcels, CaiGouScheduleExcel.class, "采购送货计划表" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public void exportKeyItem(KeyItemReportReq keyItemReportReq, HttpServletResponse response) {
        List<KeyItemFixedExcel> keyItemFixedExcel = reportMapper.getKeyItemFixedExcel(keyItemReportReq);
        ExcelUtils.defaultExport(keyItemFixedExcel, KeyItemFixedExcel.class, "关键物料定拍点" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public IPage<QZReport> getQZReport(IPage<QZReport> page, QZReq qzReq) {
        List<QZReport> qzReportList = new ArrayList<>();

        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();

            sqlListBuilder.append("select * from (select ballcode,ballname,valveseatcode,valveseatname,row_number() over(order by ballcode desc) AS rowno from atwplm.Cust_Tee_Relationship where 1 = 1 ");
            sqlCountBuilder.append("select count(*) total from atwplm.Cust_Tee_Relationship where 1 = 1 ");

            // 球单号
            if (qzReq.getBallCode() != null && !qzReq.getBallCode().equals("")) {
                sqlListBuilder.append(" and ballcode like '%").append(qzReq.getBallCode()).append("%'");
                sqlCountBuilder.append(" and ballcode like '%").append(qzReq.getBallCode()).append("%'");
            }
            // 球描述
            if (qzReq.getBallName() != null && !qzReq.getBallName().equals("")) {
                sqlListBuilder.append(" and ballname like '%").append(qzReq.getBallName()).append("%'");
                sqlCountBuilder.append(" and ballname like '%").append(qzReq.getBallName()).append("%'");
            }
            // 座单号
            if (qzReq.getSeatCode() != null && !qzReq.getSeatCode().equals("")) {
                sqlListBuilder.append(" and valveseatcode like '%").append(qzReq.getSeatCode()).append("%'");
                sqlCountBuilder.append(" and valveseatcode like '%").append(qzReq.getSeatCode()).append("%'");
            }
            // 座描述
            if (qzReq.getSeatName() != null && !qzReq.getSeatName().equals("")) {
                sqlListBuilder.append(" and valveseatname like '%").append(qzReq.getSeatName()).append("%'");
                sqlCountBuilder.append(" and valveseatname like '%").append(qzReq.getSeatName()).append("%'");
            }

            // 分页
            long pageIndex = page.getCurrent();
            long pageSize = page.getSize();
            int start = new Long((pageIndex - 1) * pageSize).intValue();
            int end = new Long((pageIndex - 1) * pageSize + pageSize).intValue();
            sqlListBuilder.append(") where rowno> ").append(start).append(" and rowno<= ").append(end);

            int total = 0;

            //3.执行
            //3.执行
            @Cleanup ResultSet resultSetCount = stmt.executeQuery(sqlCountBuilder.toString());
            while (resultSetCount.next()) {
                total = resultSetCount.getInt("total");
            }

            if (total == 0) {
                return null;
            }

            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                QZReport qzReport = new QZReport();

                qzReport.setBallCode(resultSetList.getString("ballcode"));
                qzReport.setBallName(resultSetList.getString("ballname"));
                qzReport.setSeatCode(resultSetList.getString("valveseatcode"));
                qzReport.setSeatName(resultSetList.getString("valveseatname"));
                qzReportList.add(qzReport);
            }
            page.setRecords(qzReportList);
            page.setTotal(total);

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return page;
        }
    }

    @Override
    public void exportQZ(QZReq qzReq, HttpServletResponse response) {
        List<QZReport> qzReportList = new ArrayList<>();

        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();

            sqlListBuilder.append("select ballcode,ballname,valveseatcode,valveseatname from atwplm.Cust_Tee_Relationship where 1 = 1 ");

            // 球单号
            if (qzReq.getBallCode() != null && !qzReq.getBallCode().equals("")) {
                sqlListBuilder.append(" and ballcode like '%").append(qzReq.getBallCode()).append("%'");
            }
            // 球描述
            if (qzReq.getBallName() != null && !qzReq.getBallName().equals("")) {
                sqlListBuilder.append(" and ballname like '%").append(qzReq.getBallName()).append("%'");
            }
            // 座单号
            if (qzReq.getSeatCode() != null && !qzReq.getSeatCode().equals("")) {
                sqlListBuilder.append(" and valveseatcode like '%").append(qzReq.getSeatCode()).append("%'");
            }
            // 座描述
            if (qzReq.getSeatName() != null && !qzReq.getSeatName().equals("")) {
                sqlListBuilder.append(" and valveseatname like '%").append(qzReq.getSeatName()).append("%'");
            }

            //3.执行
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                QZReport qzReport = new QZReport();
                qzReport.setBallCode(resultSetList.getString("ballcode"));
                qzReport.setBallName(resultSetList.getString("ballname"));
                qzReport.setSeatCode(resultSetList.getString("valveseatcode"));
                qzReport.setSeatName(resultSetList.getString("valveseatname"));
                qzReportList.add(qzReport);
            }

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            ExcelUtils.defaultExport(qzReportList, QZReport.class, "球&&座信息表" + DateUtil.formatDate(new Date()), response);
        }

    }

    /**
     * 下单及时率
     *
     * @param page
     * @param orderOtdReport
     * @return
     */
    @Override
    public IPage<OrderOtdReport> getOrderOtdReport(IPage<OrderOtdReport> page, OrderOtdReq orderOtdReq) {
        IPage<OrderOtdReport> orderOtdReportIPage = this.getOrderOtdBasicInfoPageFromOracle(page, orderOtdReq);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, OrderOtdStatistics> map = new HashMap<>();

        orderOtdReportIPage.getRecords().forEach(item -> {

            if (("1".equals(item.getIsAutoOrder()) && ("1".equals(item.getIsNeedCheck()) || "0".equals(item.getIsNeedCheck()))) || ("0".equals(item.getIsAutoOrder()) && ("1".equals(item.getIsNeedCheck())))) {
                item.setIsAutoOrder("是");
            } else {
                item.setIsAutoOrder("否");
            }

            String prCheckDate = item.getPrCheckDate();
            String poCheckDate = item.getPoCheckDate();
            String key = item.getPurchName();
            OrderOtdStatistics orderOtdStatistics = null;

            if (map.containsKey(key)) {
                orderOtdStatistics = map.get(key);
            } else {
                orderOtdStatistics = new OrderOtdStatistics();
                orderOtdStatistics.setPoTotal(0);
                orderOtdStatistics.setSevenNum(0);
                orderOtdStatistics.setThreeNum(0);
            }
            int days = 0;

            try {
                if(prCheckDate==null ||poCheckDate==null){
                    return;
                }

                days = CommonUtil.daysBetween(prCheckDate, poCheckDate);
            } catch (Exception e) {
                throw new RuntimeException("日期格式出错，请查验。");
            }
            if (days > 3 && days <= 5) {
                item.setIsThreeTimely("否");
                item.setIsSevenTimely("是");
                orderOtdStatistics.setSevenNum(orderOtdStatistics.getPoTotal() + 1);
            } else if (days > 5) {
                item.setIsThreeTimely("否");
                item.setIsSevenTimely("否");
            } else {
                item.setIsThreeTimely("是");
                item.setIsSevenTimely("是");
                orderOtdStatistics.setThreeNum(orderOtdStatistics.getPoTotal() + 1);
                orderOtdStatistics.setSevenNum(orderOtdStatistics.getPoTotal() + 1);
            }
            orderOtdStatistics.setPoTotal(orderOtdStatistics.getPoTotal() + 1);
            orderOtdStatistics.setPerson(key);
        });

        // 获取下单及时率
        List<OrderOtdStatistics> orderOtdStatisticsList = getOrderOtdStatisticsList(orderOtdReq);

        if (orderOtdStatisticsList.size() > 0) {
            orderOtdReportIPage.getRecords().get(0).setOrderOtdStatisticsList(orderOtdStatisticsList);
        }

        return orderOtdReportIPage;
    }

    @Override
    public IPage<AutoOrderOtdReport> getAutoOrderOtdReport(IPage<AutoOrderOtdReport> page, AutoOrderOtdReq autoOrderOtdReq) {
        IPage<AutoOrderOtdReport> autoOrderOtdList = reportMapper.getAutoOrderOtdInfo(page, autoOrderOtdReq);
        for (AutoOrderOtdReport item : autoOrderOtdList.getRecords()) {

            item.setISAUTOORDER("否");

            if ("1".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()) || "0".equals(item.getISNEEDCHECK()))) {
                //白名单
                item.setAUTOORDERTYPE("白名单");

            }

            if ("0".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()))) {
                //PO不审核
                item.setAUTOORDERTYPE("框架（PO不审核）");

            }

            if ("0".equals(item.getISAUTOORDER()) && ("2".equals(item.getISNEEDCHECK()))) {
                //PO审核
                item.setAUTOORDERTYPE("框架（PO需审核）");

            }

            if (("1".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()) || "0".equals(item.getISNEEDCHECK()))) || ("0".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK())))) {
                item.setISAUTOORDER("是");
            } else {
                item.setISAUTOORDER("否");
            }


            //item.setREQDATE();
            if ("3".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("自然关闭");
            } else if ("4".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("短缺关闭");
            } else if ("5".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("超额关闭    ");
            } else if ("2".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("已核准");
            }
        }
        autoOrderOtdList.getRecords().get(0).setAutoOrderOtdStatisticsList(getAutoOrderOtdStatisticsList(autoOrderOtdReq));
        return autoOrderOtdList;
    }

    @Override
    public void exportAutoOrderOtd(AutoOrderOtdReq autoOrderOtdReq, HttpServletResponse response) {
        List<AutoOrderOtdReport> autoOrderOtdList = reportMapper.getAutoOrderOtdList(autoOrderOtdReq);
        for (AutoOrderOtdReport item : autoOrderOtdList) {

            if ("1".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()) || "0".equals(item.getISNEEDCHECK()))) {
                //白名单
                item.setAUTOORDERTYPE("白名单");

            }

            if ("0".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()))) {
                //PO不审核
                item.setAUTOORDERTYPE("框架（PO不审核）");

            }

            if ("0".equals(item.getISAUTOORDER()) && ("2".equals(item.getISNEEDCHECK()))) {
                //PO审核
                item.setAUTOORDERTYPE("框架（PO需审核）");

            }

            if (("1".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()) || "0".equals(item.getISNEEDCHECK()))) || ("0".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK())))) {
                item.setISAUTOORDER("是");
            } else {
                item.setISAUTOORDER("否");
            }


            //item.setREQDATE();
            if ("3".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("自然关闭");
            } else if ("4".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("短缺关闭");
            } else if ("5".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("超额关闭    ");
            } else if ("2".equals(item.getLINESTATUS())) {
                item.setLINESTATUS("已核准");
            }

            item.setREQDATE(WillDateUtil.unixTimeToStr(Long.valueOf(item.getREQDATE()), "yyyy-MM-dd"));

            item.setSUPCONFIRMDATE(WillDateUtil.unixTimeToStr(Long.valueOf(item.getSUPCONFIRMDATE()), "yyyy-MM-dd"));
        }

        ExcelUtils.defaultExport(autoOrderOtdList, AutoOrderOtdReport.class, "自动下单率报表" + DateUtil.formatDate(new Date()), response);

    }


    private List<AutoOrderOtdStatistics> getAutoOrderOtdStatisticsList(AutoOrderOtdReq autoOrderOtdReq) {
        List<AutoOrderOtdReport> autoOrderOtdList = reportMapper.getAutoOrderOtdList(autoOrderOtdReq);
        Map<String, AutoOrderOtdStatistics> map = new HashMap<>();
        AutoOrderOtdStatistics autoOrderOtdStatistics = null;

        int pototal = 0;
        int whitetotal = 0;
        int fanottotal = 0;
        int fa = 0;

        for (AutoOrderOtdReport item : autoOrderOtdList) {
            pototal++;
            String key = item.getPURCHNAME();

            if (!map.containsKey(key)) {
                autoOrderOtdStatistics = new AutoOrderOtdStatistics();
                autoOrderOtdStatistics.setPerson(key);
                autoOrderOtdStatistics.setPoTotal(0);
                autoOrderOtdStatistics.setWhiteList(0);
                autoOrderOtdStatistics.setFANotApproval(0);
                autoOrderOtdStatistics.setFA(0);
                map.put(key, autoOrderOtdStatistics);
            }
            autoOrderOtdStatistics = map.get(key);

            autoOrderOtdStatistics.setPoTotal(autoOrderOtdStatistics.getPoTotal() + 1);
            if ("1".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()) || "0".equals(item.getISNEEDCHECK()))) {
                //白名单
                autoOrderOtdStatistics.setWhiteList(autoOrderOtdStatistics.getWhiteList() + 1);
                whitetotal++;

            }

            if ("0".equals(item.getISAUTOORDER()) && ("1".equals(item.getISNEEDCHECK()))) {
                //PO不审核
                autoOrderOtdStatistics.setFANotApproval(autoOrderOtdStatistics.getFANotApproval() + 1);
                fanottotal++;

            }

            if ("0".equals(item.getISAUTOORDER()) && ("2".equals(item.getISNEEDCHECK()))) {
                //PO审核
                autoOrderOtdStatistics.setFA(autoOrderOtdStatistics.getFA() + 1);
                fa++;

            }

            if ("1".equals(item.getISAUTOORDER())) {
                item.setISAUTOORDER("是");
            } else {
                item.setISAUTOORDER("否");
            }

        }


        Collection<AutoOrderOtdStatistics> valueCollection = map.values();
        List<AutoOrderOtdStatistics> autoOrderOtdStatisticsList = new ArrayList<>(valueCollection);
        for (AutoOrderOtdStatistics item : autoOrderOtdStatisticsList) {
            BigDecimal son = new BigDecimal((item.getWhiteList() + item.getFANotApproval()) * 100);
            BigDecimal mater = new BigDecimal(item.getPoTotal());

            item.setAutoOrderCent(son.divide(mater, 2, BigDecimal.ROUND_HALF_UP) + "%");

        }

        //增加合计行
        AutoOrderOtdStatistics autoOrderOtdStatistics1 = new AutoOrderOtdStatistics();
        autoOrderOtdStatistics1.setPerson("合 计");
        autoOrderOtdStatistics1.setPoTotal(pototal);
        autoOrderOtdStatistics1.setWhiteList(whitetotal);
        autoOrderOtdStatistics1.setFANotApproval(fanottotal);
        autoOrderOtdStatistics1.setFA(fa);
        autoOrderOtdStatistics1.setAutoOrderCent("");

        autoOrderOtdStatisticsList.add(autoOrderOtdStatistics1);


        return autoOrderOtdStatisticsList;

    }


    private List<OrderOtdStatistics> getOrderOtdStatisticsList(OrderOtdReq orderOtdReq) {
        List<OrderOtdReport> orderOtdBasicInfoList = this.getOrderOtdBasicInfoListFromOracle(orderOtdReq);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, OrderOtdStatistics> map = new HashMap<>();

        orderOtdBasicInfoList.forEach(item -> {

            if (("1".equals(item.getIsAutoOrder()) && ("1".equals(item.getIsNeedCheck()) || "0".equals(item.getIsNeedCheck()))) || ("0".equals(item.getIsAutoOrder()) && ("1".equals(item.getIsNeedCheck())))) {
                item.setIsAutoOrder("是");
            } else {
                item.setIsAutoOrder("否");
            }

            String prCheckDate = item.getPrCheckDate();
            String poCheckDate = item.getPoCheckDate();
            String key = item.getPurchName();
            OrderOtdStatistics orderOtdStatistics = null;

            if (!map.containsKey(key)) {
                orderOtdStatistics = new OrderOtdStatistics();
                orderOtdStatistics.setPoTotal(0);
                orderOtdStatistics.setSevenNum(0);
                orderOtdStatistics.setThreeNum(0);
                orderOtdStatistics.setMultipleNum(0);
                map.put(key, orderOtdStatistics);
            }
            orderOtdStatistics = map.get(key);

            int days = 0;
            try {
                if(prCheckDate==null ||poCheckDate==null){
                    return;
                }
                days = CommonUtil.daysBetween(prCheckDate, poCheckDate);
            } catch (Exception e) {
                throw new RuntimeException("日期格式出错，请查验。");
            }
            if (days > 3 && days <= 5) {
                item.setIsThreeTimely("否");
                item.setIsSevenTimely("是");
                orderOtdStatistics.setSevenNum(orderOtdStatistics.getSevenNum() + 1);
            } else if (days > 5) {
                item.setIsThreeTimely("否");
                item.setIsSevenTimely("否");
            } else {
                item.setIsThreeTimely("是");
                item.setIsSevenTimely("是");
                orderOtdStatistics.setThreeNum(orderOtdStatistics.getThreeNum() + 1);
                orderOtdStatistics.setSevenNum(orderOtdStatistics.getSevenNum() + 1);
            }

            //自动下单判断三天，非自动下单判断5天
            if (item.getIsAutoOrder().equals("是")){
                if (days <= 3 ) {
                    orderOtdStatistics.setMultipleNum(orderOtdStatistics.getMultipleNum() + 1);
                }
            }else{
                if (days <= 5 ) {
                    orderOtdStatistics.setMultipleNum(orderOtdStatistics.getMultipleNum() + 1);
                }
            }


            orderOtdStatistics.setPoTotal(orderOtdStatistics.getPoTotal() + 1);
            orderOtdStatistics.setPerson(key);
        });

        // 将 MAP的值转化成 list
        Collection<OrderOtdStatistics> valueCollection = map.values();
        List<OrderOtdStatistics> orderOtdStatisticsList = new ArrayList<OrderOtdStatistics>(valueCollection);

        Integer poTotal = 0;
        Integer threeNum = 0;
        Integer sevenNum = 0;
        Integer multipleNum = 0;
        BigDecimal threeOtd = new BigDecimal("0");
        BigDecimal sevenOtd = new BigDecimal("0");
        BigDecimal multipleOtd = new BigDecimal("0");

        for (OrderOtdStatistics item : orderOtdStatisticsList) {
            poTotal = poTotal + item.getPoTotal();
            threeNum = threeNum + item.getThreeNum();
            sevenNum = sevenNum + item.getSevenNum();
            multipleNum = multipleNum + item.getMultipleNum();
            threeOtd = threeOtd.add(new BigDecimal(item.getThreeNum() * 100).divide(new BigDecimal(item.getPoTotal()), 2, RoundingMode.HALF_UP));
            sevenOtd = sevenOtd.add(new BigDecimal(item.getSevenNum() * 100).divide(new BigDecimal(item.getPoTotal()), 2, RoundingMode.HALF_UP));
            multipleOtd = multipleOtd.add(new BigDecimal(item.getMultipleNum() * 100).divide(new BigDecimal(item.getPoTotal()), 2, RoundingMode.HALF_UP));
            item.setThreeOtd(new BigDecimal(item.getThreeNum() * 100).divide(new BigDecimal(item.getPoTotal()), 2, RoundingMode.HALF_UP) + " %");
            item.setSevenOtd(new BigDecimal(item.getSevenNum() * 100).divide(new BigDecimal(item.getPoTotal()), 2, RoundingMode.HALF_UP) + " %");
            item.setMultipleOtd(new BigDecimal(item.getMultipleNum() * 100).divide(new BigDecimal(item.getPoTotal()), 2, RoundingMode.HALF_UP) + " %");
        }

        if (orderOtdStatisticsList.size() > 0) {
            OrderOtdStatistics heji = new OrderOtdStatistics();
            heji.setPerson("合计 ：");
            heji.setPoTotal(poTotal);
            heji.setThreeNum(threeNum);
            heji.setSevenNum(sevenNum);
            heji.setMultipleNum(multipleNum);
            heji.setThreeOtd(new BigDecimal(threeNum * 100).divide(new BigDecimal(poTotal), 2, RoundingMode.HALF_UP) + " %");
            heji.setSevenOtd(new BigDecimal(sevenNum * 100).divide(new BigDecimal(poTotal), 2, RoundingMode.HALF_UP) + " %");
            heji.setMultipleOtd(new BigDecimal(multipleNum * 100).divide(new BigDecimal(poTotal), 2, RoundingMode.HALF_UP) + " %");
            orderOtdStatisticsList.add(heji);
        }

        return orderOtdStatisticsList;
    }

    @Override
    public void exportOrderOtd(OrderOtdReq orderOtdReq, HttpServletResponse response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<OrderOtdReport> orderOtdBasicInfoList = this.getOrderOtdBasicInfoListFromOracle(orderOtdReq);

        orderOtdBasicInfoList.forEach(item -> {

            if (("1".equals(item.getIsAutoOrder()) && ("1".equals(item.getIsNeedCheck()) || "0".equals(item.getIsNeedCheck()))) || ("0".equals(item.getIsAutoOrder()) && ("1".equals(item.getIsNeedCheck())))) {
                item.setIsAutoOrder("是");
            } else {
                item.setIsAutoOrder("否");
            }

            String prCheckDate = item.getPrCheckDate();
            String poCheckDate = item.getPoCheckDate();
            int days = 0;
            try {
                if(prCheckDate==null ||poCheckDate==null){
                    return;
                }
                days = CommonUtil.daysBetween(prCheckDate, poCheckDate);
            } catch (Exception e) {
                throw new RuntimeException("日期格式出错，请查验。");
            }
            if (days > 3 && days <= 5) {
                item.setIsThreeTimely("否");
                item.setIsSevenTimely("是");
            } else if (days > 5) {
                item.setIsThreeTimely("否");
                item.setIsSevenTimely("否");
            } else {
                item.setIsThreeTimely("是");
                item.setIsSevenTimely("是");
            }
        });
        ExcelUtils.defaultExport(orderOtdBasicInfoList, OrderOtdReport.class, "下单及时率表" + DateUtil.formatDate(new Date()), response);
    }


    private IPage<OrderOtdReport> getOrderOtdBasicInfoPageFromOracle(IPage<OrderOtdReport> page, OrderOtdReq orderOtdReq) {

        String supCode = SecureUtil.getTenantId();

        List<OrderOtdReport> orderOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();

            sqlListBuilder.append("select * from (SELECT po.sup_code,po.sup_name,pr.item_code,pr.biz_type,pr.item_name,pr.tc_uom,po.tc_num,pr.pr_code||' '||pr.pr_ln pr_code_ln,po.po_code||' '||po.po_ln po_code_ln,sup_confirm_date,NVL( pr.req_date, po.req_date ) req_date,to_char(p.approvedon,'yyyy-mm-dd') pr_check_date,to_char(o.approvedon,'yyyy-mm-dd') po_check_date,item.purch_name,row_number() over(order by to_char(o.approvedon,'yyyy-mm-dd') desc) rowno ," +
                "case when lib.is_deleted is not null then 1 else 0 end is_auto_order," +
                "    pr.is_need_check");
            sqlListBuilder.append(" FROM");
            //sqlListBuilder.append(" atwsrm.atw_u9_pr pr");
            sqlListBuilder.append("  (select prr.item_code,prr.item_name,prr.tc_uom,prr.pr_code,prr.pr_ln, prr.req_date,prr.biz_type,prr.is_need_check,prr.is_deleted FROM atwsrm.atw_u9_pr prr  " +
                " Union " +
                " select prr.item_code,prr.item_name,prr.price_uom tc_uom,prr.pr_code,b.pr_ln, prr.req_date,1 biz_type,prr.is_need_check,prr.is_deleted FROM atwsrm.atw_out_pr_item prr LEFT JOIN atw_out_pr_item_process b on prr.id=b.pr_item_id ) pr");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_po_item po ON pr.pr_code = po.pr_code and pr.pr_ln = po.pr_ln");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_item item ON item.code = pr.item_code ");
            sqlListBuilder.append(" LEFT JOIN ATWERP.PM_PURCHASEORDER o ON po.po_code = o.docNO ");
            sqlListBuilder.append(" LEFT JOIN ATWERP.PR_PR P on pr.pr_code = p.docno ");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_price_lib lib ON ( " +
                " item.code = lib.item_code  " +
                " AND to_char( o.approvedon, 'yyyy-mm-dd' ) >= to_char( lib.effective_date, 'yyyy-mm-dd' )  " +
                " AND to_char( o.approvedon, 'yyyy-mm-dd' ) <= to_char( lib.expiration_date, 'yyyy-mm-dd' )  " +
                " AND lib.status = 30  " +
                " )  ");
            sqlListBuilder.append(" WHERE pr.is_deleted = 0 AND po.is_deleted = 0 AND po.po_code IS NOT NULL ");

            sqlCountBuilder.append("SELECT COUNT(*) total ");
            sqlCountBuilder.append(" FROM");
            sqlCountBuilder.append(" atwsrm.atw_u9_pr pr");
            sqlCountBuilder.append(" LEFT JOIN atwsrm.atw_po_item po ON pr.pr_code = po.pr_code and pr.pr_ln = po.pr_ln");
            sqlCountBuilder.append(" LEFT JOIN atwsrm.atw_item item ON item.code = pr.item_code ");
            sqlCountBuilder.append(" LEFT JOIN ATWERP.PM_PURCHASEORDER o ON po.po_code = o.docNO ");
            sqlCountBuilder.append(" LEFT JOIN ATWERP.PR_PR P on pr.pr_code = p.docno ");
            sqlCountBuilder.append(" left join atwsrm.atw_price_lib lib on (item.code = lib.item_code and to_char ( o.approvedon, 'yyyy-mm-dd' ) >= to_char ( lib.effective_date, 'yyyy-mm-dd' )  and to_char ( o.approvedon, 'yyyy-mm-dd' ) <= to_char ( lib.expiration_date, 'yyyy-mm-dd' )" +
                "             and lib.status = 30" +
                "            ) ");
            sqlCountBuilder.append(" WHERE pr.is_deleted = 0 AND po.is_deleted = 0 AND po.po_code IS NOT NULL ");

            // PO审核时间 开始
            if (orderOtdReq.getPoCheckDateStart() != null && !orderOtdReq.getPoCheckDateStart().equals("")) {
                sqlListBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') >= '").append(orderOtdReq.getPoCheckDateStart()).append("'");
                sqlCountBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') >= '").append(orderOtdReq.getPoCheckDateStart()).append("'");
            }

            // PO审核时间 结束
            if (orderOtdReq.getPoCheckDateEnd() != null && !orderOtdReq.getPoCheckDateEnd().equals("")) {
                sqlListBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') <= '").append(orderOtdReq.getPoCheckDateEnd()).append("'");
                sqlCountBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') <= '").append(orderOtdReq.getPoCheckDateEnd()).append("'");
            }

            // PO审核时间 默认
            if (orderOtdReq.getPoCheckDateEnd() == null && orderOtdReq.getPoCheckDateStart() == null) {
                sqlListBuilder.append("and to_char(o.approvedon,'yyyy-mm-dd') >=  (select to_char(trunc(add_months(last_day(sysdate), -1) + 1), 'yyyy-mm-dd') from dual)");
                sqlCountBuilder.append("and to_char(o.approvedon,'yyyy-mm-dd') >=  (select to_char(trunc(add_months(last_day(sysdate), -1) + 1), 'yyyy-mm-dd') from dual)");
            }

            // 负责人
            if (orderOtdReq.getPurchName() != null && !orderOtdReq.getPurchName().equals("")) {
                sqlListBuilder.append(" and item.purch_name like '%").append(orderOtdReq.getPurchName()).append("%'");
                sqlCountBuilder.append(" and item.purch_name like '%").append(orderOtdReq.getPurchName()).append("%'");
            }

            // 分页
            long pageIndex = page.getCurrent();
            long pageSize = page.getSize();
            int start = new Long((pageIndex - 1) * pageSize).intValue();
            int end = new Long((pageIndex - 1) * pageSize + pageSize).intValue();
            sqlListBuilder.append(") where rowno> ").append(start).append(" and rowno<= ").append(end);

            int total = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //3.执行
            @Cleanup ResultSet resultSetCount = stmt.executeQuery(sqlCountBuilder.toString());
            while (resultSetCount.next()) {
                total = resultSetCount.getInt("total");
            }

            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {

                OrderOtdReport orderOtdReport = new OrderOtdReport();

                orderOtdReport.setSupCode(resultSetList.getString("sup_Code"));
                orderOtdReport.setSupName(resultSetList.getString("sup_Name"));
                orderOtdReport.setItemCode(resultSetList.getString("item_Code"));
                orderOtdReport.setItemName(resultSetList.getString("item_Name"));


                orderOtdReport.setTcUom(resultSetList.getString("tc_uom"));
                orderOtdReport.setPrCodeLn(resultSetList.getString("pr_code_ln"));
                orderOtdReport.setPoCodeLn(resultSetList.getString("po_code_ln"));

                orderOtdReport.setSupConfirmDate(resultSetList.getString("sup_confirm_date"));
                if (orderOtdReport.getSupConfirmDate() != null) {
                    orderOtdReport.setSupConfirmDate(sdf.format(new Date(Integer.parseInt(orderOtdReport.getSupConfirmDate()) * 1000L)));
                }
                orderOtdReport.setReqDate(resultSetList.getString("req_date"));
                if (orderOtdReport.getReqDate() != null) {
                    orderOtdReport.setReqDate(sdf.format(new Date(Integer.parseInt(orderOtdReport.getReqDate()) * 1000L)));
                }
                orderOtdReport.setPrCheckDate(resultSetList.getString("pr_check_date"));
                orderOtdReport.setPoCheckDate(resultSetList.getString("po_check_date"));

                orderOtdReport.setPurchName(resultSetList.getString("purch_name"));

                orderOtdReport.setIsAutoOrder(resultSetList.getString("is_auto_order"));
                orderOtdReport.setIsNeedCheck(resultSetList.getString("is_need_check"));
                orderOtdReport.setBizType(resultSetList.getString("biz_type"));


                orderOtdReports.add(orderOtdReport);
            }

            page.setRecords(orderOtdReports);
            page.setTotal(total);

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return page;
        }
    }


    private List<OrderOtdReport> getOrderOtdBasicInfoListFromOracle(OrderOtdReq orderOtdReq) {

        String supCode = SecureUtil.getTenantId();

        List<OrderOtdReport> orderOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.准备预编译的sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();

            sqlListBuilder.append("SELECT po.sup_code,po.sup_name,pr.item_code,pr.biz_type,pr.item_name,pr.tc_uom,po.tc_num,pr.pr_code||' '||pr.pr_ln pr_code_ln,po.po_code||' '||po.po_ln po_code_ln,sup_confirm_date,NVL( pr.req_date, po.req_date ) req_date,to_char(p.approvedon,'yyyy-mm-dd') pr_check_date,to_char(o.approvedon,'yyyy-mm-dd') po_check_date,item.purch_name,case when lib.is_deleted is not null then 1 else 0 end is_auto_order," +
                "    pr.is_need_check");
            sqlListBuilder.append(" FROM");
            //sqlListBuilder.append(" atwsrm.atw_u9_pr pr");
            sqlListBuilder.append("  (select prr.item_code,prr.item_name,prr.tc_uom,prr.pr_code,prr.pr_ln, prr.req_date,prr.biz_type,prr.is_need_check,prr.is_deleted FROM atwsrm.atw_u9_pr prr  " +
                " Union " +
                " select prr.item_code,prr.item_name,prr.price_uom tc_uom,prr.pr_code,b.pr_ln, prr.req_date,1 biz_type,prr.is_need_check,prr.is_deleted FROM atwsrm.atw_out_pr_item prr LEFT JOIN atw_out_pr_item_process b on prr.id=b.pr_item_id ) pr");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_po_item po ON pr.pr_code = po.pr_code and pr.pr_ln = po.pr_ln");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_item item ON item.code = pr.item_code ");
            sqlListBuilder.append(" LEFT JOIN ATWERP.PM_PURCHASEORDER o ON po.po_code = o.docNO ");
            sqlListBuilder.append(" LEFT JOIN ATWERP.PR_PR P on pr.pr_code = p.docno ");
            sqlListBuilder.append(" left join atwsrm.atw_price_lib lib on (item.code = lib.item_code and to_char ( o.approvedon, 'yyyy-mm-dd' ) >= to_char ( lib.effective_date, 'yyyy-mm-dd' )  and to_char ( o.approvedon, 'yyyy-mm-dd' ) <= to_char ( lib.expiration_date, 'yyyy-mm-dd' )" +
                "             and lib.status = 30" +
                "            ) ");

            sqlListBuilder.append(" WHERE pr.is_deleted = 0 AND po.is_deleted = 0 AND po.po_code IS NOT NULL ");

            // PO审核时间 开始
            if (orderOtdReq.getPoCheckDateStart() != null && !orderOtdReq.getPoCheckDateStart().equals("")) {
                sqlListBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') >= '").append(orderOtdReq.getPoCheckDateStart()).append("'");
            }

            // PO审核时间 结束
            if (orderOtdReq.getPoCheckDateEnd() != null && !orderOtdReq.getPoCheckDateEnd().equals("")) {
                sqlListBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') <= '").append(orderOtdReq.getPoCheckDateEnd()).append("'");
            }

            // PO审核时间 默认
            if (orderOtdReq.getPoCheckDateEnd() == null && orderOtdReq.getPoCheckDateStart() == null) {
                sqlListBuilder.append(" and to_char(o.approvedon,'yyyy-mm-dd') >=  (select to_char(trunc(add_months(last_day(sysdate), -1) + 1), 'yyyy-mm-dd') from dual)");
            }
            // 负责人
            if (orderOtdReq.getPurchName() != null && !orderOtdReq.getPurchName().equals("")) {
                sqlListBuilder.append(" and item.purch_name like '%").append(orderOtdReq.getPurchName()).append("%'");
            }

            int total = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //3.执行
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {

                OrderOtdReport orderOtdReport = new OrderOtdReport();

                orderOtdReport.setSupCode(resultSetList.getString("sup_Code"));
                orderOtdReport.setSupName(resultSetList.getString("sup_Name"));
                orderOtdReport.setItemCode(resultSetList.getString("item_Code"));
                orderOtdReport.setItemName(resultSetList.getString("item_Name"));

                orderOtdReport.setTcUom(resultSetList.getString("tc_uom"));
                orderOtdReport.setPrCodeLn(resultSetList.getString("pr_code_ln"));
                orderOtdReport.setPoCodeLn(resultSetList.getString("po_code_ln"));

                orderOtdReport.setSupConfirmDate(resultSetList.getString("sup_confirm_date"));
                if (orderOtdReport.getSupConfirmDate() != null) {
                    orderOtdReport.setSupConfirmDate(sdf.format(new Date(Integer.parseInt(orderOtdReport.getSupConfirmDate()) * 1000L)));
                }
                orderOtdReport.setReqDate(resultSetList.getString("req_date"));
                if (orderOtdReport.getReqDate() != null) {
                    orderOtdReport.setReqDate(sdf.format(new Date(Integer.parseInt(orderOtdReport.getReqDate()) * 1000L)));
                }

                orderOtdReport.setPrCheckDate(resultSetList.getString("pr_check_date"));
                orderOtdReport.setPoCheckDate(resultSetList.getString("po_check_date"));

                orderOtdReport.setPurchName(resultSetList.getString("purch_name"));
                orderOtdReport.setIsAutoOrder(resultSetList.getString("is_auto_order"));
                orderOtdReport.setIsNeedCheck(resultSetList.getString("is_need_check"));
                orderOtdReport.setBizType(resultSetList.getString("biz_type"));

                orderOtdReports.add(orderOtdReport);
            }

        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return orderOtdReports;
        }
    }

    @Override
    public IPage<OrderAmountOtdReport> getOrderAmountOtdReport(IPage<OrderAmountOtdReport> page, OrderAmountOtdReportReq orderAmountOtdReportReq) {
        IPage<OrderAmountOtdReport> orderAmountOtdInfo = this.reportMapper.getOrderAmountOtdInfo(page, orderAmountOtdReportReq);
        for (OrderAmountOtdReport orderAmountOtdReport : orderAmountOtdInfo.getRecords()) {
            orderAmountOtdReportReq.setSupName(orderAmountOtdReport.getSupName());
            orderAmountOtdReportReq.setMainName(orderAmountOtdReport.getMainName());
            //去年同期
            List<OrderAmountOtdReport> orderAmountOtdLastYearList = this.reportMapper.getOrderAmountOtdLastYearList(orderAmountOtdReportReq);
            if (orderAmountOtdLastYearList.size() > 0) {
                orderAmountOtdReport.setLastyeartotalamount(orderAmountOtdLastYearList.get(0).getTotalamount());
            } else {
                orderAmountOtdReport.setLastyeartotalamount("0.000000");

            }
            //去年全年/2
            List<OrderAmountOtdReport> orderAmountOtdLastYear2List = this.reportMapper.getOrderAmountOtdLastYear2List(orderAmountOtdReportReq);
            if (orderAmountOtdLastYear2List.size() > 0) {
                String lastyeartotalamount2 = orderAmountOtdLastYear2List.get(0).getTotalamount();
                orderAmountOtdReport.setLastyeartotalamount2(new BigDecimal(lastyeartotalamount2).divide(new BigDecimal("2")).toString());
            }
            //同类型占比
            List<OrderAmountOtdReport> orderAmountOtdList = this.reportMapper.getOrderAmountOtdtotalList(orderAmountOtdReportReq);
            if (orderAmountOtdList.size() > 0) {
                orderAmountOtdReport.setTypepercent(new BigDecimal(orderAmountOtdReport.getTotalamount()).divide(new BigDecimal(orderAmountOtdList.get(0).getTotalamount()), BigDecimal.ROUND_CEILING).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP).toString() + "%");
            }


        }
        return orderAmountOtdInfo;
    }

    @Override
    public void exportOrderAmountOtdReport(OrderAmountOtdReportReq orderAmountOtdReportReq, HttpServletResponse response) {

        List<OrderAmountOtdReport> orderAmountOtdInfo = this.reportMapper.getOrderAmountOtdList(orderAmountOtdReportReq);
        for (OrderAmountOtdReport orderAmountOtdReport : orderAmountOtdInfo) {
            orderAmountOtdReportReq.setSupName(orderAmountOtdReport.getSupName());
            orderAmountOtdReportReq.setMainName(orderAmountOtdReport.getMainName());
            //去年同期
            List<OrderAmountOtdReport> orderAmountOtdLastYearList = this.reportMapper.getOrderAmountOtdLastYearList(orderAmountOtdReportReq);
            if (orderAmountOtdLastYearList.size() > 0) {
                orderAmountOtdReport.setLastyeartotalamount(orderAmountOtdLastYearList.get(0).getTotalamount());
            } else {
                orderAmountOtdReport.setLastyeartotalamount("0.000000");

            }
            //去年全年/2
            List<OrderAmountOtdReport> orderAmountOtdLastYear2List = this.reportMapper.getOrderAmountOtdLastYear2List(orderAmountOtdReportReq);
            if (orderAmountOtdLastYear2List.size() > 0) {
                String lastyeartotalamount2 = orderAmountOtdLastYear2List.get(0).getTotalamount();
                orderAmountOtdReport.setLastyeartotalamount2(new BigDecimal(lastyeartotalamount2).divide(new BigDecimal("2")).toString());
            }
            //同类型占比
            List<OrderAmountOtdReport> orderAmountOtdList = this.reportMapper.getOrderAmountOtdtotalList(orderAmountOtdReportReq);
            if (orderAmountOtdList.size() > 0) {
                orderAmountOtdReport.setTypepercent(new BigDecimal(orderAmountOtdReport.getTotalamount()).divide(new BigDecimal(orderAmountOtdList.get(0).getTotalamount()), BigDecimal.ROUND_CEILING).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP).toString() + "%");
            }
        }

        ExcelUtils.defaultExport(orderAmountOtdInfo, OrderAmountOtdReport.class, "采购额统计报表" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public IPage<MouldManagementEntity> getMouldManagementReport(IPage<MouldManagementEntity> page, MouldManagementEntity mouldManagementEntity) {
        return this.reportMapper.getMouldManagementReport(page, mouldManagementEntity);
    }

    @Override
    public void exportMouldManagementReport(MouldManagementEntity mouldManagement, HttpServletResponse response) {
        List<MouldManagementEntity> mouldManagementReportList = this.reportMapper.getMouldManagementReportList(mouldManagement);
        ExcelUtils.defaultExport(mouldManagementReportList, MouldManagementEntity.class, "模具清单" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public Boolean deleteMouldManagementReport(List<MouldManagementEntity> mouldManagementEntities) {
        for (MouldManagementEntity mouldManagementEntity : mouldManagementEntities) {
            this.reportMapper.deleteMouldManagementReport(mouldManagementEntity);
        }
        return true;
    }

    @Override
    public Boolean insertMouldManagementReport(List<MouldManagementEntity> mouldManagementEntities) {

        for (MouldManagementEntity mouldManagementEntity : mouldManagementEntities) {
            this.reportMapper.insertMouldManagementReport(mouldManagementEntity);
        }
        return true;

    }

    @Override
    public Boolean updateMouldManagementReport(List<MouldManagementEntity> mouldManagementEntities) {
        for (MouldManagementEntity mouldManagementEntity : mouldManagementEntities) {
            this.reportMapper.updateMouldManagementReport(mouldManagementEntity);
        }
        return true;
    }

    @Override
    public IPage<MouldManagementWholeEntity> getMouldManagementWholeReport(IPage<MouldManagementWholeEntity> page, MouldManagementWholeEntity MouldManagementWholeEntity) {
        IPage<MouldManagementWholeEntity> mouldManagementWholeReport = this.reportMapper.getMouldManagementWholeReport(page, MouldManagementWholeEntity);
        List<MouldManagementWholeEntity> records = mouldManagementWholeReport.getRecords();
        for (MouldManagementWholeEntity mouldManagementWhole:records) {
            if(mouldManagementWhole.getRealDate1()==null){
                mouldManagementWhole.setCurrentState("实际模具PO日期");
            } else if(mouldManagementWhole.getRealDate2()==null){
                mouldManagementWhole.setCurrentState("实际模具完成日期");
            }else if(mouldManagementWhole.getRealDate3()==null){
                mouldManagementWhole.setCurrentState("实际模具检验完成日期");
            }else if(mouldManagementWhole.getRealDate4()==null){
                mouldManagementWhole.setCurrentState("实际试样订单下达日期");
            }else if(mouldManagementWhole.getRealDate5()==null){
                mouldManagementWhole.setCurrentState("实际试样铸件完成日期");
            }else if(mouldManagementWhole.getRealDate6()==null){
                mouldManagementWhole.setCurrentState("样实际品检验完成日期");
            }else if(mouldManagementWhole.getRealDate7()==null){
                mouldManagementWhole.setCurrentState("实际加工(厂内/厂外）完成日期");
            }else if(mouldManagementWhole.getRealDate8()==null){
                mouldManagementWhole.setCurrentState("实际试装物料确认到位日期");
            }else if(mouldManagementWhole.getRealDate9()==null){
                mouldManagementWhole.setCurrentState("实际样品装配试压完成日期");
            }else if(mouldManagementWhole.getRealDate10()==null){
                mouldManagementWhole.setCurrentState("实际试样完成日期（品管签字）");
            }else{
                mouldManagementWhole.setCurrentState("完成");
            }
        }

        return mouldManagementWholeReport;
    }

    @Override
    public void exportMouldManagementWholeReport(MouldManagementWholeEntity MouldManagementWhole, HttpServletResponse response) {

        IPage<MouldManagementWholeEntity> mouldManagementWholeReport = this.reportMapper.getMouldManagementWholeReport(Condition.getPage(new Query().setSize(10000)), MouldManagementWhole);
        List<MouldManagementWholeEntity> records = mouldManagementWholeReport.getRecords();
        for (MouldManagementWholeEntity mouldManagementWhole:records) {
            if(mouldManagementWhole.getRealDate1()==null){
                mouldManagementWhole.setCurrentState("实际模具PO日期");
            } else if(mouldManagementWhole.getRealDate2()==null){
                mouldManagementWhole.setCurrentState("实际模具完成日期");
            }else if(mouldManagementWhole.getRealDate3()==null){
                mouldManagementWhole.setCurrentState("实际模具检验完成日期");
            }else if(mouldManagementWhole.getRealDate4()==null){
                mouldManagementWhole.setCurrentState("实际试样订单下达日期");
            }else if(mouldManagementWhole.getRealDate5()==null){
                mouldManagementWhole.setCurrentState("实际试样铸件完成日期");
            }else if(mouldManagementWhole.getRealDate6()==null){
                mouldManagementWhole.setCurrentState("样实际品检验完成日期");
            }else if(mouldManagementWhole.getRealDate7()==null){
                mouldManagementWhole.setCurrentState("实际加工(厂内/厂外）完成日期");
            }else if(mouldManagementWhole.getRealDate8()==null){
                mouldManagementWhole.setCurrentState("实际试装物料确认到位日期");
            }else if(mouldManagementWhole.getRealDate9()==null){
                mouldManagementWhole.setCurrentState("实际样品装配试压完成日期");
            }else if(mouldManagementWhole.getRealDate10()==null){
                mouldManagementWhole.setCurrentState("实际试样完成日期（品管签字）");
            }else{
                mouldManagementWhole.setCurrentState("完成");
            }
        }
        ExcelUtils.defaultExport(records, MouldManagementWholeEntity.class, "模具清单" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public Boolean deleteMouldManagementWholeReport(List<MouldManagementWholeEntity> MouldManagementWholeEntities) {
        return null;
    }

    @Override
    public Boolean insertMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWhole) {
        mouldManagementWhole.setPlanDate1(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),2));
        mouldManagementWhole.setPlanDate2(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),20));
        mouldManagementWhole.setPlanDate3(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),3));
        mouldManagementWhole.setPlanDate4(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),1));
        mouldManagementWhole.setPlanDate5(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),25));
        mouldManagementWhole.setPlanDate6(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),2));
        mouldManagementWhole.setPlanDate7(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),3));
        mouldManagementWhole.setPlanDate8(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),1));
        mouldManagementWhole.setPlanDate9(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),1));
        mouldManagementWhole.setPlanDate10(cn.hutool.core.date.DateUtil.offsetDay(mouldManagementWhole.getPrDate(),1));
        return this.reportMapper.insertMouldManagementWholeReport(mouldManagementWhole);
    }

    @Override
    public Boolean updateMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWhole) {
        return this.reportMapper.updateMouldManagementWholeReport(mouldManagementWhole);
    }

    @Override
    public IPage<JitManagementEntity> getJitManagementReport(IPage<JitManagementEntity> page, JitManagementEntity JitManagementEntity) {
        return this.reportMapper.getJitManagementReport(page,JitManagementEntity);
    }

    @Override
    public void exportJitManagementReport(JitManagementEntity JitManagement, HttpServletResponse response) {
        List<JitManagementEntity> jitManagementReportList = this.reportMapper.getJitManagementReportList(JitManagement);
        ExcelUtils.defaultExport(jitManagementReportList, JitManagementEntity.class, "JIT清单管理" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public Boolean deleteJitManagementReport(List<JitManagementEntity> JitManagementEntities) {
        return null;
    }

    @Override
    public Boolean insertJitManagementReport(List<JitManagementEntity> JitManagementEntities) {
        BladeUser user = AuthUtil.getUser();
        for (JitManagementEntity jitManagementEntity : JitManagementEntities) {
            jitManagementEntity.setCreateUser(user.getUserId());
            jitManagementEntity.setCreateTime(new Date());
            this.reportMapper.insertJitManagementReport(jitManagementEntity);
        }
        return true;
    }

    @Override
    public Boolean updateJitManagementReport(List<JitManagementEntity> JitManagementEntities) {
        BladeUser user = AuthUtil.getUser();
        for (JitManagementEntity jitManagementEntity : JitManagementEntities) {
            List<JitManagementEntity> jitManagementReportList = this.reportMapper.getJitManagementReportList(jitManagementEntity);
            JitManagementEntity jitManagementEntityOld = jitManagementReportList.get(0);
            jitManagementEntityOld.setUpdateUser(user.getUserId());
            jitManagementEntityOld.setUpdateTime(new Date());
            this.reportMapper.insertJitManagementHistory(jitManagementEntityOld);
            jitManagementEntity.setUpdateUser(user.getUserId());
            jitManagementEntity.setUpdateTime(new Date());
            this.reportMapper.updateJitManagementReport(jitManagementEntity);
        }
        return true;
    }


    @Override
    public IPage<SupplierOutputVo> getsupplierOutputReport(IPage<SupplierOutputVo> page, SupplierOutputVo supplierOutputVo) {

        IPage<SupplierOutputVo> productionCapacityList = this.reportMapper.getProductionCapacityList(page,supplierOutputVo);
        return productionCapacityList;
    }

    @Override
    public List<SupplierOutputQZVo> getQZOutputReport(IPage<Object> page, SupplierOutputVo supplierOutputVo) {
        // 找出总条数、占用总公时、占用总金额
        List<SupplierOutputQZVo> supplierOutputQZVoList = this.reportMapper.getSupplierOutputInfoQZList(page,supplierOutputVo);

        for (SupplierOutputQZVo item : supplierOutputQZVoList) {

            String zgsAndZje = this.reportMapper.getZgsAndZjeBySupName(item.getSupName());

            String zgs = new BigDecimal(zgsAndZje.split("-")[0]).divide(new BigDecimal("2"),2,RoundingMode.HALF_UP).toString(); //总工时/2 （冷喷热喷各一半）
            String zje = new BigDecimal(zgsAndZje.split("-")[1]).divide(new BigDecimal("2"),2,RoundingMode.HALF_UP).toString();; //总金额/2 （冷喷热喷各一半）
            String zygs = item.getZygs(); // 占用工时
            String zyje = item.getZyje(); // 占用金额
            String ycgs = new BigDecimal(zgs).subtract(new BigDecimal(zygs)).setScale(BigDecimal.ROUND_HALF_UP, 2).toString();
            String ycje = new BigDecimal(zje).subtract(new BigDecimal(zyje)).setScale(BigDecimal.ROUND_HALF_UP, 2).toString();
            String gssycbl = new BigDecimal(ycgs).multiply(new BigDecimal("100")).divide(new BigDecimal(zgs),2,RoundingMode.HALF_UP).toString()+"%";

            item.setZgs(zgs);
            item.setZje(zje);
            item.setZygs(zygs);
            item.setZyje(zyje);
            item.setYcgs(ycgs);
            item.setYcje(ycje);
            item.setGysycbl(gssycbl);
        }

        return supplierOutputQZVoList;
    }


    @Override
    public void supplierOutputReportQZJob() {
        //供应商产能溢出报表（球座）的业务逻辑
        //先清表
        this.reportMapper.deleteSupplierOutputInfoQZ();

        //从oracle找到所有的供应商、物料、需求数量
        List<SupplierOutputFromOracle> supplierOutputFromOracleList = this.reportMapper.getSupplierOutputFromOracleListQZ();
        for (SupplierOutputFromOracle supplierOutputFromOracle : supplierOutputFromOracleList) {
            //先拆解，在匹配数据，然后丢到 atw_supplier_output_info_qz
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(supplierOutputFromOracle.getItemName());

            SupplierOutputInfoQZEntity supplierOutputInfoEntity = new SupplierOutputInfoQZEntity();
            supplierOutputInfoEntity.setSupName(supplierOutputFromOracle.getSupName());
            supplierOutputInfoEntity.setSupCode(supplierOutputFromOracle.getSupCode());
            supplierOutputInfoEntity.setItemCode(supplierOutputFromOracle.getItemCode());
            supplierOutputInfoEntity.setItemName(supplierOutputFromOracle.getItemName());
            supplierOutputInfoEntity.setMaterialType(itemInfoEntity.getItemize());
            supplierOutputInfoEntity.setCj(itemInfoEntity.getSize());
            supplierOutputInfoEntity.setType(itemInfoEntity.getForm());
            supplierOutputInfoEntity.setBj(itemInfoEntity.getPound());
            supplierOutputInfoEntity.setTc(itemInfoEntity.getCoat());
            supplierOutputInfoEntity.setDj(itemInfoEntity.getGrade());
            supplierOutputInfoEntity.setMaterial(itemInfoEntity.getMaterial());
            supplierOutputInfoEntity.setNumber(supplierOutputFromOracle.getReqNum());
            supplierOutputInfoEntity.setDate(supplierOutputFromOracle.getDate());

            //获取产能 工艺
            System.out.println(supplierOutputInfoEntity.getItemName());
            String produceAndPrice = this.reportMapper.getProductionCapacityQZ(supplierOutputInfoEntity);
            if (produceAndPrice != null) {
                supplierOutputInfoEntity.setProduceCapacity(produceAndPrice.split("-")[0]);
                supplierOutputInfoEntity.setSinglePrice(produceAndPrice.split("-")[1]);
            }
            this.reportMapper.insertSupplierOutputInfoQZ(supplierOutputInfoEntity);
        }
    }

    @Override
    public void supplierOutputReportjob() {
        //供应商产能溢出报表的业务逻辑
        //先清表
        this.reportMapper.deleteSupplierOutputInfo();

        //从oracle找到所有的供应商、物料、数量
        List<SupplierOutputFromOracle> supplierOutputFromOracleList = this.reportMapper.getSupplierOutputFromOracleList();
        for (SupplierOutputFromOracle supplierOutputFromOracle : supplierOutputFromOracleList) {
            //先拆解，在匹配数据，然后丢到 atw_supplier_output_info
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(supplierOutputFromOracle.getItemName());

            //过滤掉一些无用数据
            if (itemInfoEntity == null || supplierOutputFromOracle.getItemCode().startsWith("200") ||
                supplierOutputFromOracle.getNum().equals("0")) {
                continue;
            }

            SupplierOutputInfoEntity supplierOutputInfoEntity = new SupplierOutputInfoEntity();
            supplierOutputInfoEntity.setSupName(supplierOutputFromOracle.getSupCode());
            supplierOutputInfoEntity.setSupCode(supplierOutputFromOracle.getSupCode());
            supplierOutputInfoEntity.setItemCode(supplierOutputFromOracle.getItemCode());
            supplierOutputInfoEntity.setItemName(supplierOutputFromOracle.getItemName());
            supplierOutputInfoEntity.setMaterialType(itemInfoEntity.getItemize());
            supplierOutputInfoEntity.setCj(itemInfoEntity.getSize());
            supplierOutputInfoEntity.setType(itemInfoEntity.getForm());
            supplierOutputInfoEntity.setBj(itemInfoEntity.getPound());
            supplierOutputInfoEntity.setFlStruct(itemInfoEntity.getFlange());
            supplierOutputInfoEntity.setSeries(itemInfoEntity.getSeries());
            supplierOutputInfoEntity.setMaterial(itemInfoEntity.getMaterial());
            supplierOutputInfoEntity.setNumber(supplierOutputFromOracle.getNum());

            //获取产能 工艺
            SupplierOutputInfoDto supplierOutputInfoDto = new SupplierOutputInfoDto();
            BeanUtil.copy(supplierOutputInfoEntity, supplierOutputInfoDto);

            SupplierOutputInfoDto productionCapacity = this.reportMapper.getProductionCapacity(supplierOutputInfoDto);
            if (productionCapacity != null) {
                supplierOutputInfoEntity.setOutputCent(productionCapacity.getProductionCapacity2());
                supplierOutputInfoEntity.setWeightCent(productionCapacity.getWeight());
                supplierOutputInfoEntity.setCastingProcess(productionCapacity.getCastingProcess());
                supplierOutputInfoEntity.setBottleneckProcesses(productionCapacity.getBottleneckProcesses());

            }/*else{
                SupplierOutputInfoDto productionCapacityByPriority = this.reportMapper.getProductionCapacityByPriority(supplierOutputInfoDto);
                if (productionCapacityByPriority != null) {
                    supplierOutputInfoEntity.setOutputCent(productionCapacityByPriority.getProductionCapacity2());
                    supplierOutputInfoEntity.setWeightCent(productionCapacityByPriority.getWeight());
                    supplierOutputInfoEntity.setCastingProcess(productionCapacityByPriority.getCastingProcess());
                    supplierOutputInfoEntity.setBottleneckProcesses(productionCapacityByPriority.getBottleneckProcesses());

                }

            }*/
            this.reportMapper.insertSupplierOutputInfo(supplierOutputInfoEntity);
        }

    }

    @Override
    public OutPutEchrtsOfSupplierVO getsupplierOutputEcharts(SupplierScheduleReq supplierScheduleReq) {
        List<SupplierOutputFromOracle> supplierOutputOfEcharts = this.reportMapper.getSupplierOutputOfEcharts(supplierScheduleReq);

        Date date = cn.hutool.core.date.DateUtil.date();
        String format = cn.hutool.core.date.DateUtil.format(date, "yyyy-MM");
        String oneWeek=format+"-07";
        String twoWeek=format+"-14";
        String threeWeek=format+"-21";
        String fourWeek=format+"-28";

        Date oneWeekDate = cn.hutool.core.date.DateUtil.parse(oneWeek);
        Date twoWeekDate = cn.hutool.core.date.DateUtil.parse(twoWeek);
        Date threeWeekDate = cn.hutool.core.date.DateUtil.parse(threeWeek);
        Date fourWeekDate = cn.hutool.core.date.DateUtil.parse(fourWeek);


        double totalCnNum= 0;;//产能总数量
        double totalDwlCnNum =0;//低温蜡产能总数量
        double totalZwlCnNum = 0;//中温蜡产能总数量
        double totalSzCnNum = 0;//砂铸产能总数量

        double totalGsNum= 0;;//工时总数量
        double totalDwlGsNum= 0;;//低温蜡工时总数量
        double totalZwlGsNum= 0;;//中温蜡工时总数量
        double totalSzGsNum= 0;;//砂铸工时总数量


        SupplierOutputFromOracle productionCapacityWithSup = this.reportMapper.getProductionCapacityWithSup(supplierScheduleReq);
        if (productionCapacityWithSup != null) {
            totalCnNum= Double.parseDouble(productionCapacityWithSup.getZcz());
            totalGsNum= Double.parseDouble(productionCapacityWithSup.getZgs());
        }

        List<SupplierOutputFromOracle> productionCapacityWithGy = this.reportMapper.getProductionCapacityWithGy(supplierScheduleReq);
        for (SupplierOutputFromOracle supplierOutputFromOracle:productionCapacityWithGy) {
            if (supplierOutputFromOracle.getSupCode().equals(supplierScheduleReq.getSupCode())) {
                //取对应供应商的数据
                if(supplierOutputFromOracle.getGy().equals("低温蜡")){
                    totalDwlCnNum+=Double.valueOf(supplierOutputFromOracle.getZcz()) ;
                    totalDwlGsNum+=Double.valueOf(supplierOutputFromOracle.getZgs()) ;
                } else if (supplierOutputFromOracle.getGy().equals("中温蜡")) {
                    totalZwlCnNum+=Double.valueOf(supplierOutputFromOracle.getZcz()) ;
                    totalZwlGsNum+=Double.valueOf(supplierOutputFromOracle.getZgs()) ;
                } else if (supplierOutputFromOracle.getGy().equals("砂铸")) {
                    totalSzCnNum+=Double.valueOf(supplierOutputFromOracle.getZcz()) ;
                    totalSzGsNum+=Double.valueOf(supplierOutputFromOracle.getZgs()) ;
                }

            }

        }

        totalDwlCnNum=totalDwlCnNum/4;
        totalDwlGsNum=totalDwlGsNum/4;

        totalZwlCnNum=totalZwlCnNum/4;
        totalZwlGsNum=totalZwlGsNum/4;

        totalSzCnNum=totalSzCnNum/4;
        totalSzGsNum=totalSzGsNum/4;




        //重量
        double oneWeekDwlZy=0;
        double oneWeekZwlZy=0;
        double oneWeekSzZy=0;

        double twoWeekDwlZy=0;
        double twoWeekZwlZy=0;
        double twoWeekSzZy=0;

        double threeWeekDwlZy=0;
        double threeWeekZwlZy=0;
        double threeWeekSzZy=0;

        double fourWeekDwlZy=0;
        double fourWeekZwlZy=0;
        double fourWeekSzZy=0;


        //工时
        double oneWeekDwlZyGS=0;
        double oneWeekZwlZyGS=0;
        double oneWeekSzZyGS=0;

        double twoWeekDwlZyGS=0;
        double twoWeekZwlZyGS=0;
        double twoWeekSzZyGS=0;

        double threeWeekDwlZyGS=0;
        double threeWeekZwlZyGS=0;
        double threeWeekSzZyGS=0;

        double fourWeekDwlZyGS=0;
        double fourWeekZwlZyGS=0;
        double fourWeekSzZyGS=0;

        for (SupplierOutputFromOracle supplierOutputFromOracle:supplierOutputOfEcharts) {
            if (StringUtils.isBlank(supplierOutputFromOracle.getProNo())  ) {
                //无项目按ww时间
                String outputCent = supplierOutputFromOracle.getOutputCent();//工时
                String weightCent = supplierOutputFromOracle.getWeightCent();//重量
                String num = String.valueOf(Double.valueOf(supplierOutputFromOracle.getNotRcvNum())*0.4);
                Date wwpoDate = supplierOutputFromOracle.getWwpoDate();
                Date planDate = supplierOutputFromOracle.getPlanDate();
                String gy = supplierOutputFromOracle.getGy();

                if (StringUtils.isBlank(num)||StringUtils.isBlank(outputCent)||StringUtils.isBlank(weightCent)||StringUtils.isBlank(gy)||"#N/A".equals(outputCent)||"#N/A".equals(weightCent)){
                    continue;
                }

                if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                    if ("低温蜡".equals(gy)) {
                        oneWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        oneWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        oneWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        oneWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        oneWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        oneWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                    if ("低温蜡".equals(gy)) {
                        twoWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        twoWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        twoWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        twoWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        twoWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        twoWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                    if ("低温蜡".equals(gy)) {
                        threeWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        threeWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        threeWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        threeWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        threeWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        threeWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                }else  {
                    if ("低温蜡".equals(gy)) {
                        fourWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        fourWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        fourWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        fourWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        fourWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        fourWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                }


            }else{
                //有项目的按计划时间
                String outputCent = supplierOutputFromOracle.getOutputCent();//工时
                String weightCent = supplierOutputFromOracle.getWeightCent();//重量
                String num = supplierOutputFromOracle.getReqNum();
                Date wwpoDate = supplierOutputFromOracle.getWwpoDate();
                Date planDate = supplierOutputFromOracle.getPlanDate();
                String gy = supplierOutputFromOracle.getGy();

                if (StringUtils.isBlank(num)||StringUtils.isBlank(outputCent)||StringUtils.isBlank(weightCent)||StringUtils.isBlank(gy)||"#N/A".equals(outputCent)||"#N/A".equals(weightCent)){
                    continue;
                }

                if (cn.hutool.core.date.DateUtil.compare(planDate, oneWeekDate)<=0) {
                    if ("低温蜡".equals(gy)) {
                        oneWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        oneWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        oneWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        oneWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        oneWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        oneWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                } else if (cn.hutool.core.date.DateUtil.compare(planDate, twoWeekDate)<=0) {
                    if ("低温蜡".equals(gy)) {
                        twoWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        twoWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        twoWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        twoWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        twoWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        twoWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                }else if (cn.hutool.core.date.DateUtil.compare(planDate, threeWeekDate)<=0) {
                    if ("低温蜡".equals(gy)) {
                        threeWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        threeWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        threeWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        threeWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        threeWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        threeWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                }else {
                    if ("低温蜡".equals(gy)) {
                        fourWeekDwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        fourWeekDwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("中温蜡".equals(gy)) {
                        fourWeekZwlZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        fourWeekZwlZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    } else if ("砂铸".equals(gy)) {
                        fourWeekSzZy+=Double.parseDouble(num)*Double.parseDouble(weightCent);
                        fourWeekSzZyGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }

                }
            }
        }

        String[] dwlNum = {String.format("%.2f",totalDwlCnNum), String.format("%.2f",totalDwlCnNum), String.format("%.2f",totalDwlCnNum), String.format("%.2f",totalDwlCnNum)};
        String[] zwlNum = {String.format("%.2f",totalZwlCnNum), String.format("%.2f",totalZwlCnNum), String.format("%.2f",totalZwlCnNum), String.format("%.2f",totalZwlCnNum)};
        String[] szNum = {String.format("%.2f",totalSzCnNum), String.format("%.2f",totalSzCnNum), String.format("%.2f",totalSzCnNum), String.format("%.2f",totalSzCnNum)};

        String[] dwlGsNum = {String.format("%.2f",totalDwlGsNum), String.format("%.2f",totalDwlGsNum), String.format("%.2f",totalDwlGsNum), String.format("%.2f",totalDwlGsNum)};
        String[] zwlGsNum = {String.format("%.2f",totalZwlGsNum), String.format("%.2f",totalZwlGsNum), String.format("%.2f",totalZwlGsNum), String.format("%.2f",totalZwlGsNum)};
        String[] szGsNum = {String.format("%.2f",totalSzGsNum), String.format("%.2f",totalSzGsNum), String.format("%.2f",totalSzGsNum), String.format("%.2f",totalSzGsNum)};

        String[] dwlZyGsNum ={String.format("%.2f",oneWeekDwlZyGS), String.format("%.2f",twoWeekDwlZyGS), String.format("%.2f",threeWeekDwlZyGS), String.format("%.2f",fourWeekDwlZyGS)};
        String[] zwlZyGsNum ={String.format("%.2f",oneWeekZwlZyGS), String.format("%.2f",twoWeekZwlZyGS), String.format("%.2f",threeWeekZwlZyGS), String.format("%.2f",fourWeekZwlZyGS)};
        String[] szZyGsNum ={String.format("%.2f",oneWeekSzZyGS), String.format("%.2f",twoWeekSzZyGS), String.format("%.2f",threeWeekSzZyGS), String.format("%.2f",fourWeekSzZyGS)};

        String[] dwlZyNum ={String.format("%.2f",oneWeekDwlZy), String.format("%.2f",twoWeekDwlZy), String.format("%.2f",threeWeekDwlZy), String.format("%.2f",fourWeekDwlZy)};
        String[] zwlZyNum ={String.format("%.2f",oneWeekZwlZy), String.format("%.2f",twoWeekZwlZy), String.format("%.2f",threeWeekZwlZy), String.format("%.2f",fourWeekZwlZy)};
        String[] szZyNum ={String.format("%.2f",oneWeekSzZy), String.format("%.2f",twoWeekSzZy), String.format("%.2f",threeWeekSzZy), String.format("%.2f",fourWeekSzZy)};

        OutPutEchrtsOfSupplierVO outPutEchrtsOfSupplierVO=new OutPutEchrtsOfSupplierVO();
        outPutEchrtsOfSupplierVO.setDwlNum(dwlNum);
        outPutEchrtsOfSupplierVO.setZwlNum(zwlNum);
        outPutEchrtsOfSupplierVO.setSzNum(szNum);

        outPutEchrtsOfSupplierVO.setDwlGsNum(dwlGsNum);
        outPutEchrtsOfSupplierVO.setZwlGsNum(zwlGsNum);
        outPutEchrtsOfSupplierVO.setSzGsNum(szGsNum);

        outPutEchrtsOfSupplierVO.setDwlZyNum(dwlZyNum);
        outPutEchrtsOfSupplierVO.setZwlZyNum(zwlZyNum);
        outPutEchrtsOfSupplierVO.setSzZyNum(szZyNum);

        outPutEchrtsOfSupplierVO.setDwlZyGsNum(dwlZyGsNum);
        outPutEchrtsOfSupplierVO.setZwlZyGsNum(zwlZyGsNum);
        outPutEchrtsOfSupplierVO.setSzZyGsNum(szZyGsNum);


        outPutEchrtsOfSupplierVO.setTotalCnNum(String.format("%.2f",oneWeekDwlZy+twoWeekDwlZy+threeWeekDwlZy+fourWeekDwlZy+oneWeekZwlZy+twoWeekZwlZy+threeWeekZwlZy+fourWeekZwlZy+oneWeekSzZy+twoWeekSzZy+threeWeekSzZy+fourWeekSzZy));
        outPutEchrtsOfSupplierVO.setTotalGsNum(String.format("%.2f",oneWeekDwlZyGS+twoWeekDwlZyGS+threeWeekDwlZyGS+fourWeekDwlZyGS+oneWeekZwlZyGS+twoWeekZwlZyGS+threeWeekZwlZyGS+fourWeekZwlZyGS+oneWeekSzZyGS+twoWeekSzZyGS+threeWeekSzZyGS+fourWeekSzZyGS));



        outPutEchrtsOfSupplierVO.setTotalDwlCnNum(String.format("%.2f",oneWeekDwlZy+twoWeekDwlZy+threeWeekDwlZy+fourWeekDwlZy));
        outPutEchrtsOfSupplierVO.setTotalDwlGsNum(String.format("%.2f",oneWeekDwlZyGS+twoWeekDwlZyGS+threeWeekDwlZyGS+fourWeekDwlZyGS));
        outPutEchrtsOfSupplierVO.setTotalZwlCnNum(String.format("%.2f",oneWeekZwlZy+twoWeekZwlZy+threeWeekZwlZy+fourWeekZwlZy));
        outPutEchrtsOfSupplierVO.setTotalZwlGsNum(String.format("%.2f",oneWeekZwlZyGS+twoWeekZwlZyGS+threeWeekZwlZyGS+fourWeekZwlZyGS));
        outPutEchrtsOfSupplierVO.setTotalSzCnNum(String.format("%.2f",oneWeekSzZy+twoWeekSzZy+threeWeekSzZy+fourWeekSzZy));
        outPutEchrtsOfSupplierVO.setTotalSzGsNum(String.format("%.2f",oneWeekSzZyGS+twoWeekSzZyGS+threeWeekSzZyGS+fourWeekSzZyGS));

        return outPutEchrtsOfSupplierVO;
    }

    @Override
    public OutPutEchrtsOfPtphVO getPtphOutputEcharts(SupplierScheduleReq supplierScheduleReq) {


        OutPutEchrtsOfPtphVO OutPutEchrtsOfPtphVO=new OutPutEchrtsOfPtphVO();




        Date date = cn.hutool.core.date.DateUtil.nextMonth();

        String format = cn.hutool.core.date.DateUtil.format(date, "yyyy-MM");
        String oneWeek=format+"-07";
        String twoWeek=format+"-14";
        String threeWeek=format+"-21";
        String fourWeek=format+"-28";

        Date nowdate = cn.hutool.core.date.DateUtil.date();
        String nowdateformat = cn.hutool.core.date.DateUtil.format(nowdate, "yyyy-MM");
        String startdate=nowdateformat+"-20";

        Date end =offsetMonth(new DateTime(), 2);
        String enddateformat = cn.hutool.core.date.DateUtil.format(end, "yyyy-MM");
        String enddate=enddateformat+"-10";

        List<SupplierOutputFromOracle> PtphOutputOfEcharts = this.reportMapper.getPtphOutputOfEcharts(startdate,enddate);


        Date oneWeekDate = cn.hutool.core.date.DateUtil.parse(oneWeek);
        Date twoWeekDate = cn.hutool.core.date.DateUtil.parse(twoWeek);
        Date threeWeekDate = cn.hutool.core.date.DateUtil.parse(threeWeek);
        Date fourWeekDate = cn.hutool.core.date.DateUtil.parse(fourWeek);

        double oneWeekGS=0;
        double twoWeekGS=0;
        double threeWeekGS=0;
        double fourWeekGS=0;

        for (SupplierOutputFromOracle supplierOutputFromOracle:PtphOutputOfEcharts) {

            if(StringUtils.isNotBlank(supplierOutputFromOracle.getProNo())){
                //有项目的用需求数量

            }else{
                //无项目的用未到货数量
                supplierOutputFromOracle.setReqNum(supplierOutputFromOracle.getNotRcvNum());
            }

            String outputCent = supplierOutputFromOracle.getOutputCent();//工时
            String weightCent = supplierOutputFromOracle.getWeightCent();//重量
            String num = supplierOutputFromOracle.getReqNum();
            Date wwpoDate = supplierOutputFromOracle.getWwpoDate();
            Date planDate = supplierOutputFromOracle.getPlanDate();
            String gy = supplierOutputFromOracle.getGy();

            /*if (StringUtils.isBlank(num)||StringUtils.isBlank(outputCent)||StringUtils.isBlank(weightCent)||StringUtils.isBlank(gy)||"#N/A".equals(outputCent)||"#N/A".equals(weightCent)){
                continue;
            }*/

            if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                oneWeekGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);

            } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                twoWeekGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);

            }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                threeWeekGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
            }else {
                fourWeekGS+=Double.parseDouble(num)*Double.parseDouble(outputCent);
            }
        }

        double zygs=oneWeekGS+twoWeekGS+threeWeekGS+fourWeekGS;
        double ycz=313.2-zygs;
        double ycbl=ycz/313.2*100;
        String titleText="总工时："+313.2 +"占用工时："+String.format("%.2f", zygs)+"溢出值："+String.format("%.2f", ycz)+"溢出比例："+String.format("%.2f", ycbl)+"%";

        String[] PtphNum = {String.format("%.2f",oneWeekGS), String.format("%.2f",twoWeekGS), String.format("%.2f",threeWeekGS), String.format("%.2f",fourWeekGS)};
        OutPutEchrtsOfPtphVO.setPtphNum(PtphNum);
        OutPutEchrtsOfPtphVO.setTitleText(titleText);
        return OutPutEchrtsOfPtphVO;
    }

    @Override
    public OutPutEchrtsOfDjVO getDjOutputEcharts(SupplierScheduleReq supplierScheduleReq) {

        OutPutEchrtsOfDjVO OutPutEchrtsOfDjVO=new OutPutEchrtsOfDjVO();



        Date nowdate = cn.hutool.core.date.DateUtil.date();
        String nowdateformat = cn.hutool.core.date.DateUtil.format(nowdate, "yyyy-MM");
        String startdate=nowdateformat+"-20";

        Date end =offsetMonth(new DateTime(), 2);
        String enddateformat = cn.hutool.core.date.DateUtil.format(end, "yyyy-MM");
        String enddate=enddateformat+"-10";


        Date nextMonthdate = cn.hutool.core.date.DateUtil.nextMonth();
        String format = cn.hutool.core.date.DateUtil.format(nextMonthdate, "yyyy-MM");
        String oneWeek=format+"-07";
        String twoWeek=format+"-14";
        String threeWeek=format+"-21";
        String fourWeek=format+"-28";
        String firstday=format+"-01";




        List<SupplierOutputFromOracle> DjOutputOfEcharts = this.reportMapper.getDjOutputOfEcharts(startdate,enddate);

        Date oneWeekDate = cn.hutool.core.date.DateUtil.parse(oneWeek);
        Date twoWeekDate = cn.hutool.core.date.DateUtil.parse(twoWeek);
        Date threeWeekDate = cn.hutool.core.date.DateUtil.parse(threeWeek);
        Date fourWeekDate = cn.hutool.core.date.DateUtil.parse(fourWeek);

        double oneWeekGS1=0;
        double twoWeekGS1=0;
        double threeWeekGS1=0;
        double fourWeekGS1=0;

        double oneWeekGS2=0;
        double twoWeekGS2=0;
        double threeWeekGS2=0;
        double fourWeekGS2=0;

        double oneWeekGS3=0;
        double twoWeekGS3=0;
        double threeWeekGS3=0;
        double fourWeekGS3=0;

        double oneWeekGS4=0;
        double twoWeekGS4=0;
        double threeWeekGS4=0;
        double fourWeekGS4=0;

        double oneWeekGS5=0;
        double twoWeekGS5=0;
        double threeWeekGS5=0;
        double fourWeekGS5=0;

        double oneWeekGS6=0;
        double twoWeekGS6=0;
        double threeWeekGS6=0;
        double fourWeekGS6=0;

        double oneWeekGS7=0;
        double twoWeekGS7=0;
        double threeWeekGS7=0;
        double fourWeekGS7=0;

        Double[] DjAvgNum1 = new Double[4];
        Double[] DjAvgNum2 = new Double[4];
        Double[] DjAvgNum3 = new Double[4];
        Double[] DjAvgNum4 = new Double[4];
        Double[] DjAvgNum5 = new Double[4];
        Double[] DjAvgNum6 = new Double[4];
        Double[] DjAvgNum7 = new Double[4];

        for (SupplierOutputFromOracle supplierOutputFromOracle:DjOutputOfEcharts) {


            if(StringUtils.isNotBlank(supplierOutputFromOracle.getProNo())){
                //有项目的用需求数量

            }else{
                //无项目的用未到货数量
                supplierOutputFromOracle.setReqNum(supplierOutputFromOracle.getNotRcvNum());
            }



            //拆解  长管中法兰锻件-10PA1-C-D-XL-F316L
            String itemName = supplierOutputFromOracle.getItemName();
            String[] itemNameSplit = itemName.split("-");
            String cz = itemNameSplit[itemNameSplit.length - 1];//材质
            String materialType = itemNameSplit[0];//材料类型
            String kjbj = itemNameSplit[1];//口径、磅级   阀盘锻件的口径
            String fpbj = itemNameSplit[2];//阀盘锻件的磅级

            String kj="";//口径
            String bj="";//磅级

            if("A105,LF2".indexOf(cz)>-1){
                cz="CS";
            }else{
                cz="SS";
            }

            if(materialType.indexOf("阀盘锻件")>-1){
                kj=kjbj;
                bj=fpbj;
            }else {
                ItemInfoEntityOfZDJ itemInfoOfZhuDuanJian = getItemInfoOfZhuDuanJian(itemName);
                kj=itemInfoOfZhuDuanJian.getSize();
                bj=itemInfoOfZhuDuanJian.getPound();
            }

            if (materialType.indexOf("阀帽锻件")>-1) {
                materialType="阀帽锻件";
            } else if (materialType.indexOf("阀盘锻件")>-1) {
                materialType="阀盘锻件";
            } else if (materialType.indexOf("阀体锻件")>-1) {
                materialType="阀体锻件";
            } else if (materialType.indexOf("中法兰锻件")>-1) {
                materialType="中法兰锻件";
            }

            //通过materialType kj bj CZ 还有供应商查找对应的工时
            SupplierOutputFromOracle djOutput = this.reportMapper.getDjOutput(materialType, kj, bj, cz, supplierScheduleReq.getSupCode());

            if(djOutput!=null){
                String outputCent = djOutput.getOutputCent();//工时
                String weightCent = supplierOutputFromOracle.getWeightCent();//重量
                String num = supplierOutputFromOracle.getReqNum();
                Date wwpoDate = supplierOutputFromOracle.getWwpoDate();
                Date planDate = supplierOutputFromOracle.getPlanDate();
                String gy = supplierOutputFromOracle.getGy();

            /*if (StringUtils.isBlank(num)||StringUtils.isBlank(outputCent)||StringUtils.isBlank(weightCent)||StringUtils.isBlank(gy)||"#N/A".equals(outputCent)||"#N/A".equals(weightCent)){
                continue;
            }*/
                if(materialType.equals("中法兰锻件")){
                    if(Double.parseDouble(kj)>=1 && Double.parseDouble(kj)<=4){
                        //图形1

                        double v = Double.parseDouble(djOutput.getZgs()) / 4;
                        DjAvgNum1 = new Double[]{v, v, v, v};


                        if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                            oneWeekGS1+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                            twoWeekGS1+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                            threeWeekGS1+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }else {
                            fourWeekGS1+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }
                    }else {
                        //图形2

                        double v = Double.parseDouble(djOutput.getZgs()) / 4;
                        DjAvgNum2 = new Double[]{v, v, v, v};

                        if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                            oneWeekGS2+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                            twoWeekGS2+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                            threeWeekGS2+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }else {
                            fourWeekGS2+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }
                    }
                }

                if(materialType.equals("阀盘锻件")){
                    //图形3
                    double v = Double.parseDouble(djOutput.getZgs()) / 4;
                    DjAvgNum3 = new Double[]{v, v, v, v};

                    if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                        oneWeekGS3+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                    } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                        twoWeekGS3+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                    }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                        threeWeekGS3+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }else {
                        fourWeekGS3+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                    }
                }

                if(materialType.equals("阀体锻件")){
                    if(Double.parseDouble(kj)>=1 && Double.parseDouble(kj)<=3){
                        //图形4
                        double v = Double.parseDouble(djOutput.getZgs()) / 4;
                        DjAvgNum4 = new Double[]{v, v, v, v};

                        if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                            oneWeekGS4+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                            twoWeekGS4+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                            threeWeekGS4+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }else {
                            fourWeekGS4+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }
                    }else {
                        //图形5
                        double v = Double.parseDouble(djOutput.getZgs()) / 4;
                        DjAvgNum5 = new Double[]{v, v, v, v};

                        if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                            oneWeekGS5+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                            twoWeekGS5+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                            threeWeekGS5+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }else {
                            fourWeekGS5+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }
                    }
                }

                if(materialType.equals("阀帽锻件")){
                    if(Double.parseDouble(kj)>=1 && Double.parseDouble(kj)<=4){
                        //图形6
                        double v = Double.parseDouble(djOutput.getZgs()) / 4;
                        DjAvgNum6 = new Double[]{v, v, v, v};

                        if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                            oneWeekGS6+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                            twoWeekGS6+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                            threeWeekGS6+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }else {
                            fourWeekGS6+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }
                    }else {
                        //图形7
                        double v = Double.parseDouble(djOutput.getZgs()) / 4;
                        DjAvgNum7 = new Double[]{v, v, v, v};

                        if (cn.hutool.core.date.DateUtil.compare(wwpoDate, oneWeekDate)<=0) {
                            oneWeekGS7+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        } else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, twoWeekDate)<=0) {
                            twoWeekGS7+=Double.parseDouble(num)*Double.parseDouble(outputCent);

                        }else if (cn.hutool.core.date.DateUtil.compare(wwpoDate, threeWeekDate)<=0) {
                            threeWeekGS7+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }else {
                            fourWeekGS7+=Double.parseDouble(num)*Double.parseDouble(outputCent);
                        }
                    }
                }


            }


        }

        String[] DjNum1 = {String.format("%.2f",oneWeekGS1), String.format("%.2f",twoWeekGS1), String.format("%.2f",threeWeekGS1), String.format("%.2f",fourWeekGS1)};
        String[] DjNum2 = {String.format("%.2f",oneWeekGS2), String.format("%.2f",twoWeekGS2), String.format("%.2f",threeWeekGS2), String.format("%.2f",fourWeekGS2)};
        String[] DjNum3 = {String.format("%.2f",oneWeekGS3), String.format("%.2f",twoWeekGS3), String.format("%.2f",threeWeekGS3), String.format("%.2f",fourWeekGS3)};
        String[] DjNum4 = {String.format("%.2f",oneWeekGS4), String.format("%.2f",twoWeekGS4), String.format("%.2f",threeWeekGS4), String.format("%.2f",fourWeekGS4)};
        String[] DjNum5 = {String.format("%.2f",oneWeekGS5), String.format("%.2f",twoWeekGS5), String.format("%.2f",threeWeekGS5), String.format("%.2f",fourWeekGS5)};
        String[] DjNum6 = {String.format("%.2f",oneWeekGS6), String.format("%.2f",twoWeekGS6), String.format("%.2f",threeWeekGS6), String.format("%.2f",fourWeekGS6)};
        String[] DjNum7 = {String.format("%.2f",oneWeekGS7), String.format("%.2f",twoWeekGS7), String.format("%.2f",threeWeekGS7), String.format("%.2f",fourWeekGS7)};

        OutPutEchrtsOfDjVO.setDjNum1(DjNum1);
        OutPutEchrtsOfDjVO.setDjNum2(DjNum2);
        OutPutEchrtsOfDjVO.setDjNum3(DjNum3);
        OutPutEchrtsOfDjVO.setDjNum4(DjNum4);
        OutPutEchrtsOfDjVO.setDjNum5(DjNum5);
        OutPutEchrtsOfDjVO.setDjNum6(DjNum6);
        OutPutEchrtsOfDjVO.setDjNum7(DjNum7);

        OutPutEchrtsOfDjVO.setDjAvgNum1(DjAvgNum1);
        OutPutEchrtsOfDjVO.setDjAvgNum2(DjAvgNum2);
        OutPutEchrtsOfDjVO.setDjAvgNum3(DjAvgNum3);
        OutPutEchrtsOfDjVO.setDjAvgNum4(DjAvgNum4);
        OutPutEchrtsOfDjVO.setDjAvgNum5(DjAvgNum5);
        OutPutEchrtsOfDjVO.setDjAvgNum6(DjAvgNum6);
        OutPutEchrtsOfDjVO.setDjAvgNum7(DjAvgNum7);
        return OutPutEchrtsOfDjVO;
    }

    @Override
    public OutPutEchrtsOfQZVO getQZOutputEcharts(SupplierScheduleReq supplierScheduleReq) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        OutPutEchrtsOfQZVO outPutEchrtsOfQZVO = new OutPutEchrtsOfQZVO();

        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(nowDate);
        calendar.add(Calendar.MONTH, 1);
        nowDate = calendar.getTime();
        String nextMonth = format.format(nowDate);

        String startWeek = nextMonth + "-01";
        String oneWeek = nextMonth + "-07";
        String twoWeek = nextMonth + "-14";
        String threeWeek = nextMonth + "-21";
        String fourWeek = nextMonth + "-28";

        Date startWeekDate = cn.hutool.core.date.DateUtil.parse(startWeek);
        Date oneWeekDate = cn.hutool.core.date.DateUtil.parse(oneWeek);
        Date twoWeekDate = cn.hutool.core.date.DateUtil.parse(twoWeek);
        Date threeWeekDate = cn.hutool.core.date.DateUtil.parse(threeWeek);
        Date fourWeekDate = cn.hutool.core.date.DateUtil.parse(fourWeek);

        // 详细数据
        List<SupplierOutputInfoQZEntity> supplierOutputInfoQZEntityList = this.reportMapper.getQZOutputOfEcharts(supplierScheduleReq.getSupCode());

        //总工时（平均）
        String zgsAndZje = this.reportMapper.getZgsAndZjeBySupCode(supplierScheduleReq.getSupCode());
        if(zgsAndZje==null) {
            throw new RuntimeException("找不到该供应商的球座分析信息");
        }
        String zgs = new BigDecimal(zgsAndZje.split("-")[0]).divide(new BigDecimal("2"),2,RoundingMode.HALF_UP).toString(); //总工时/2 （冷喷热喷各一半）
        String zje = new BigDecimal(zgsAndZje.split("-")[1]).divide(new BigDecimal("2"),2,RoundingMode.HALF_UP).toString();; //总金额/2 （冷喷热喷各一半）
        double lpZgs = Double.valueOf(zgs);
        double lpZje = Double.valueOf(zje);
        double rpZgs = Double.valueOf(zgs);
        double rpZje = Double.valueOf(zje);

        double aveLpZgs = lpZgs / 4;
        double aveLpZje = lpZje / 4;
        double aveRpZgs = rpZgs / 4;
        double aveRpZje = rpZje / 4;

        // 前期准备数据
        double totalLpZgs = 0;//冷喷总工时
        double totalLpZje = 0;//冷喷总金额
        double totalRpZgs = 0;//热喷总工时
        double totalRpZje = 0;//热喷总金额

        double oneWeekLpZgs = 0;// 第一周冷喷总工时
        double oneWeekLpZje = 0;// 第一周冷喷总金额
        double twoWeekLpZgs = 0;// 第二周冷喷总工时
        double twoWeekLpZje = 0;// 第二周冷喷总金额
        double threeWeekLpZgs = 0;// 第三周冷喷总工时
        double threeWeekLpZje = 0;// 第三周冷喷总金额
        double fourWeekLpZgs = 0;// 第四周冷喷总工时
        double fourWeekLpZje = 0;// 第四周冷喷总金额

        double oneWeekRpZgs = 0;// 第一周热喷总工时
        double oneWeekRpZje = 0;// 第一周热喷总金额
        double twoWeekRpZgs = 0;// 第二周热喷总工时
        double twoWeekRpZje = 0;// 第二周热喷总金额
        double threeWeekRpZgs = 0;// 第三周热喷总工时
        double threeWeekRpZje = 0;// 第三周热喷总金额
        double fourWeekRpZgs = 0;// 第四周热喷总工时
        double fourWeekRpZje = 0;// 第四周热喷总金额

        for(SupplierOutputInfoQZEntity item:supplierOutputInfoQZEntityList) {
            double reqNum = Double.valueOf(item.getNumber()); // 数量
            double gs = Double.valueOf(item.getProduceCapacity());//工时
            double je = Double.valueOf(item.getSinglePrice());// 金额
            String tc = item.getTc(); // 图层
            Date date = null;

            try {
                date = sdf.parse(item.getDate()); // 日期
            }catch (Exception e) {
                throw new RuntimeException("时间格式转换错误");
            }

            if ("G14".equals(tc) || "G20".equals(tc)) { //冷喷
                totalLpZgs = totalLpZgs + gs * reqNum /60;
                totalLpZje = totalLpZje + je * reqNum;
                if(date.compareTo(startWeekDate)>=0 && date.compareTo(oneWeekDate)<=0) {
                    oneWeekLpZgs = oneWeekLpZgs + gs*reqNum/60;
                    oneWeekLpZje = oneWeekLpZje + je*reqNum;
                } else if (date.compareTo(oneWeekDate) > 0 && date.compareTo(twoWeekDate)<=0) {
                    twoWeekLpZgs = twoWeekLpZgs + gs*reqNum/60;
                    twoWeekLpZje = twoWeekLpZje + je*reqNum;
                } else if (date.compareTo(twoWeekDate) > 0 && date.compareTo(threeWeekDate)<=0) {
                    threeWeekLpZgs = threeWeekLpZgs + gs*reqNum/60;
                    threeWeekLpZje = threeWeekLpZje + je*reqNum;
                } else if (date.compareTo(threeWeekDate) > 0 && date.compareTo(fourWeekDate)<=0) {
                    fourWeekLpZgs = fourWeekRpZgs + gs*reqNum/60;
                    fourWeekLpZje = fourWeekLpZje + je*reqNum;
                } else {
                    continue;
                }
            } else { // 热喷
                totalRpZgs = totalRpZgs + gs * reqNum / 60;
                totalRpZje = totalRpZje + je * reqNum;
                if(date.compareTo(startWeekDate)>=0 && date.compareTo(oneWeekDate)<=0) {
                    oneWeekRpZgs = oneWeekRpZgs + gs*reqNum/60;
                    oneWeekRpZje = oneWeekRpZje + je*reqNum;
                } else if (date.compareTo(oneWeekDate) > 0 && date.compareTo(twoWeekDate)<=0) {
                    twoWeekRpZgs = twoWeekRpZgs + gs*reqNum/60;
                    twoWeekRpZje = twoWeekRpZje + je*reqNum;
                } else if (date.compareTo(twoWeekDate) > 0 && date.compareTo(threeWeekDate)<=0) {
                    threeWeekRpZgs = threeWeekRpZgs + gs*reqNum/60;
                    threeWeekRpZje = threeWeekRpZje + je*reqNum;
                } else if (date.compareTo(threeWeekDate) > 0 && date.compareTo(fourWeekDate)<=0) {
                    fourWeekRpZgs = fourWeekRpZgs + gs*reqNum/60;
                    fourWeekRpZje = fourWeekRpZje + je*reqNum;
                } else {
                    continue;
                }
            }

            String[] lpbzje = {String.format("%.2f",oneWeekLpZje/10000), String.format("%.2f",twoWeekLpZje/10000), String.format("%.2f",threeWeekLpZje/10000), String.format("%.2f",fourWeekLpZje/10000)};
            String[] lppjje = {String.format("%.2f",aveLpZje), String.format("%.2f",aveLpZje), String.format("%.2f",aveLpZje), String.format("%.2f",aveLpZje)};
            String[] lpbzgs = {String.format("%.2f",oneWeekLpZgs), String.format("%.2f",twoWeekLpZgs), String.format("%.2f",threeWeekLpZgs), String.format("%.2f",fourWeekLpZgs)};
            String[] lppjgs = {String.format("%.2f",aveLpZgs), String.format("%.2f",aveLpZgs), String.format("%.2f",aveLpZgs), String.format("%.2f",aveLpZgs)};

            String[] rpbzje = {String.format("%.2f",oneWeekRpZje/10000), String.format("%.2f",twoWeekRpZje/10000), String.format("%.2f",threeWeekRpZje/10000), String.format("%.2f",fourWeekRpZje/10000)};
            String[] rppjje = {String.format("%.2f",aveRpZje), String.format("%.2f",aveRpZje), String.format("%.2f",aveRpZje), String.format("%.2f",aveRpZje)};
            String[] rpbzgs = {String.format("%.2f",oneWeekRpZgs), String.format("%.2f",twoWeekRpZgs), String.format("%.2f",threeWeekRpZgs), String.format("%.2f",fourWeekRpZgs)};
            String[] rppjgs = {String.format("%.2f",aveRpZgs), String.format("%.2f",aveRpZgs), String.format("%.2f",aveRpZgs), String.format("%.2f",aveRpZgs)};

            outPutEchrtsOfQZVO.setLpbzgs(lpbzgs);
            outPutEchrtsOfQZVO.setLpbzje(lpbzje);
            outPutEchrtsOfQZVO.setLppjgs(lppjgs);
            outPutEchrtsOfQZVO.setLppjje(lppjje);
            outPutEchrtsOfQZVO.setRpbzgs(rpbzgs);
            outPutEchrtsOfQZVO.setRpbzje(rpbzje);
            outPutEchrtsOfQZVO.setRppjgs(rppjgs);
            outPutEchrtsOfQZVO.setRppjje(rppjje);
            outPutEchrtsOfQZVO.setTotalLpZgs(String.format("%.2f",totalLpZgs));
            outPutEchrtsOfQZVO.setTotalLpZje(String.format("%.2f",totalLpZje/10000));
            outPutEchrtsOfQZVO.setTotalRpZgs(String.format("%.2f",totalRpZgs));
            outPutEchrtsOfQZVO.setTotalRpZje(String.format("%.2f",totalRpZje/10000));

        }
        return outPutEchrtsOfQZVO;
    }

}




