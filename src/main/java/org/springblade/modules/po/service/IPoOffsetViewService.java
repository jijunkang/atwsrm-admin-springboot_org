package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.StatisticDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.po.dto.PoOffsetViewInfo;
import org.springblade.modules.po.dto.PoOffsetViewReq;
import org.springblade.modules.po.dto.PoTracelogDTO;
import org.springblade.modules.po.entity.PoOffsetViewEntity;
import org.springblade.modules.po.vo.PoOffsetViewVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务类
 * @author Will
 */
public
interface IPoOffsetViewService extends BaseService<PoOffsetViewEntity>{

    String TYPE_OFFSET    = "offset";
    String TYPE_REMAIN_0  = "remain_0";
    String TYPE_REMAIN_1  = "remain_1";
    String TYPE_REMAIN_5  = "remain_5";
    String TYPE_REMAIN_10 = "remain_10";
    String TYPE_REMAIN_20 = "remain_20";

    Map<String,String> FIELDS_MAP = new HashMap<String, String>(){{
        put("proNo","项目号");
        put("poCode","订单号");
        put("poLn","行号");
        put("type","偏移类型");
        put("itemCode","料品号");
        put("itemName","料品描述");
        put("supCode","供应商编码");
        put("supName","供应商名称");
        put("supContact","联系人");
        put("supMobile","电话号码");
        put("priceNum","计量数量");
        put("priceUom","计量单位");
        put("tcNum","交易数量");
        put("tcUom","交易单位");
        put("proNum","项目数量");
        put("offsetDays","偏移量");
        put("reqDate","要求交期");
        put("supConfirmDate","确认交期");
        put("supUpdateDate","修改交期");
        put("operationDate","运算交期");
        put("price","采购单价");
        put("amount","金额");
        put("purchCode","采购员工号");
        put("purchName","采购员工姓名");
        put("traceCode","跟单员编号");
        put("traceName","跟单员名称");
        put("rcvGoodsNum","到货数量");
        put("arvGoodsNum","实收数量");
        put("proGoodsNum","未到货数量");
        put("returnGoodsNum","退货数量");
        put("fillGoodsNum","退货需要补货数量");
        put("remark","备注");
    }};


    QueryWrapper<PoOffsetViewEntity> getQueryWrapper(PoOffsetViewEntity pooffsetview);

    IPage<PoOffsetViewVO> selectTodoPage(Query query, PoOffsetViewVO poOffsetViewVO);

    List<StatisticDTO> getStatistics(String traceCode);

    int toProcessCount();

    /**
     * 提交跟单日志
     * */
    @Transactional
    boolean submitLog(PoTracelogDTO tracelogDTO);

    /**
     * 批量提交跟单日志
     * @param tracelogDTOs
     * @return
     */
    boolean submitLog(List<PoTracelogDTO> tracelogDTOs);

    /**
     * 导出
     * @param poOffsetViewEntity
     * @param query
     * @param response
     */
    void export(PoOffsetViewEntity poOffsetViewEntity, Query query, HttpServletResponse response) throws Exception;

    /**
     * 自定义导出
     * @param poOffsetView
     * @param fields
     * @param response
     */
    void customExport(PoOffsetViewEntity poOffsetView, String[] fields, HttpServletResponse response);

    PoOffsetViewInfo getPoOffsetViewInfo();

    void craftCtrlExport(PoOffsetViewVO poOffsetViewVO,  HttpServletResponse response) throws Exception;

    IPage<PoOffsetViewVO> listMore(Query query, PoOffsetViewReq poOffsetViewReq);
}
