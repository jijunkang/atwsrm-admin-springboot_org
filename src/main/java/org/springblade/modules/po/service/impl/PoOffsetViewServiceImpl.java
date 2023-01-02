package org.springblade.modules.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.springblade.common.dto.StatisticDTO;
import org.springblade.common.utils.ExcelHeader;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.po.dto.*;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.entity.PoOffsetViewEntity;
import org.springblade.modules.po.entity.PoTracelogEntity;
import org.springblade.modules.po.mapper.PoOffsetViewMapper;
import org.springblade.modules.po.service.ICraftCtrlNodeService;
import org.springblade.modules.po.service.IPoItemCraftCtrlNodeService;
import org.springblade.modules.po.service.IPoOffsetViewService;
import org.springblade.modules.po.service.IPoTracelogService;
import org.springblade.modules.po.vo.PoItemCraftCtrlNodeVO;
import org.springblade.modules.po.vo.PoOffsetViewExcel;
import org.springblade.modules.po.vo.PoOffsetViewVO;
import org.springblade.modules.po.wrapper.PoOffsetViewWrapper;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class PoOffsetViewServiceImpl extends BaseServiceImpl<PoOffsetViewMapper, PoOffsetViewEntity> implements IPoOffsetViewService{


    @Autowired
    IPoTracelogService poTracelogService;

    @Autowired
    IParamService paramService;

    @Autowired
    IItemService itemService;

    @Autowired
    IPoItemCraftCtrlNodeService poItemCraftCtrlNodeService;

    @Autowired
    ICraftCtrlNodeService craftCtrlNodeService;

    @Override
    public
    QueryWrapper<PoOffsetViewEntity> getQueryWrapper(PoOffsetViewEntity pooffsetview){
        QueryWrapper<PoOffsetViewEntity> queryWrapper = Wrappers.<PoOffsetViewEntity>query()
                .like(StringUtil.isNotBlank(pooffsetview.getPoCode()), "po_code", pooffsetview.getPoCode())
                .like(StringUtil.isNotBlank(pooffsetview.getProNo()), "pro_no", pooffsetview.getProNo())
                .like(StringUtil.isNotBlank(pooffsetview.getItemCode()), "item_code", pooffsetview.getItemCode())
                .like(StringUtil.isNotBlank(pooffsetview.getSupCode()), "sup_code", pooffsetview.getSupCode())
                .like(StringUtil.isNotBlank(pooffsetview.getItemName()), "item_name", pooffsetview.getItemName())
                .like(StringUtil.isNotBlank(pooffsetview.getSupName()), "sup_name", pooffsetview.getSupName())
                .eq(StringUtil.isNotBlank(pooffsetview.getType()), "type", pooffsetview.getType())
                .eq(StringUtil.isNotBlank(pooffsetview.getTraceCode()), "trace_code", pooffsetview.getTraceCode());
        return queryWrapper;
    }

    @Override
    public
    IPage<PoOffsetViewVO> selectTodoPage(Query query, PoOffsetViewVO poOffsetViewVO){
        IPage<PoOffsetViewEntity> entityPage = this.baseMapper.getPoOffetViewPage(Condition.getPage(query), poOffsetViewVO);
        return getPoOffsetViewVOIPage(entityPage);
    }

    @Override
    public
    List<StatisticDTO> getStatistics(String traceCode){

        ArrayMap<String, String> typeMap = new ArrayMap<String, String>(){{
            put(TYPE_OFFSET, "交期偏移");
            put(TYPE_REMAIN_0, "当日未交货");
            put(TYPE_REMAIN_1, "1天到货日");
            put(TYPE_REMAIN_5, "5天到货日");
            put(TYPE_REMAIN_10, "10天到货日");
            put(TYPE_REMAIN_20, "20天到货日");
        }};
        List<StatisticDTO> retList = Lists.newArrayList();
        for(String type : typeMap.keySet()){
            StatisticDTO dto = new StatisticDTO();
            retList.add(dto);
            dto.setKey(type);
            dto.setTitle(typeMap.get(type));
            QueryWrapper<PoOffsetViewEntity> wrap = Condition.getQueryWrapper(new PoOffsetViewEntity()).eq("type", type)
                                                             .eq("trace_code", traceCode);
            dto.setCount(count(wrap));
        }
        return retList;
    }

    @Override
    public
    int toProcessCount(){
        String traceCode = AuthUtil.getUser().getAccount();
        return count(Condition.getQueryWrapper(new PoOffsetViewEntity()).eq("trace_code", traceCode));
    }

    @Override
    public
    boolean submitLog(PoTracelogDTO tracelogDTO){

        PoOffsetViewEntity poOffset = getById(tracelogDTO.getPoOffsetId());

        PoTracelogEntity pte = new PoTracelogDTO();
        pte.setPoCode(poOffset.getPoCode());
        pte.setPoLn(poOffset.getPoLn());
        pte.setContent(tracelogDTO.getRemark());
        pte.setPurchCode(poOffset.getPurchCode());
        pte.setPurchName(poOffset.getPurchName());
        pte.setTraceCode(poOffset.getTraceCode());
        pte.setTraceName(poOffset.getTraceName());
        poTracelogService.save(pte);

        return removeById(poOffset.getId());
    }

    @Override
    public
    boolean submitLog(List<PoTracelogDTO> tracelogDTOs){
        if(tracelogDTOs == null || tracelogDTOs.isEmpty()){
            return false;
        }
        for(PoTracelogDTO dto : tracelogDTOs){
            if(!submitLog(dto)){
                return  false;
            }
        }
        return true;
    }

    /**
     * 导出
     */
    @Override
    public
    void export(PoOffsetViewEntity poOffsetView, Query query, HttpServletResponse response) throws Exception{
        String traceCode = AuthUtil.getUser().getAccount();
        poOffsetView.setTraceCode(traceCode);
        QueryWrapper<PoOffsetViewEntity> queryWrapper = getQueryWrapper(poOffsetView);

        List<PoOffsetViewEntity> entityList = list(queryWrapper);
        if(entityList == null || entityList.isEmpty()){
            throw new Exception("暂无数据");
        }

        List<PoOffsetViewExcel> excelList = Lists.newArrayList();
        for(PoOffsetViewEntity entity : entityList){
            PoOffsetViewExcel dto = BeanUtil.copy(entity, PoOffsetViewExcel.class);
            excelList.add(dto);
        }

        ExcelUtils.defaultExport(excelList, PoOffsetViewExcel.class, "交期跟踪" + DateUtil.formatDate(new Date()), response);


    }

    /**
     * 自定义导出
     */
    @Override
    public
    void customExport(PoOffsetViewEntity poOffsetView, String[] fields, HttpServletResponse response){
        String traceCode = AuthUtil.getUser().getAccount();
        poOffsetView.setTraceCode(traceCode);
        QueryWrapper<PoOffsetViewEntity> queryWrapper = getQueryWrapper(poOffsetView);

        List<PoOffsetViewEntity> entityList = list(queryWrapper);
        if(entityList == null || entityList.isEmpty()){
            throw new RuntimeException("暂无数据");
        }

        List<PoOffsetViewExcel> excelList = Lists.newArrayList();
        for(PoOffsetViewEntity entity : entityList){
            PoOffsetViewExcel dto = BeanUtil.copy(entity, PoOffsetViewExcel.class);
            excelList.add(dto);
        }
        List<ExcelHeader> headers = Lists.newArrayList();
        for (String field : fields) {
            if ("reqDate".equals(field)) {
                headers.add(new ExcelHeader("reqDateFmt", FIELDS_MAP.get(field)));
            } else if ("supConfirmDate".equals(field)) {
                headers.add(new ExcelHeader("supConfirmDateFmt", FIELDS_MAP.get(field)));
            } else if ("supUpdateDate".equals(field)) {
                headers.add(new ExcelHeader("supUpdateDateFmt", FIELDS_MAP.get(field)));
            } else if ("operationDate".equals(field)) {
                headers.add(new ExcelHeader("operationDateFmt", FIELDS_MAP.get(field)));
            } else {
                headers.add(new ExcelHeader(field, FIELDS_MAP.get(field)));
            }
        }
        ExcelUtils.exportExcel2007(excelList, "交期跟踪" + DateUtil.formatDate(new Date()), headers, response);

    }

    @Override
    public
    PoOffsetViewInfo getPoOffsetViewInfo(){
        PoOffsetViewInfo info = new PoOffsetViewInfo();
        info.setLastSyncTime(DateUtil.parse(paramService.getValue("pooffsetview.lastsynctime"), DateUtil.PATTERN_DATETIME));
        return info;
    }

    @Override
    public void craftCtrlExport(PoOffsetViewVO poOffsetViewVO,  HttpServletResponse response) throws Exception {
        String traceCode = AuthUtil.getUser().getAccount();
        poOffsetViewVO.setTraceCode(traceCode);
        List<PoOffsetViewEntity> entityList = this.baseMapper.getPoOffetViewPage(poOffsetViewVO);
        if(entityList == null || entityList.isEmpty()){
            throw new RuntimeException("暂无数据");
        }
        List<PoOffsetViewDTO> excelList = Lists.newArrayList();
        for(PoOffsetViewEntity entity : entityList){
            PoOffsetViewDTO dto = BeanUtil.copy(entity, PoOffsetViewDTO.class);
            dto.setPoItemNodeList(poItemCraftCtrlNodeService.getDTOS(dto.getPiId()));
            excelList.add(dto);
        }
        ExcelUtils.defaultExport(excelList, PoOffsetViewDTO.class, "工艺卡控" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public IPage<PoOffsetViewVO> listMore(Query query, PoOffsetViewReq poOffsetViewReq) {
        IPage<PoOffsetViewEntity> entityPage = Condition.getPage(query);
        if ("and".equals(poOffsetViewReq.getSelectType())) {
            entityPage = page(Condition.getPage(query), getAllQueryWrapper(poOffsetViewReq));
        } else if ("or".equals(poOffsetViewReq.getSelectType())) {
            entityPage = page(Condition.getPage(query), getOrQueryWrapper(poOffsetViewReq));
        }
        return getPoOffsetViewVOIPage(entityPage);
    }

    /**
     * getPoOffsetViewVOIPage
     *
     * @param entityPage IPage
     * @return IPage
     */
    private IPage<PoOffsetViewVO> getPoOffsetViewVOIPage(IPage<PoOffsetViewEntity> entityPage) {
        IPage<PoOffsetViewVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<PoOffsetViewVO> voList = Lists.newArrayList();
        for (PoOffsetViewEntity entity : entityPage.getRecords()) {
            PoOffsetViewVO vo = PoOffsetViewWrapper.build().entityVO(entity);
            //获取工艺卡控进度
            getCraftCtrl(vo);
            voList.add(vo);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * getMoreQueryWrapper
     *
     * @param poOffsetViewReq PoOffsetViewReq
     * @return QueryWrapper
     */
    private QueryWrapper<PoOffsetViewEntity> getAllQueryWrapper(PoOffsetViewReq poOffsetViewReq) {
        QueryWrapper<PoOffsetViewEntity> queryWrapper = Condition.getQueryWrapper(new PoOffsetViewEntity());
        queryWrapper.eq("trace_code", poOffsetViewReq.getTraceCode());
        queryWrapper.eq(StringUtil.isNotBlank(poOffsetViewReq.getType()), "type", poOffsetViewReq.getType());
        //项目编号
        getStringValue(queryWrapper, poOffsetViewReq.getProNoType(), poOffsetViewReq.getProNo(), "pro_no");
        //订单编号
        getStringValue(queryWrapper, poOffsetViewReq.getPoCodeType(), poOffsetViewReq.getPoCode(), "po_code");
        //物料编号
        getStringValue(queryWrapper, poOffsetViewReq.getItemCodeType(), poOffsetViewReq.getItemCode(), "item_code");
        //物料描述
        getStringValue(queryWrapper, poOffsetViewReq.getItemNameType(), poOffsetViewReq.getItemName(), "item_name");

        //行号
        getLongValue(queryWrapper, poOffsetViewReq.getPoLnType(), null, null,
            null, null, poOffsetViewReq.getPoLnMax(), poOffsetViewReq.getPoLnMin(), "po_ln");
        //数量
        getLongValue(queryWrapper, poOffsetViewReq.getPriceNumType(), null, null,
            poOffsetViewReq.getPriceNumMax(), poOffsetViewReq.getPriceNumMin(), null, null, "price_num");
        //要求交期
        getLongValue(queryWrapper, poOffsetViewReq.getReqDateType(), poOffsetViewReq.getReqDateMax(), poOffsetViewReq.getReqDateMin(),
            null, null, null, null, "req_date");
        //确认交期
        getLongValue(queryWrapper, poOffsetViewReq.getSupConfirmDateType(), poOffsetViewReq.getSupConfirmDateMax(), poOffsetViewReq.getSupConfirmDateMin(),
            null, null, null, null, "sup_confirm_date");
        //修改交期
        getLongValue(queryWrapper, poOffsetViewReq.getSupUpdateDateType(), poOffsetViewReq.getSupUpdateDateMax(), poOffsetViewReq.getSupUpdateDateMin(),
            null, null, null, null, "sup_update_date");
        //运算交期
        getLongValue(queryWrapper, poOffsetViewReq.getOperationDateType(), poOffsetViewReq.getOperationDateMax(), poOffsetViewReq.getOperationDateMin(),
            null, null, null, null, "operation_date");

        //备注
        getStringValue(queryWrapper, poOffsetViewReq.getRemarkType(), poOffsetViewReq.getRemark(), "remark");
        //供应商名称
        getStringValue(queryWrapper, poOffsetViewReq.getSupNameType(), poOffsetViewReq.getSupName(), "sup_name");
        //联系人
        getStringValue(queryWrapper, poOffsetViewReq.getSupContactType(), poOffsetViewReq.getSupContact(), "sup_contact");
        //联系方式
        getStringValue(queryWrapper, poOffsetViewReq.getSupMobileType(), poOffsetViewReq.getSupMobile(), "sup_mobile");
        return queryWrapper;
    }


    /**
     * getOrQueryWrapper
     *
     * @param poOffsetViewReq PoOffsetViewReq
     * @return QueryWrapper
     */
    private QueryWrapper<PoOffsetViewEntity> getOrQueryWrapper(PoOffsetViewReq poOffsetViewReq) {
        QueryWrapper<PoOffsetViewEntity> queryWrapper = Condition.getQueryWrapper(new PoOffsetViewEntity());
        queryWrapper.and(wrapper -> {
            getStringValue(wrapper, poOffsetViewReq.getProNoType(), poOffsetViewReq.getProNo(), "pro_no");
            wrapper.or();
            if (StringUtil.isEmpty(poOffsetViewReq.getProNo())) {
                getStringValue(wrapper, poOffsetViewReq.getProNoType(), "/", "pro_no");
                wrapper.or();
            }
            getStringValue(wrapper, poOffsetViewReq.getPoCodeType(), poOffsetViewReq.getPoCode(), "po_code");
            wrapper.or();
            getStringValue(wrapper, poOffsetViewReq.getItemCodeType(), poOffsetViewReq.getItemCode(), "item_code");
            wrapper.or();
            getStringValue(wrapper, poOffsetViewReq.getItemNameType(), poOffsetViewReq.getItemName(), "item_name");
            wrapper.or();

            getLongValue(wrapper, poOffsetViewReq.getPoLnType(), null, null,
                null, null, poOffsetViewReq.getPoLnMax(), poOffsetViewReq.getPoLnMin(), "po_ln");
            wrapper.or();
            getLongValue(wrapper, poOffsetViewReq.getPriceNumType(), null, null,
                poOffsetViewReq.getPriceNumMax(), poOffsetViewReq.getPriceNumMin(), null, null, "price_num");
            wrapper.or();
            getLongValue(wrapper, poOffsetViewReq.getReqDateType(), poOffsetViewReq.getReqDateMax(), poOffsetViewReq.getReqDateMin(),
                null, null, null, null, "req_date");
            wrapper.or();
            getLongValue(wrapper, poOffsetViewReq.getSupConfirmDateType(), poOffsetViewReq.getSupConfirmDateMax(), poOffsetViewReq.getSupConfirmDateMin(),
                null, null, null, null, "sup_confirm_date");
            wrapper.or();
            getLongValue(wrapper, poOffsetViewReq.getSupUpdateDateType(), poOffsetViewReq.getSupUpdateDateMax(), poOffsetViewReq.getSupUpdateDateMin(),
                null, null, null, null, "sup_update_date");
            wrapper.or();
            getLongValue(wrapper, poOffsetViewReq.getOperationDateType(), poOffsetViewReq.getOperationDateMax(), poOffsetViewReq.getOperationDateMin(),
                null, null, null, null, "operation_date");
            wrapper.or();

            getStringValue(wrapper, poOffsetViewReq.getRemarkType(), poOffsetViewReq.getRemark(), "remark");
            wrapper.or();
            getStringValue(wrapper, poOffsetViewReq.getSupNameType(), poOffsetViewReq.getSupName(), "sup_name");
            wrapper.or();
            getStringValue(wrapper, poOffsetViewReq.getSupContactType(), poOffsetViewReq.getSupContact(), "sup_contact");
            wrapper.or();
            getStringValue(wrapper, poOffsetViewReq.getSupMobileType(), poOffsetViewReq.getSupMobile(), "sup_mobile");

            if (StringUtil.isEmpty(poOffsetViewReq.getProNo()) && StringUtil.isEmpty(poOffsetViewReq.getPoCode()) && StringUtil.isEmpty(poOffsetViewReq.getPoLnMax()) && StringUtil.isEmpty(poOffsetViewReq.getPoLnMin()) &&
                StringUtil.isEmpty(poOffsetViewReq.getItemCode()) && StringUtil.isEmpty(poOffsetViewReq.getItemName()) && StringUtil.isEmpty(poOffsetViewReq.getPriceNumMax()) &&
                StringUtil.isEmpty(poOffsetViewReq.getPriceNumMin()) && StringUtil.isEmpty(poOffsetViewReq.getReqDateMax()) && StringUtil.isEmpty(poOffsetViewReq.getReqDateMin()) &&
                StringUtil.isEmpty(poOffsetViewReq.getSupConfirmDateMax()) && StringUtil.isEmpty(poOffsetViewReq.getSupConfirmDateMin()) && StringUtil.isEmpty(poOffsetViewReq.getSupUpdateDateMax()) &&
                StringUtil.isEmpty(poOffsetViewReq.getSupUpdateDateMin()) && StringUtil.isEmpty(poOffsetViewReq.getOperationDateMax()) && StringUtil.isEmpty(poOffsetViewReq.getOperationDateMin()) &&
                StringUtil.isEmpty(poOffsetViewReq.getRemark()) && StringUtil.isEmpty(poOffsetViewReq.getSupName()) && StringUtil.isEmpty(poOffsetViewReq.getSupContact()) &&
                StringUtil.isEmpty(poOffsetViewReq.getSupMobile())) {
                wrapper.eq("1", 1);
            }
        });
        queryWrapper.eq("trace_code", poOffsetViewReq.getTraceCode());
        queryWrapper.eq(StringUtil.isNotBlank(poOffsetViewReq.getType()), "type", poOffsetViewReq.getType());
        return queryWrapper;
    }

    /**
     * 字符串列
     *
     * @param queryWrapper QueryWrapper
     * @param type         String
     * @param stringValue  String
     * @param column       String
     */
    private void getStringValue(QueryWrapper<PoOffsetViewEntity> queryWrapper, String type, String stringValue, String column) {
        switch (StringUtil.isNotBlank(type) ? type : "") {
            case "like":
                queryWrapper.like(StringUtil.isNotBlank(stringValue), column, stringValue);
                break;
            case "notLike":
                queryWrapper.notLike(StringUtil.isNotBlank(stringValue), column, stringValue);
                break;
            case "isNull":
                queryWrapper.and(wra -> {
                    wra.isNull(column);
                    wra.or();
                    if ("pro_no".equals(column)) {
                        wra.eq(column, "/");
                        wra.or();
                    }
                    wra.eq(column, "");
                });
                break;
            case "notNull":
                queryWrapper.isNotNull(column);
                break;
            default:
                break;
        }
    }

    /**
     * 数值和日期列
     *
     * @param queryWrapper QueryWrapper
     * @param type         String
     * @param longMax      long
     * @param longMin      long
     * @param bigMax       BigDecimal
     * @param bigMin       BigDecimal
     * @param column       String
     */
    private void getLongValue(QueryWrapper<PoOffsetViewEntity> queryWrapper, String type, Long longMax, Long longMin,
                              BigDecimal bigMax, BigDecimal bigMin, Integer intMax, Integer intMin, String column) {
        switch (StringUtil.isNotBlank(type) ? type : "") {
            case "between":
                //Long
                if (longMin != null) {
                    queryWrapper.ge(column, longMin / 1000);
                }
                if (longMax != null) {
                    queryWrapper.le(column, longMax / 1000);
                }
                //BigDecimal
                queryWrapper.ge(bigMin != null, column, bigMin);
                queryWrapper.le(bigMax != null, column, bigMax);
                //Integer
                queryWrapper.ge(intMin != null, column, intMin);
                queryWrapper.le(intMax != null, column, intMax);
                break;
            case "==":
                if (longMin != null) {
                    queryWrapper.eq(column, longMin / 1000);
                }
                queryWrapper.eq(bigMin != null, column, bigMin);
                queryWrapper.eq(intMin != null, column, intMin);
                break;
            case "!=":
                if (longMin != null) {
                    queryWrapper.ne(column, longMin / 1000);
                }
                queryWrapper.ne(bigMin != null, column, bigMin);
                queryWrapper.ne(intMin != null, column, intMin);
                break;
            case ">":
                if (longMin != null) {
                    queryWrapper.gt(column, longMin / 1000);
                }
                queryWrapper.gt(bigMin != null, column, bigMin);
                queryWrapper.gt(intMin != null, column, intMin);
                break;
            case "<":
                if (longMin != null) {
                    queryWrapper.lt(column, longMin / 1000);
                }
                queryWrapper.lt(bigMin != null, column, bigMin);
                queryWrapper.lt(intMin != null, column, intMin);
                break;
            case ">=":
                if (longMin != null) {
                    queryWrapper.ge(column, longMin / 1000);
                }
                queryWrapper.ge(bigMin != null, column, bigMin);
                queryWrapper.ge(intMin != null, column, intMin);
                break;
            case "<=":
                if (longMin != null) {
                    queryWrapper.le(column, longMin / 1000);
                }
                queryWrapper.le(bigMin != null, column, bigMin);
                queryWrapper.le(intMin != null, column, intMin);
                break;
            case "isNull":
                queryWrapper.and(wra -> {
                    wra.isNull(column);
                    wra.or();
                    wra.eq(column, "");
                });
                break;
            case "notNull":
                queryWrapper.isNotNull(column);
                break;
            default:
                break;
        }

    }


    /**
     * 获取工艺卡控进度
     *
     * @param vo PoItemVO
     */
    private void getCraftCtrl(PoOffsetViewVO vo) {
        List<PoItemCraftCtrlNodeVO> list = poItemCraftCtrlNodeService.getByPoItemId(vo.getPiId());
        vo.setPoItemCraftCtrlNodeVos(list);
        Item item = itemService.getByCode(vo.getItemCode());
        vo.setCraftCtrlNodeName(StringUtil.isEmpty(item.getCraftctrlName()) ? null : item.getCraftctrlName());
        if (!StringUtil.isEmpty(item.getCraftctrlCode())) {
            CraftCtrlNodeEntity craftCtrlNodeEntity = craftCtrlNodeService.getByCode(item.getCraftctrlCode());
            vo.setCraftCtrlNodeId(StringUtil.isEmpty(craftCtrlNodeEntity) ? null : craftCtrlNodeEntity.getId());
        }
    }
}
