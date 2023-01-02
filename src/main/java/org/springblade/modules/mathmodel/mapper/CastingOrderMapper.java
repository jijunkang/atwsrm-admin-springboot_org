package org.springblade.modules.mathmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;

import java.util.List;

/**
 * Author: 昕月
 * Date：2022/6/7 19:54
 * Desc:
 */
@Mapper
public interface CastingOrderMapper extends BaseMapper<CastingOrderEntity> {

    List<CastingOrderEntity> selectSupName(@Param("req") CastingOrderEntity entity);

    String selectItemPrice(@Param("castingOrderEntity") CastingOrderEntity castingOrderEntity);
}
