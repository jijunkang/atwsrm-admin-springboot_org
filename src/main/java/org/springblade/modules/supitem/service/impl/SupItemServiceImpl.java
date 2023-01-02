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
package org.springblade.modules.supitem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.supitem.dto.SupItemExcelDTO;
import org.springblade.modules.supitem.entity.SupItem;
import org.springblade.modules.supitem.mapper.SupItemMapper;
import org.springblade.modules.supitem.service.ISupItemService;
import org.springblade.modules.supitem.vo.SupItemVO;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 服务实现类
 * @author Blade
 * @since 2019-12-04
 */
@Service
public
class SupItemServiceImpl extends BaseServiceImpl<SupItemMapper, SupItem> implements ISupItemService{

    @Autowired
    @Lazy
    ISupplierService supplierService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Override
    public
    IPage<SupItemVO> selectSupItemPage(IPage<SupItemVO> page, SupItemVO supItem){
        return page.setRecords(baseMapper.selectSupItemPage(page, supItem));
    }

    @Override public
    List<SupItem> listSupItemByItemCode(String itemCode){
        SupItem supItem = new SupItem();
        supItem.setItemCode(itemCode);
        QueryWrapper<SupItem> query = Condition.getQueryWrapper(supItem);
        return list(query);
    }


    @Override public
    int countSupItemByItemCode(String itemCode){
        SupItem supItem = new SupItem();
        supItem.setItemCode(itemCode);
        QueryWrapper<SupItem> query = Condition.getQueryWrapper(supItem);
        return count(query);
    }

    /**
     * @return
     */
    @Override public
    int updateByCode(String itemCode, String[] supCodes){

        Item        item       = itemService.getByCode(itemCode);
        Set<String> supCodeSet = Sets.newHashSet(Arrays.asList(supCodes));

        baseMapper.deleteByItemCode(itemCode);
        List<SupItem> newList = Lists.newArrayList();
        for(String supCode : supCodeSet){
            Supplier supplier = supplierService.getByCode(supCode);
            SupItem newSi = new SupItem();
            newList.add(newSi);

            newSi.setItemCode(itemCode);
            newSi.setItemName(item.getName());
            newSi.setSupCode(supplier.getCode());
            newSi.setSupName(supplier.getName());

        }
        saveBatch(newList);
        return newList.size();
    }


    /**
     * 导入excel
     * @return
     */
    @Override public
    boolean importExcel(MultipartFile file) throws Exception{
        List<SupItemExcelDTO> dtoList    = ExcelUtils.importExcel(file, 0, 1, SupItemExcelDTO.class);
        List<SupItem>         entityList = BeanUtil.copy(dtoList, SupItem.class);
        for(SupItem entity : entityList){
            SupItem  old = getByCode(entity.getItemCode(),entity.getSupCode());
            if(old!=null){
                entity.setId(old.getId());
                entity.setIsDeleted(0);
            }
            saveOrUpdate(entity);
            //更新物料 sup_count
            itemService.updateSupCount(entity.getItemCode());
        }
        return true;
    }

    @Override
    public SupItem getByCode(String itemCode, String supCode){
        return baseMapper.selectByCode(itemCode, supCode );
    }

}
