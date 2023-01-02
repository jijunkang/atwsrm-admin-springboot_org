package org.springblade.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PrepayOrderItemMapper extends BaseMapper<PrepayOrderItemEntity> {

    String getPoCodesByPayId(Long prepayId);

}
