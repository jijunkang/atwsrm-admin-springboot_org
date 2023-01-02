package org.springblade.modules.forecast.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.forecast.entity.ForecastEntity;

import java.util.List;
import java.util.Map;

/**
 * 情报 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ForecastVO extends ForecastEntity {

	private static final long serialVersionUID = 1L;

}
