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
package org.springblade.modules.item.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.item.dto.ItemMoreReq;
import org.springblade.modules.item.dto.ItemReq;
import org.springblade.modules.item.dto.SupItemSaveDTO;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.vo.ItemVO;
import org.springblade.modules.pr.dto.SubmitPriceReq;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 物料表 服务类
 * @author Blade
 * @since 2019-11-27
 */
public
interface IItemService extends BaseService<Item>{

	String PUR_ATTR_PRICELIB = "pricelib";      //白名单
	String PUR_ATTR_MODEL    = "model";         //数学模型
	String PUR_ATTR_PROTOCOL = "protocol";      //框架协议

    String[] MAIN_CODE_GASCTRLS = {"131613", "140101", "140102", "140103", "140104", "140105", "140106", "140107", "140109",
            "140110", "140111", "140113", "140116", "140117", "140118", "140121", "140301", "140303", "140305", "140307",
            "140308"};

	/**
	 * 自定义分页
	 * @return
	 */
	IPage<ItemVO> selectItemPage(IPage<ItemVO> page, Item item);

	/**
	 * 保存交叉关系
	 * @return
	 */
	boolean saveSupItem(SupItemSaveDTO supItems);

	/**
	 * 未完善的物料
	 * @return
	 */
	IPage<ItemVO> incompletePage(IPage<ItemVO> page, Item item);

    void exportAllItemInfo(Item item, HttpServletResponse response);

	boolean updateById(ItemReq item);

    Item getByCode(String itemCode);

	boolean updateBatch(List<ItemReq> itemList);

	boolean updateSupCount(String itemCode);

	int incompleteCount();

	int dullCount();

	/**
	 * 导入修改
	 * @param file
	 * @return
	 */
	boolean updateByExcel(MultipartFile file) throws Exception;

    /**
     * 是否是气控件
     * 2020.07.29
     * @return
     */
    boolean isGasCtrl(String mainCode);

    /**
     * listMore
     *
     * @param query Query
     * @param itemMoreReq ItemMoreReq
     * @return IPage
     */
    IPage<Item> listMore(Query query, ItemMoreReq itemMoreReq);

    /**
     * export
     *
     * @param itemMoreReq ItemMoreReq
     * @param httpServletResponse HttpServletResponse
     */
    void export(ItemMoreReq itemMoreReq, HttpServletResponse httpServletResponse);

    boolean updateByIds(Item item, String ids);
}
