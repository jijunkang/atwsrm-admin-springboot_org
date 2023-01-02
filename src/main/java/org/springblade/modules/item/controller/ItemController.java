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
package org.springblade.modules.item.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.item.dto.ItemMoreReq;
import org.springblade.modules.item.dto.ItemReq;
import org.springblade.modules.item.dto.SupItemSaveDTO;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.item.vo.ItemVO;
import org.springblade.modules.item.wrapper.ItemWrapper;
import org.springblade.modules.pr.dto.SubmitPriceReq;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 物料表 控制器
 *
 * @author Blade
 * @since 2019-11-27
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-item/item")
@Api(value = "物料表", tags = "物料表接口")
public class ItemController extends BladeController {

    private IItemService itemService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入item")
    public R<ItemVO> detail(Item item) {
        Item detail = itemService.getOne(Condition.getQueryWrapper(item));
        return R.data(ItemWrapper.build().entityVO(detail));
    }

    /**
     * 分页 物料表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入item")
    public R<IPage<ItemVO>> list(Item item,  Query query) {
        IPage<Item> pages = itemService.page(Condition.getPage(query), Condition.getQueryWrapper(item).orderByDesc("update_time"));
        return R.data(ItemWrapper.build().pageVO(pages));
    }


    /**
     * 自定义分页 物料表
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入item")
    public R<IPage<ItemVO>> page(Item item,  Query query) {
        IPage<ItemVO> pages = itemService.selectItemPage(Condition.getPage(query), item);
        return R.data(pages);
    }

    /**
     * 需要完善资料的物料
     */
    @GetMapping("/incompletepage")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入item")
    public R<IPage<ItemVO>> incompletePage(Item item,  Query query) {
        IPage<ItemVO> pages = itemService.incompletePage(Condition.getPage(query), item);
        return R.data(pages);
    }

    @GetMapping("/exportIncomplete")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportIncomplete(Item item, HttpServletResponse response) {
        itemService.exportAllItemInfo(item, response);
    }

    /**
     * 修改 物料表
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入item")
    public
    R update(@Valid @RequestBody ItemReq item){
        return R.status(itemService.updateById(item));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/updatebatch")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入pricelib")
    public
    R updateBatch(@Valid @RequestBody List<ItemReq> itemList){
        return R.status(itemService.updateBatch(itemList));
    }

    /**
     * 导入修改
     */
    @PostMapping("/updatebyexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public
    R updateByExcel(@Valid @RequestParam MultipartFile file){
        try{
            return R.status(itemService.updateByExcel(file));
        }catch(Exception e){
            e.printStackTrace();
            return R.fail("读取excel文件失败");
        }
    }

    /**
     * 删除 物料表
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public
    R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
        return R.status(itemService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 保存交叉关系
     */
    @PostMapping("/saveSupItem")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "保存交叉关系", notes = "传入supItem")
    public
    R saveSupItem(@Valid @RequestBody SupItemSaveDTO supItems){
        return R.status(itemService.saveSupItem(supItems));
    }


    /**
     * 高级查询 物料表
     */
    @GetMapping("/listmore")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入itemMoreReq")
    public R<IPage<Item>> listMore(ItemMoreReq itemMoreReq, Query query) {
        return R.data(itemService.listMore(query, itemMoreReq));
    }


    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入itemMoreReq")
    public void export(ItemMoreReq itemMoreReq, HttpServletResponse response) throws Exception {
        itemService.export(itemMoreReq, response);
    }


    /**
     * 批量修改采购员
     * @param item
     * @param ids
     * @return
     */
    @PutMapping("/updatebysss")
    public R brachUpdate(Item item,String ids){
        return R.status( itemService.updateByIds(item,ids));
    }
}
