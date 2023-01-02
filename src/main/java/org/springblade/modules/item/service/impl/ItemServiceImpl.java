/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author:  William Wang (wxx@idwsoft.com)
 */
package org.springblade.modules.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.dto.ItemExcelDTO;
import org.springblade.modules.item.dto.ItemMoreReq;
import org.springblade.modules.item.dto.ItemReq;
import org.springblade.modules.item.dto.SupItemSaveDTO;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.mapper.ItemMapper;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.item.vo.ItemExcel;
import org.springblade.modules.item.vo.ItemVO;
import org.springblade.modules.supitem.entity.SupItem;
import org.springblade.modules.supitem.service.ISupItemService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IParamService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * 物料表 服务实现类
 * @author Blade
 * @since 2019-11-27
 */
@Service
public
class ItemServiceImpl extends BaseServiceImpl<ItemMapper, Item> implements IItemService{

    @Autowired
    ISupItemService supItemService;

    @Autowired
    IUserService userService;

    @Autowired
    IDictBizService dictBizService;

    @Autowired
    @Lazy
    IParamService paramService;


    /**
     * 根据 code查找
     * @return
     */
    @Override
    public
    Item getByCode(String itemCode){
        QueryWrapper<Item> query = Condition.getQueryWrapper(new Item()).eq("code", itemCode);
        return getOne(query);
    }

    @Override
    public
    IPage<ItemVO> selectItemPage(IPage<ItemVO> page, Item item){
        if(StringUtils.isNotEmpty(item.getCode()) && item.getCode().split(",").length > 1){
            return page.setRecords(baseMapper.selectItemPageOfList(page, item));
        }
        return page.setRecords(baseMapper.selectItemPage(page, item));
    }

    @Override
    public
    IPage<ItemVO> incompletePage(IPage<ItemVO> page, Item item){

        String userCode = SecureUtil.getUser().getAccount();
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            item.setPurchCode(userCode);
        }
        List<ItemVO> records = baseMapper.incompletePage(page, item);
        for(ItemVO vo : records){

            List<SupItem> supItemList = supItemService.listSupItemByItemCode(vo.getCode());
            if(supItemList!=null && supItemList.size() > 0 ) {
                String supItems = "";
                for(SupItem supItem: supItemList) {
                    supItems = supItems + "," +supItem.getSupName();
                }
                vo.setSupItems(supItems.substring(1,supItems.length()));
            }
        }


        return page.setRecords(records);
    }

    @Override
    public
    int incompleteCount(){
        Item item = new Item();
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            item.setPurchCode(SecureUtil.getUser().getAccount());
        }
        return baseMapper.incompleteCount(item);
    }

    /**
     * 呆滞物料数量
     * @return
     */
    @Override
    public
    int dullCount(){
        return baseMapper.dullCount();
    }

    @Override
    public
    boolean saveSupItem(SupItemSaveDTO supItems){
        //        baseMapper.delete();
        return true;
    }


    public
    boolean updateById(ItemReq req){
        Item item = getById(req.getId());
        if(StringUtils.isBlank(req.getSOrN())){
            req.setSOrN(null);
        }
        BeanUtil.copy(req, item);
        // 更新供应商交叉
        if(req.getSupCodes() != null){
            item.setSupCount(supItemService.updateByCode(item.getCode(), req.getSupCodes()));
        }
        if(StringUtils.isNotBlank(req.getPurchCode())){
            User purchUser = userService.getByAccount(AuthUtil.getTenantId(), req.getPurchCode());
            if (purchUser != null) {
                item.setPurchName(purchUser.getRealName());
            }

        }
        if(StringUtils.isNotBlank(req.getTraceCode())){
            User tractUser = userService.getByAccount(AuthUtil.getTenantId(), req.getTraceCode());
            if (tractUser != null) {
                item.setTraceName(tractUser.getRealName());
            }

        }
        if(StringUtils.isNotBlank(req.getPurchCode())){
            User tractUser = userService.getByAccount(AuthUtil.getTenantId(), req.getPurchCode());
            if (tractUser != null) {
                item.setPurchName(tractUser.getRealName());
            }

        }
        return super.updateById(item);
    }

    @Override
    public
    boolean updateBatch(List<ItemReq> itemList){
        for(ItemReq item : itemList){
            updateById(item);
        }
        return true;
    }

    @Override
    public
    boolean updateSupCount(String itemCode){
        Item entity = getByCode(itemCode);
        if(entity == null){
            return false;
        }
        entity.setSupCount(supItemService.countSupItemByItemCode(itemCode));
        return updateById(entity);
    }

    /**
     * 导入修改
     * @return
     */
    @Override
    public
    boolean updateByExcel(MultipartFile file) throws Exception{
        List<Item> dtoList = ExcelUtils.importExcel(file, 0, 1, Item.class);
        for(Item dto : dtoList){
            Item entity = getByCode(dto.getCode());
            if(entity == null){
                continue;
            }
            update(dto, Condition.getQueryWrapper(new Item()).eq("code", dto.getCode()));
        }
        return true;
    }


    @Override
    public
    boolean isGasCtrl(String mainCode){
        for(String gasCode : MAIN_CODE_GASCTRLS){
            if(gasCode.equals(mainCode))
                return true;
        }
        return false;
    }

    @Override
    public IPage<Item> listMore(Query query, ItemMoreReq itemMoreReq) {
        IPage<Item> page = Condition.getPage(query);
        if("and".equals(itemMoreReq.getSelectType())){
            page = page(Condition.getPage(query), getAndQueryWrapper(itemMoreReq));
        } else if("or".equals(itemMoreReq.getSelectType())){
            page = page(Condition.getPage(query), getOrQueryWrapper(itemMoreReq));
        }
        return page;
    }

    @Override
    public void export(ItemMoreReq itemMoreReq, HttpServletResponse httpServletResponse) {
        List<Item> list = list(getAndQueryWrapper(itemMoreReq));
        List<ItemExcelDTO> excelList = Lists.newArrayList();
        for (Item item : list) {
            ItemExcelDTO dto = BeanUtil.copy(item, ItemExcelDTO.class);
            dto.setSORN(item.getSOrN());
            if(StringUtil.isNotBlank(item.getPurchAttr())){
                dto.setPurchAttr(dictBizService.getValue("item_pur_attr", item.getPurchAttr()));
            }
            excelList.add(dto);
        }
        ExcelUtils.defaultExport(excelList, ItemExcelDTO.class, "采购目录" + DateUtil.formatDate(new Date()), httpServletResponse);
    }

    @Override
    public boolean updateByIds(Item item, String ids) {
        boolean flag = false;
        if (StringUtils.isNotBlank(ids)){
            String[] id = ids.split(",");
            List<Item> list = this.baseMapper.updateByIds(id);
            flag = true;
        }
        return flag;
    }

    /**
     * getAndQueryWrapper
     *
     * @param itemMoreReq ItemMoreReq
     * @return QueryWrapper
     */
    private QueryWrapper<Item> getAndQueryWrapper(ItemMoreReq itemMoreReq){
        QueryWrapper<Item> queryWrapper = Condition.getQueryWrapper(new Item());
        //主分类
        getStringValue(queryWrapper, itemMoreReq.getMainNameType(), itemMoreReq.getMainName(), null, "main_name");
        //物料编码

        if(itemMoreReq.getCode()!= null && itemMoreReq.getCode().indexOf(",")>-1) {
            getStringValue(queryWrapper, "find", itemMoreReq.getCode(), null, "code");
        } else {
            getStringValue(queryWrapper, itemMoreReq.getCodeType(), itemMoreReq.getCode(), null, "code");
        }


        //物料名称
        getStringValue(queryWrapper, itemMoreReq.getNameType(), itemMoreReq.getName(), null, "name");
        //采购倍数
        getNumberValue(queryWrapper, itemMoreReq.getPurchMultipleType(), itemMoreReq.getPurchMultipleMax(),
            itemMoreReq.getPurchMultipleMin(), null, null, "purch_multiple");
        //最小起订量
        getNumberValue(queryWrapper, itemMoreReq.getPurchMixType(), itemMoreReq.getPurchMixMax(), itemMoreReq.getPurchMixMin(),
            null, null, "purch_mix");
        //安全库存
        getNumberValue(queryWrapper, itemMoreReq.getStockLowerLimitType(), itemMoreReq.getStockLowerLimitMax(), itemMoreReq.getStockLowerLimitMax(),
            null, null, "stock_lower_limit");
        //采购预提前期
        getNumberValue(queryWrapper, itemMoreReq.getPurchBeforeDateType(), null, null, itemMoreReq.getPurchBeforeDateMax(),
            itemMoreReq.getPurchBeforeDateMin(), "purch_before_date");
        //采购后提前期
        getNumberValue(queryWrapper, itemMoreReq.getPurchAfterDateType(), null, null, itemMoreReq.getPurchAfterDateMax(),
            itemMoreReq.getPurchAfterDateMin(), "purch_after_date");
        //采购处理提前期
        getNumberValue(queryWrapper, itemMoreReq.getPurchDisposeDateType(), null, null, itemMoreReq.getPurchDisposeDateMax(),
            itemMoreReq.getPurchDisposeDateMin(), "purch_dispose_date");
        //价格属性
        getStringValue(queryWrapper, itemMoreReq.getPurchAttrType(), itemMoreReq.getPurchAttrs(), itemMoreReq.getPurchAttrList(), "purch_attr");
        //采购员
        getStringValue(queryWrapper, itemMoreReq.getPurchCodeType(), itemMoreReq.getPurchCodes(), itemMoreReq.getPurchCodeList(), "purch_code");
        //下单员
        getStringValue(queryWrapper, itemMoreReq.getPlaceCodeType(), itemMoreReq.getPlaceCodes(), itemMoreReq.getPlaceCodeList(), "place_code");
        //跟单员
        getStringValue(queryWrapper, itemMoreReq.getTraceCodeType(), itemMoreReq.getTraceCodes(), itemMoreReq.getTraceCodeList(), "trace_code");
        //系列化
        queryWrapper.and(temp ->{
            appendSOrNs(itemMoreReq, temp);
        });
        return queryWrapper;
    }

    /**
     * getOrQueryWrapper
     *
     * @param itemMoreReq ItemMoreReq
     * @return QueryWrapper
     */
    private QueryWrapper<Item> getOrQueryWrapper(ItemMoreReq itemMoreReq){
        QueryWrapper<Item> queryWrapper = Condition.getQueryWrapper(new Item());
        queryWrapper.and(wrapper -> {
            getStringValue(wrapper, itemMoreReq.getMainNameType(), itemMoreReq.getMainName(), null, "main_name");
            wrapper.or();
            getStringValue(wrapper, itemMoreReq.getCodeType(), itemMoreReq.getCode(), null, "code");
            wrapper.or();
            getStringValue(wrapper, itemMoreReq.getNameType(), itemMoreReq.getName(), null, "name");
            wrapper.or();
            getNumberValue(wrapper, itemMoreReq.getPurchMultipleType(), itemMoreReq.getPurchMultipleMax(),
                itemMoreReq.getPurchMultipleMin(), null, null, "purch_multiple");
            wrapper.or();
            getNumberValue(wrapper, itemMoreReq.getPurchMixType(), itemMoreReq.getPurchMixMax(), itemMoreReq.getPurchMixMin(),
                null, null, "purch_mix");
            wrapper.or();
            getNumberValue(wrapper, itemMoreReq.getStockLowerLimitType(), itemMoreReq.getStockLowerLimitMax(), itemMoreReq.getStockLowerLimitMax(),
                null, null, "stock_lower_limit");
            wrapper.or();
            getNumberValue(wrapper, itemMoreReq.getPurchBeforeDateType(), null, null, itemMoreReq.getPurchBeforeDateMax(),
                itemMoreReq.getPurchBeforeDateMin(), "purch_before_date");
            wrapper.or();
            getNumberValue(wrapper, itemMoreReq.getPurchAfterDateType(), null, null, itemMoreReq.getPurchAfterDateMax(),
                itemMoreReq.getPurchAfterDateMin(), "purch_after_date");
            wrapper.or();
            getNumberValue(wrapper, itemMoreReq.getPurchDisposeDateType(), null, null, itemMoreReq.getPurchDisposeDateMax(),
                itemMoreReq.getPurchDisposeDateMin(), "purch_dispose_date");
            wrapper.or();
            getStringValue(wrapper, itemMoreReq.getPurchAttrType(), itemMoreReq.getPurchAttrs(), itemMoreReq.getPurchAttrList(), "purch_attr");
            wrapper.or();
            getStringValue(wrapper, itemMoreReq.getPurchCodeType(), itemMoreReq.getPurchCodes(), itemMoreReq.getPurchCodeList(), "purch_code");
            wrapper.or();
            getStringValue(wrapper, itemMoreReq.getPlaceCodeType(), itemMoreReq.getPlaceCodes(), itemMoreReq.getPlaceCodeList(), "place_code");
            wrapper.or();
            getStringValue(wrapper, itemMoreReq.getTraceCodeType(), itemMoreReq.getTraceCodes(), itemMoreReq.getTraceCodeList(), "trace_code");

            if(StringUtil.isEmpty(itemMoreReq.getSOrNs()) && StringUtil.isEmpty(itemMoreReq.getMainCode()) && StringUtil.isEmpty(itemMoreReq.getCode())
                && StringUtil.isEmpty(itemMoreReq.getName()) && StringUtil.isEmpty(itemMoreReq.getPurchMultipleMax()) && StringUtil.isEmpty(itemMoreReq.getPurchMultipleMin())
                && StringUtil.isEmpty(itemMoreReq.getPurchMixMax()) && StringUtil.isEmpty(itemMoreReq.getPurchMixMin()) && StringUtil.isEmpty(itemMoreReq.getStockLowerLimitMin())
                && StringUtil.isEmpty(itemMoreReq.getStockLowerLimitMax()) && StringUtil.isEmpty(itemMoreReq.getPurchBeforeDateMax()) && StringUtil.isEmpty(itemMoreReq.getPurchBeforeDateMin())
                && StringUtil.isEmpty(itemMoreReq.getPurchAfterDateMax()) && StringUtil.isEmpty(itemMoreReq.getPurchAfterDateMin()) && StringUtil.isEmpty(itemMoreReq.getPurchDisposeDateMax())
                && StringUtil.isEmpty(itemMoreReq.getPurchDisposeDateMin()) && StringUtil.isEmpty(itemMoreReq.getPurchAttrs()) && StringUtil.isEmpty(itemMoreReq.getPurchCodes())
                && StringUtil.isEmpty(itemMoreReq.getPlaceCodes()) && StringUtil.isEmpty(itemMoreReq.getTraceCodes())){
                wrapper.eq("1", 1);
            }
            wrapper.or(temp ->{
                appendSOrNs(itemMoreReq, temp);
            });
        });
        return queryWrapper;
    }



    /**
     * appendSOrN
     *
     * @param itemMoreReq ItemMoreReq
     * @param queryWrapper QueryWrapper
     */
    private void appendSOrNs(ItemMoreReq itemMoreReq, QueryWrapper<Item> queryWrapper) {
        if (StringUtil.isNotBlank(itemMoreReq.getSOrNType())) {
            switch (itemMoreReq.getSOrNType()) {
                case "isNull":
                    queryWrapper.isNull("s_or_n");
                    break;
                case "notNull":
                    queryWrapper.isNotNull("s_or_n");
                    break;
                case "in":
                case "notIn":
                    getSOrNWrapper(itemMoreReq, queryWrapper);
                    break;
                default:
                    queryWrapper.eq("1", 1);
                    break;
            }
        } else {
            //普通查询
            if (StringUtil.isNotBlank(itemMoreReq.getSOrN())) {
                if ("NULL".equals(itemMoreReq.getSOrN())) {
                    queryWrapper.isNull("s_or_n");
                } else {
                    queryWrapper.eq("s_or_n", itemMoreReq.getSOrN());
                }
            } else {
                queryWrapper.eq("1", 1);
            }
        }
    }

    /**
     * getSOrNWrapper
     *
     * @param itemMoreReq ItemMoreReq
     * @param queryWrapper QueryWrapper
     */
    private void getSOrNWrapper(ItemMoreReq itemMoreReq, QueryWrapper<Item> queryWrapper) {
        if (StringUtil.isNotBlank(itemMoreReq.getSOrNs())) {
            if (itemMoreReq.getSOrNs().contains(",")) {
                for (String str : itemMoreReq.getSOrNs().split(",")) {
                    if ("NULL".equals(str)) {
                        if("in".equals(itemMoreReq.getSOrNType())){
                            queryWrapper.isNull("s_or_n");
                        }else if("notIn".equals(itemMoreReq.getSOrNType())){
                            queryWrapper.isNotNull("s_or_n");
                        }
                        queryWrapper.or();
                    } else {
                        if("in".equals(itemMoreReq.getSOrNType())){
                            queryWrapper.eq("s_or_n", str);
                        }else if("notIn".equals(itemMoreReq.getSOrNType())){
                            queryWrapper.ne("s_or_n", str);
                        }
                        queryWrapper.or();
                    }
                }
            } else {
                if ("NULL".equals(itemMoreReq.getSOrNs())) {
                    if("in".equals(itemMoreReq.getSOrNType())){
                        queryWrapper.isNull("s_or_n");
                    }else if("notIn".equals(itemMoreReq.getSOrNType())){
                        queryWrapper.isNotNull("s_or_n");
                    }
                    queryWrapper.or();
                } else {
                    if("in".equals(itemMoreReq.getSOrNType())){
                        queryWrapper.eq("s_or_n", itemMoreReq.getSOrNs());
                    }else if("notIn".equals(itemMoreReq.getSOrNType())){
                        queryWrapper.ne("s_or_n", itemMoreReq.getSOrNs());
                    }
                    queryWrapper.or();
                }
            }
        }else{
            queryWrapper.eq("1",1);
        }
    }

    /**
     * appendOrBySelectType
     *
     * @param selectType String
     * @param queryWrapper QueryWrapper
     */
    private void appendOrBySelectType(String selectType, QueryWrapper<Item> queryWrapper){
        if("or".equals(selectType)){
            queryWrapper.or();
        }
    }





    /**
     * 字符串列
     *
     * @param queryWrapper QueryWrapper
     * @param type         String
     * @param stringValue  String
     * @param setValue     Set
     * @param column       String
     */
    private void getStringValue(QueryWrapper<Item> queryWrapper, String type, String stringValue, Set<String> setValue, String column) {
        switch (StringUtil.isNotBlank(type) ? type : "") {
            case "like":
                queryWrapper.like(StringUtil.isNotBlank(stringValue), column, stringValue);
                break;
            case "notLike":
                queryWrapper.notLike(StringUtil.isNotBlank(stringValue), column, stringValue);
                break;
            case "isNull":
                queryWrapper.isNull(column);
                break;
            case "notNull":
                queryWrapper.isNotNull(column);
                break;
            case "in":
                queryWrapper.in(StringUtil.isNotBlank(stringValue), column, setValue);
                break;
            case "notIn":
                queryWrapper.notIn(StringUtil.isNotBlank(stringValue), column, setValue);
                break;
            case "==":
                queryWrapper.eq(StringUtil.isNotBlank(stringValue), column, stringValue);
                break;
            case "find":
                queryWrapper.apply(StringUtil.isNotBlank(stringValue), "FIND_IN_SET (" + "code,'"+ stringValue +"')");
                break;
            default:
                queryWrapper.like(StringUtil.isNotBlank(stringValue), column, stringValue);
                break;
        }
    }


    /**
     * getNumberValue
     *
     * @param queryWrapper QueryWrapper
     * @param type String
     * @param bigMax BigDecimal
     * @param bigMin BigDecimal
     * @param intMax Integer
     * @param intMin Integer
     * @param column String
     */
    private void getNumberValue(QueryWrapper<Item> queryWrapper, String type, BigDecimal bigMax, BigDecimal bigMin,
                                Integer intMax, Integer intMin, String column) {
        switch (StringUtil.isNotBlank(type) ? type : "") {
            case "between":
                //BigDecimal
                queryWrapper.ge(bigMin != null, column, bigMin);
                queryWrapper.le(bigMax != null, column, bigMax);
                //Integer
                queryWrapper.ge(intMin != null, column, intMin);
                queryWrapper.le(intMax != null, column, intMax);
                break;
            case "==":
                queryWrapper.eq(bigMin != null, column, bigMin);
                queryWrapper.eq(intMin != null, column, intMin);
                break;
            case "!=":
                queryWrapper.ne(bigMin != null, column, bigMin);
                queryWrapper.ne(intMin != null, column, intMin);
                break;
            case ">":
                queryWrapper.gt(bigMin != null, column, bigMin);
                queryWrapper.gt(intMin != null, column, intMin);
                break;
            case "<":
                queryWrapper.lt(bigMin != null, column, bigMin);
                queryWrapper.lt(intMin != null, column, intMin);
                break;
            case ">=":
                queryWrapper.ge(bigMin != null, column, bigMin);
                queryWrapper.ge(intMin != null, column, intMin);
                break;
            case "<=":
                queryWrapper.le(bigMin != null, column, bigMin);
                queryWrapper.le(intMin != null, column, intMin);
                break;
            case "isNull":
                queryWrapper.isNull(column);
                break;
            case "notNull":
                queryWrapper.isNotNull(column);
                break;
            default:
                break;
        }

    }

    @Override
    public void exportAllItemInfo(Item item, HttpServletResponse response) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            item.setPurchCode(SecureUtil.getUser().getAccount());
        }
        List<ItemVO> itemVOS = baseMapper.incompleteList(item);
        List<ItemExcel> itemExcels = new ArrayList<>();
        for(ItemVO vo : itemVOS){
            vo.setSupItemList(supItemService.listSupItemByItemCode(vo.getCode()));
        }

        for(ItemVO vo : itemVOS){
            String supItems = "";
            if(vo.getSupItemList()!=null && vo.getSupItemList().size()>0) {
                for(SupItem supItem: vo.getSupItemList()) {
                    supItems = supItems + "," +supItem.getSupName();
                }
            }
            ItemExcel itemExcel = BeanUtil.copy(vo, ItemExcel.class);
            if(!supItems.isEmpty()) {
                itemExcel.setSupItems(supItems.substring(1,supItems.length()));
            }
            itemExcels.add(itemExcel);
        }

        ExcelUtils.defaultExport(itemExcels, ItemExcel.class, "物料维护" + DateUtil.formatDate(new Date()), response);
    }


}
