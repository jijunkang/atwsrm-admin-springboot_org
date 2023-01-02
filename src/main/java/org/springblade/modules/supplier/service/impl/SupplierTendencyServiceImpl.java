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
package org.springblade.modules.supplier.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.springblade.modules.supplier.entity.SupplierTendency;
import org.springblade.modules.supplier.mapper.SupplierTendencyMapper;
import org.springblade.modules.supplier.service.ISupplierTendencyService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.*;

/**
 * 供应商动态信息 服务实现类
 *
 * @author Blade
 * @since 2019-11-23
 */
@Service
public class SupplierTendencyServiceImpl extends BaseServiceImpl<SupplierTendencyMapper, SupplierTendency> implements ISupplierTendencyService {

	@Override
	public IPage<Map<String, Object>> selectSupplierTendencyPage(IPage<SupplierTendency> page, SupplierTendency supplierTendency) {


		//18年一月一日时间戳
		Long t0 = 1483200000L;
		//19年十二月一日时间戳
		Long t12 = 1577721600L;
		//一个月30天的时间戳毫秒数
		Long d = 30 * 24 * 3600L;
		List<Map<String, Object>> result = Lists.newArrayList();


		for (Long time = t0; time < t12; time += d) {
			Map<String, Object> resultItem = new HashMap<>();
			SupplierTendency queryFs = new SupplierTendency();
			queryFs.setSupCode(supplierTendency.getSupCode());
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(time * 1000);
			SupplierTendency findFs = baseMapper.getAvgTendency(supplierTendency, time, time + d);
			resultItem.put(calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1), findFs == null ? 0 : findFs.getPassRate());
			result.add(resultItem);
		}


		IPage<Map<String, Object>> resultPage = new Page();
		resultPage.setRecords(result);
		return resultPage;
	}

}
