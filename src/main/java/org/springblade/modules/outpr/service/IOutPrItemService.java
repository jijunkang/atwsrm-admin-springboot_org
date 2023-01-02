package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.outpr.dto.OutPrItemDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.vo.OutPrItemVO;
import org.springblade.modules.pr.dto.ItemInfoDTO;
import org.springblade.modules.pr.dto.SubmitPriceReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.entity.U9PrEntity;

import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IOutPrItemService extends BaseService<OutPrItemEntity> {
//    Integer STATUS_INIT      = 10; //  '待询价',
//    Integer STATUS_ENQUIRY   = 20; //  '待接单',
//    Integer STATUS_SUPACCEPT = 30; //  '待审核',
//    Integer STATUS_FLOW      = 40; //  '流标',
//    Integer STATUS_FLOW2ART  = 41; //  '流标转人工',
//    Integer STATUS_CHECK1    = 50; //  '一级审核通过',
//    Integer STATUS_CHECK2    = 51; //  '一级审核已阅',
//    Integer STATUS_ACCORD    = 60; //  '待下单',
//    Integer STATUS_ORDER     = 70; //  '自然关闭',
//    Integer STATUS_CLOSE     = 71; //  '短缺关闭',

    Integer STATUS_INIT        = 10; // 初始 待询价
    Integer STATUS_INQUIRYDATE = 20; // 待接单,询交期中
    Integer STATUS_INQUIRY     = 30; // 询价中
    Integer STATUS_FLOW        = 40; // 流标 待处理
    Integer STATUS_FLOW_SUBMIT = 41; // 待提交（流标）
    Integer STATUS_QUOTED      = 50; // 报完价 待采购选定供应商
    Integer STATUS_WINBID      = 60; // 采购选定供应商/框架协议/数学模型 待审核
    Integer STATUS_ACCORD      = 70; // 审核通过 待下单
    Integer STATUS_ORDER       = 80; // 已下单
    Integer STATUS_CLOSE       = 90; // 已关闭

    String PURCHTYPE_OUT = "out";   //  '工序委外',
    String PURCHTYPE_INN = "inner"; //  '阀内件',

    // 上传文件格式
    String FLOW_SUBMIT = "flowSubmit";
    String WIN_BID ="winBid";

    // 是否需要审核
    String NEED_CHECK = "0";
    String NOT_NEED_CHECK = "1";

    OutPrItemDTO getDtoById(Long id);

    IPage<OutPrItemVO> voPage(Query query, OutPrItemDTO outpritem);

    IPage<OutPrItemVO> inquiryPage(Query query, OutPrItemDTO outpritem);

    IPage<OutPrItemVO> voBidPage(Query query, OutPrItemDTO outpritem);
    IPage<OutPrItemVO> voInquiryBidPage(Query query, OutPrItemDTO outpritem);

    boolean flowPrItemId(Long prItemId, String cause);

    int getCount();
    int getTabCount(String status);

    OutPrItemEntity getByPrcodeAndItemcode(String prCode, String itemCode);

    List<Map<String, Object>> getOutTab();

    boolean submitBatch(SubmitPriceReq SubmitPriceReq);

    boolean flowBatchExcelOfOut(SubmitPriceReq submitPriceReq);

    boolean removeOutPrList(SubmitPriceReq submitPriceReq);

    List<ItemInfoDTO> autoRetrieve(List<OutPrItemDTO> outPrItemDTOS);

    List<Map<String, Object>> inquiryCountOfWW();

    int inquiryCountOfWWForZT();

    void letPrFlow(OutPrItemEntity pr, String flowCause);

    QueryWrapper<OutPrItemEntity> getQueryWrapper(OutPrItemEntity u9_pr);
}
