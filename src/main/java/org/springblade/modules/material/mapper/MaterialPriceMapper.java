package org.springblade.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.material.entity.MaterialPriceEntity;

import java.math.BigDecimal;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface MaterialPriceMapper extends BaseMapper<MaterialPriceEntity> {

    BigDecimal getPriceKg(String itemName);

}
