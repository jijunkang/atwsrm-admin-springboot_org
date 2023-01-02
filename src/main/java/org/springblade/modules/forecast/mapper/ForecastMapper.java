package org.springblade.modules.forecast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.forecast.entity.ForecastEntity;

import java.util.List;

/**
 * 情报 Mapper 接口
 *
 * @author Will
 */
public interface ForecastMapper extends BaseMapper<ForecastEntity> {

	List<ForecastEntity> selectYmPage(@Param("page") IPage<ForecastEntity> page,@Param("forecast") ForecastEntity forecast);
}
