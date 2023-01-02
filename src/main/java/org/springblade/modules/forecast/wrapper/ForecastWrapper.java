package org.springblade.modules.forecast.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.forecast.entity.ForecastEntity;
import org.springblade.modules.forecast.vo.ForecastVO;

/**
 * 情报 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ForecastWrapper extends BaseEntityWrapper<ForecastEntity, ForecastVO>  {

	public static ForecastWrapper build() {
		return new ForecastWrapper();
 	}

	@Override
	public ForecastVO entityVO(ForecastEntity forecast) {
		ForecastVO forecastVO = BeanUtil.copy(forecast, ForecastVO.class);

		return forecastVO;
	}

}
