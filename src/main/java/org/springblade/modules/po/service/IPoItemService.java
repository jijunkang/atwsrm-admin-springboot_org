package org.springblade.modules.po.service;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.outpr.entity.OutPrItemArtifactEntity;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.dto.PoItemNodeReq;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.dto.PoUpDateReq;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoItemReqRepotTotal;
import org.springblade.modules.po.vo.PoItemNewReportVO;
import org.springblade.modules.po.vo.PoItemReqRepotVO;
import org.springblade.modules.po.vo.PoItemVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 采购订单明细 服务类
 *
 * @author Will
 */
public
interface IPoItemService extends BaseService<PoItemEntity> {

    /**
     * 待下单
     */
    Integer STATUS_INIT = 10;
    /**
     * 已下单
     */
    Integer STATUS_ORDER = 20;
    /**
     * 已关闭
     */
    Integer STATUS_CLOSE = 30;

    String SOURCE_PRICELIB = "pricelib"; //订单来源-白名单
    String SOURCE_MODEL = "model";    //订单来源-数学模型
    String SOURCE_PROTOCOL = "protocol"; //订单来源-框架协议
    String SOURCE_ENQUIRY = "enquiry";  //订单来源-询价
    String SOURCE_MANUAL = "manual";   //订单来源-无供应商手动处理
    String SOURCE_ESB = "esb";      //订单来源-ESB同步
    String SOURCE_OUT = "out";      //订单来源-委外
    String SOURCE_INNER = "inner";    //订单来源-阀内件
    String SOURCE_ART = "art";      //订单来源-转人工
    String SOURCE_HANG = "hang";      //订单来源-挂起转单

    String BIZ_BRANCH_COMBILL_WW = "combill_ww";  //并单 全程委外
    String BIZ_BRANCH_COMBILL = "combill";    //并单 非全程委外

    QueryWrapper<PoItemEntity> getQueryWrapper(PoItemDTO poItem);

    boolean submitTrace(Long poItemId);

    /**
     * 待下单数量
     *
     * @return
     */
    int waitCount();

    boolean createByOutPreOrder(OutSupPreOrderEntity preOrder, String s);

    boolean createByOutArtifact(OutPrItemArtifactEntity entity, String remark);

    IPage<PoItemVO> getDeliveryPage(IPage<PoItemEntity> page, PoItemDTO poItem);

    int getDeliveryCount();

    boolean submitUpdateDate(PoUpDateReq poUpDateReq) throws Exception;

    /**
     * 导出
     *
     * @param poItem
     */
    void getDeliveryExport(PoItemDTO poItem, HttpServletResponse response);

    /**
     * 采购报表
     *
     * @param query
     * @param po_item
     * @return
     */
    IPage<PoItemVO> reportPage(Query query, PoItemDTO po_item);

    /**
     * VMI监控报表
     *
     * @param query
     * @param po_item
     * @return
     */
    IPage<PoItemVO> vmiReportPage(Query query, PoItemDTO po_item);


    /**
     * 采购报表 导出
     * @param poItem
     * @param response
     */
    void getReportExport(PoItemDTO poItem, HttpServletResponse response);

    /**
     * vmi采购报表 导出
     * @param poItem
     * @param response
     */
    void getVMIReportExport(PoItemDTO poItem, HttpServletResponse response);

    /**
     * 导入修改交期
     *
     * @param file
     * @return
     */
    boolean importUpdateDate(MultipartFile file) throws Exception;

    /**
     * 物料历史最高价
     *
     * @param itemCode
     * @return
     */
    BigDecimal getHighestPrice(String itemCode);

    /**
     * 物料历史最低价
     *
     * @param itemCode
     * @return
     */
    BigDecimal getLowestPrice(String itemCode);

    /**
     * 最近价
     *
     * @param itemCode
     * @return
     */
    PoItemEntity getLastPoInfos(String itemCode,String itemName);

    IPage<PoItemVO> pageWithPr(Query query, PoItemDTO poItemEntity);

    IPage<PoItemVO> pageWithItemPoContract(Query query, PoItemDTO poItemEntity);


    IPage<PoItemReqRepotVO> getReqRepotPage(Query query, PoItemEntity poItemEntity);

    /**
     * 供应计划排程（新）
     *
     * @param query Query
     * @param poItemEntity PoItemEntity
     * @return IPage
     */
    IPage<PoItemNewReportVO> newReportPage(Query query, PoItemEntity poItemEntity);

    /**
     * 供应计划排程2（新）
     *
     * @param query Query
     * @param poItemEntity PoItemEntity
     * @return IPage
     */
    IPage<PoItemNewReportVO> newReportPage2(Query query, PoItemEntity poItemEntity);

    /**
     * 供应计划排程2一键发送邮件
     * @param poCodeAndLnAndSupCodes
     */
    boolean planreqreportsendEmail(List<String> poCodeAndLnAndSupCodes);

    /**
     * 供应计划排程导出
     *
     * @param poItemEntity PoItemEntity
     * @param response     HttpServletResponse
     */
    void poItemExport(PoItemEntity poItemEntity, HttpServletResponse response);

    /**
     * 供应计划排程导出（新）
     *
     * @param poItemEntity PoItemEntity
     * @param response     HttpServletResponse
     */
    void newPoItemExport(PoItemEntity poItemEntity, HttpServletResponse response);

    /**
     * 供应计划排程导出2（新）
     *
     * @param poItemEntity PoItemEntity
     * @param response     HttpServletResponse
     */
    void newPoItemExport2(PoItemEntity poItemEntity, HttpServletResponse response);

    /**
     * 供应计划排程下载
     *
     * @param response HttpServletResponse
     */
    boolean poItemDownload(HttpServletResponse response);

    /**
     * 供应计划排程下载（新）
     *
     * @param response HttpServletResponse
     */
    boolean newReportDownload(HttpServletResponse response);

    /**
     * 统计所有项目号
     *
     * @param orderCode
     * @return
     */
    String connectProNoByPoCode(Set<String> orderCode);

    /**
     * poCodes
     * （订单行交期最早的交期 - 当前日期） <= 15 (交期取修改交期，无修改交期取承诺交期）
     *
     * @param day
     * @return
     */
    List<String> getRemindPoCodes(int day);

    List<PoItemReqRepotVO> getVoList(List<PoItemReqRepotTotal> totalList);

    List<PoItemNewReportVO> getNewVoList(List<PoItemReqRepotTotal> totalList);

    List<PoItemNewReportVO> getExportVoList(List<PoItemReqRepotTotal> totalList);

    List<PoItemReqRepotTotal> getTotalList(PoItemEntity poItemEntity);

    List<PoItemReqRepotTotal> getNewTotalList(PoItemEntity poItemEntity);

    /**
     * poItemExport
     */
    void putPoItemTask();

    /**
     * poItemExport
     */
    void newPutPoItemTask();

    /**
     * 获取所有列
     *
     * @param dateTitle List
     * @return List
     */
    List<ExcelExportEntity> getAllEntity(List<PoItemReqRepotCurrMonthDTO> dateTitle);

    List<ExcelExportEntity> getNewAllEntity(List<PoItemReqRepotCurrMonthDTO> dateTitle);

    /**
     * 获取所有值
     *
     * @param voList    List
     * @param dateTitle List
     * @param entity    List
     * @return List
     */
    List<Map<String, Object>> getValueList(List<PoItemReqRepotVO> voList, List<PoItemReqRepotCurrMonthDTO> dateTitle, List<ExcelExportEntity> entity);

    List<Map<String, Object>> getNewValueList(List<PoItemNewReportVO> voList, List<PoItemReqRepotCurrMonthDTO> dateTitle, List<ExcelExportEntity> entity);

    IPage<PoItemVO> getCraftCtrlPage(IPage<PoItemEntity> page, PoItemDTO poItem);

    boolean savesCraftCtrl(List<PoItemNodeReq> poItemNodeReqs);

    boolean updateCraftCtrl(PoItemNodeReq poItemNodeReq);

    void craftCtrlExport(PoItemDTO poItem, HttpServletResponse response) throws Exception;

    List<PoItemEntity> getPoItemEntity(PoItemDTO poItem);

    boolean updateReqDateBatch(List<PoItemDTO> poItemDTOS);

    boolean updatePromiseDateBatch(List<PoItemDTO> poItemDTOS);
    boolean updatePromiseDateBatchToU9(List<PoItemDTO> poItemDTOS);

    boolean batchUpdateIsUrgent(List<PoItemDTO> poItemDTOS);

    boolean updateDelDateBatch(List<PoItemEntity> poItemEntityList);

    PoItemEntity getByPoCodeAndPoLn(String poCode, Integer poLn);

    boolean submitPrice(List<PoItemEntity> poItemEntityList);

    boolean sendEmail(String ids);

    boolean batchSetUpdateCheckDate(String ids);

    IPage<PoItemVO> getHistoryPrice(IPage<PoItemVO> page, String itemCode);

    boolean updatePromiseDateBatchFromShjh(List<PoItemDTO> poItemDTOS);

    void updatePoStatus();

}
