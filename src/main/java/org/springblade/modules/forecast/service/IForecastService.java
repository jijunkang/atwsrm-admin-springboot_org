package org.springblade.modules.forecast.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.forecast.entity.ForecastEntity;
import org.springblade.modules.forecast.vo.ForecastVO;

import java.util.Map;

/**
 * 情报 服务类
 *
 * @author Will
 */
public interface IForecastService extends BaseService<ForecastEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param forecast
	 * @return
	 */

	IPage<Map<String, Object>> selectYmPage(IPage<ForecastEntity> page, ForecastEntity forecast);
}
