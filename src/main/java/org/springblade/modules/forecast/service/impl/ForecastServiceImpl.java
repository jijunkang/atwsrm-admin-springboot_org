package org.springblade.modules.forecast.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;
import org.springblade.common.utils.CommonUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.modules.forecast.entity.ForecastEntity;
import org.springblade.modules.forecast.mapper.ForecastMapper;
import org.springblade.modules.forecast.service.IForecastService;
import org.springblade.modules.forecast.vo.ForecastVO;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 情报 服务实现类
 *
 * @author Will
 */
@Service
public class ForecastServiceImpl extends BaseServiceImpl<ForecastMapper, ForecastEntity> implements IForecastService {


	@Override
	public IPage<Map<String, Object>> selectYmPage(IPage<ForecastEntity> page, ForecastEntity forecast) {

		// group 查询物料编号
		IPage<ForecastEntity> groupPage = page.setRecords(baseMapper.selectYmPage(page, forecast));
		List<ForecastEntity> records = groupPage.getRecords();
		Set<String> monthList = new HashSet<>();
		if (CollectionUtil.isEmpty(records)) {
			return null;
		}

		// 查询 从 startMonth 到  endMonth 的数据
		String year = "2019";
		int startMonth = 6;
		int endMonth = 12;

		List<Map<String, Object>> result = Lists.newArrayList();
		records.forEach(o -> {
			Map<String, Object> retItem = new HashMap<>();
			retItem.put("item_code", o.getItemCode());
			retItem.put("item_name", o.getItemName());

			for (int month = startMonth; month <= endMonth; month++) {
				ForecastEntity queryFs = new ForecastEntity();
				queryFs.setItemCode(o.getItemCode());
				queryFs.setMark(o.getMark());
				queryFs.setForeYear(o.getForeYear());
				queryFs.setForeMonth(month);

				ForecastEntity findFs = getOne(Condition.getQueryWrapper(queryFs));
				retItem.put(year + "_" + month, findFs == null ? 0 : findFs.getForeQty());

			}

			result.add(retItem);

		});

		IPage<Map<String, Object>> retPage = new Page();
		retPage.setRecords(result);
		return retPage;
	}
}
