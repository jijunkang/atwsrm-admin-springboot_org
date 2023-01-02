package org.springblade.modules.pr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.ss.formula.functions.T;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.vo.*;
import org.springblade.modules.pricelib.entity.PriceLibEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 请购单 服务类
 * @author Will
 */
public
interface IU9PrService extends BaseService<U9PrEntity>{

    Integer STATUS_INIT        = 10; //初始 待询价
    Integer STATUS_HANG        = 20; //挂起
    Integer STATUS_INQUIRY     = 30; //询价中
    Integer STATUS_INQUIRYDATE = 31; //询交期
    Integer STATUS_FLOW        = 40; //流标
    Integer STATUS_FLOW_SUBMIT = 41; //待提交（流标）
    Integer STATUS_FLOW_NOSUP  = 42; //流标 （无供应商）
    Integer STATUS_QUOTED      = 50; //报完价 待采购选定供应商
    Integer STATUS_WINBID      = 60; // 采购选定供应商/框架协议/数学模型 待审核
    Integer STATUS_WAIT        = 70; // 审核通过 待下单
    Integer STATUS_ORDER       = 80; //已下单
    Integer STATUS_CLOSE       = 90; //已关闭

    String INQUIRYWAY_ASSIGN         = "assign";          //  指定供应商 三期废弃
    String INQUIRYWAY_EXCLUSIVE      = "exclusive";       //  独家采购
    String INQUIRYWAY_HAVEPRICE      = "have_price";      //  有价格
    String INQUIRYWAY_HAVEPRICE1DATE = "have_price1date"; //  有价格 req_date < std_date
    String INQUIRYWAY_HAVEMODEL      = "have_model";      //  有数学模型
    String INQUIRYWAY_HAVEPROTOCOL   = "have_protocol";   //  有框架协议
    String INQUIRYWAY_COMPETE        = "compete";         //  供应商询价
    String INQUIRYWAY_NOSUP          = "no_sup";          //  无供应商

    // 采购类型
    String PURCHASE_TYPE_NORMAL      = "normal";          //  无供应商
    String PURCHASE_TYPE_INNER       = "inner";          //  无供应商

    // 流标类型
    String FLOW_TYPE_NOSUP           = "no_sup";
    String FLOW_TYPE_NOQUOTE         = "no_quote";
    String FLOW_TYPE_SUPREFUSE       = "sup_refuse";
    String FLOW_TYPE_PR3             = "pr3";
    String FLOW_TYPE_PRICEATTRERR    = "priceattr_err";
    String FLOW_TYPE_ATWREJECT       = "atwreject";   //评标审核拒绝
    String FLOW_TYPE_INQDATE_REFUSE  = "inqdate_refuse";   //询交期转流标 add 2020.05.09
    String FLOW_TYPE_INQPRICE_REFUSE  = "inqprice_refuse";  //询价转流标 add 2020.05.09
    String FLOW_TYPE_POCANCEL        = "po_cancel";        //PO单取消 add 2020.05.09

    // 上传文件格式
    String FLOW_SUBMIT = "flowSubmit";
    String WIN_BID ="winBid";


    // 业务类型,1:pr1-326-全程委外，2：pr2-326 0:标准采购
    String WW = "1";

    // 是否需要审核
    String NEED_CHECK = "0";
    String NOT_NEED_CHECK = "1";

    // 锻件类别
    String DJ_ZFL = "150204";
    String DJ_FM = "150202";

    /**
     * 流标的 采购员录入价格
     * @return
     */
    boolean submitPrice(SubmitPriceDTO item);

    /**
     * @param page
     * @param prReq
     * @return
     */
    IPage<U9PrDTO> selectPage(IPage<U9PrDTO> page, PrReq prReq);


    IPage<U9PrDTO> selectAllPrPage(IPage<U9PrDTO> page, PrReq prReq);

    U9PrDTO getDtoById(Long id);

    /**
     * 待处理询价单数量
     * @return
     */
    int toProcessCount();

    /**
     * 待处理询价单数量_小零件
     * @return
     */
    int toProcessCountOfOthers();


    /**
     * 流标数量
     * @return
     */
    int flowCount();

    /**
     * 流标数量_小零件
     * @return
     */
    int flowCountOfOthers();


    void letPrFlow(U9PrEntity pr, String flowType);

    boolean statusToFlow(Long prid);

    boolean evaluateBidOfOthers(Long prid);

    boolean setBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq);

    boolean setBasicItemInfoOfXLJ(SubmitPriceReq submitPriceReq);

    boolean setBasicItemInfoOfXLJRX(SubmitPriceReq submitPriceReq);

    boolean setBasicItemInfoOfDZ(SubmitPriceReq submitPriceReq);

    boolean addBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq);

    boolean addBasicItemInfoOfXLJ(SubmitPriceReq submitPriceReq);

    QueryWrapper<U9PrEntity> getQueryWrapper(U9PrEntity u9_pr);

    boolean updateBatch(List<U9PrEntity> prList);

    /**
     * 导出
     * @param prReq
     * @param response
     */
    void export(PrReq prReq, HttpServletResponse response);

    IPage<Map<String, Object>> getPriceLib(IPage<PriceLibEntity> page, U9PrEntity u9pr);

    boolean createByHang(U9PrHangDTO u9PrHangDTO);

    IPage<U9PrEntity> getU9Page(IPage<U9PrEntity> page);

    int getPriceFrameCount();

    /**
     * 批量录入价格
     *
     * @param
     * @return boolean
     */
    boolean submitBatch(SubmitPriceReq SubmitPriceReq);

    /**
     * 询价单中台统计
     *
     * @return List
     */
    List<Map<String, Object>> getInquiryCount(String type);

    /**
     * 流标中台统计
     *
     * @return List
     */
    List<Map<String, Object>> getFlowCount(String type);

    /**
     * getCheckPage
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> getCheckPage(IPage<U9PrDTO> page, PrReq prReq);

    /**
     * getPriceList
     *
     * @param u9PrVO u9PrVO
     * @return List
     */
    List<PriceVO> getPriceList(U9PrVO u9PrVO);

    /**
     * inquiryExport
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    void inquiryExport(PrReq prReq, HttpServletResponse response);

    /**
     * inquiryExportOfOthers
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    void inquiryExportOfOthers(PrReq prReq, HttpServletResponse response);

    /**
     * flowExport
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    void flowExport(PrReq prReq, HttpServletResponse response);

    /**
     * flowExportOfOthers
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    void flowExportOfOthers(PrReq prReq, HttpServletResponse response);

    /**
     * flowBatchExcel
     *
     * @param
     * @return boolean
     */
    boolean flowBatchExcel(SubmitPriceReq submitPriceReq);


    boolean flowBatchExcelOfSupItem(SubmitPriceReq submitPriceReq);
    /**
     * flowBatchAudit
     *
     * @param
     * @return boolean
     */
    boolean flowBatchAudit(SubmitPriceReq submitPriceReq);

    /**
     * flowBatchAudit
     *
     * @param ioEntity IoEntity
     * @return boolean
     */
    boolean flowAudit(IoEntity ioEntity);

    /**
     * inquiryPage
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> inquiryPage(IPage<U9PrDTO> page, PrReq prReq);

    /**
     * autoRetrieve
     *
     * @param u9PrDTOS
     * @return
     */
    List<ItemInfoDTO> autoRetrieve(List<U9PrDTO> u9PrDTOS);

    /**
     * removePrList
     *
     * @param submitPriceReq
     * @return
     */
    boolean removePrList(SubmitPriceReq submitPriceReq);

    boolean deleteDJInfo(SubmitPriceReq submitPriceReq);

    boolean deleteXLJInfo(SubmitPriceReq submitPriceReq);


    /**
     * inquiryCheckPage
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> inquiryCheckPage(IPage<U9PrDTO> page, PrReq prReq);

    /**
     * flowPage
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> flowPage(IPage<U9PrDTO> page, PrReq prReq);

    /**
     * flowPageOfOthers
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> flowPageOfOthers(IPage<U9PrDTO> page, PrReq prReq);

    /**
     * flowCheckPage
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> flowCheckPage(IPage<U9PrDTO> page, PrReq prReq);


    /**
     * flowNoSupPage
     *
     * @param page IPage
     * @param prReq PrReq
     * @return IPage
     */
    IPage<U9PrDTO> flowNoSupPage(IPage<U9PrDTO> page, PrReq prReq);


    List<ItemInfoOfZDJVO> getItemInfoOfZDJVO(String itemCode, String itemName);

    List<ItemInfoOfZDJVO> getItemInfoOfDJVO(String itemCode, String itemName);

    IPage<ItemInfoEntityDJReport> getItemInfoOfDJVOReport(IPage<ItemInfoEntityDJReport> page, SubmitPriceReq submitPriceReq);

    IPage<ItemInfoEntityOfXLJ> getItemInfoOfXLJVOReport(IPage<ItemInfoEntityOfXLJ> page, SubmitPriceReq submitPriceReq);

    IPage<ItemInfoEntityOfLZQ> getItemInfoOfLZQVOReport(IPage<ItemInfoEntityOfLZQ> page, SubmitPriceReq submitPriceReq);

    IPage<ItemInfoEntityOfQZNew> getItemInfoOfQZReport(IPage<ItemInfoEntityOfQZNew> page, SubmitPriceReq submitPriceReq);
    IPage<ItemInfoEntityOfDZ> getItemInfoOfDZVOReport(IPage<ItemInfoEntityOfDZ> page, SubmitPriceReq submitPriceReq);

    IPage<ItemInfoEntityOfFL> getItemInfoOfFLVOReport(IPage<ItemInfoEntityOfFL> page, SubmitPriceReq submitPriceReq);

    List<ItemInfoOfZDJVO> getItemInfoOfWWVO(String itemCode, String itemName);

    List<ItemInfoEntityOfXLJ> getItemInfoOfXLJVO(String itemCode, String itemName);

    List<ItemInfoEntityOfDZ> getItemInfoOfDZVO(String itemCode, String itemName);

    List<ItemInfoEntityOfFL> getItemInfoOfFLVO(String itemCode, String itemName);

    List<ItemInfoEntityOfLZQ> getItemInfoOfLZQVO(String itemCode, String itemName);

    List<ItemInfoOfQZVO> getItemInfoOfQZVO(String itemCode, String itemName);

    List<ItemInfoEntityOfQZNew> getItemInfoOfQZNew(SubmitPriceReq submitPriceReq);

    <T> T getBasicItemInfoOfDJ(String itemCode, String itemName);

    <T> T getBasicItemInfoOfXLJ(String itemCode, String itemName);

    <T> T getBasicItemInfoOfXLJRX(String itemCode, String itemName);

    <T> T getBasicItemInfoOfDZ(String itemCode, String itemName);



    boolean saveBatchOfOthers(SubmitPriceReq SubmitPriceReq);

    boolean saveBatchOfOthersForDialog(SubmitPriceReq SubmitPriceReq);

    /**
     * removePrListOfOthers
     *
     * @param submitPriceReq
     * @return
     */
    boolean removePrListOfOthers(SubmitPriceReq submitPriceReq);

    boolean winTheBid(SubmitPriceReq submitPriceReq);

    boolean cancelTheBid(SubmitPriceReq submitPriceReq);

    boolean moveToOthersOfNoSup(SubmitPriceReq submitPriceReq);

    boolean addOtherInfos(SupItemOthers supItemOthers);

    boolean updateOtherInfos(SupItemOthers supItemOthers);

    boolean removeOtherInfos(SupItemOthers supItemOthers);

    IPage<SupItemOthers> getOthersInfo(IPage<SupItemOthers> page, SubmitPriceReq submitPriceReq);

    boolean sendAndInquiry(SubmitPriceReq submitPriceReq);

    void exportAllItemInfo(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    void exportAllItemInfoXLJ(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    void exportAllItemInfoLZQ(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    void exportAllItemInfoQZNew(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    void exportAllItemInfoDZ(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    void exportAllItemInfoFL(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    List<MaterialMaliyVO> getitemInfoGBL(String itemCode, String itemName);

    IPage<AutoOrderOfDJ> autoOrderOfDJ(IPage<AutoOrderOfDJ> page, SubmitPriceReq submitPriceReq);

    IPage<AutoOrderOfXLJ> autoOrderOfXLJ(IPage<AutoOrderOfXLJ> page, AutoOrderOfXLJ autoOrderOfXLJ);

    void exportAutoOrderOfDJ(SubmitPriceReq submitPriceReq, HttpServletResponse response);

    void exportAutoOrderOfXLJ(AutoOrderOfXLJ autoOrderOfXLJ, HttpServletResponse response);

    List<U9PrFromPhpDTO> handleExceptData(List<U9PrFromPhpDTO> u9list);

    List<U9PrFromPhpDTO> handleExceptData2(List<U9PrFromPhpDTO> u9list);

    String handleExceptDataWithNoProject(List<U9PrFromPhpDTO> u9list);

    void interceptPR(U9PrFromPhpDTO u9fromphp,List<U9PrFromPhpDTO> repU9List);

    void SendPR(U9PrFromPhpDTO u9fromphp);

    void releaseNoProPR();

    void releaseNoProPR2();

    Boolean releaseNoProPRByPrLn( List<U9PrEntityNoPro> u9PrEntityNoPros);
}

