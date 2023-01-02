package org.springblade.modules.mathmodel.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.pr.vo.MaterialMaliyVO;

import java.util.List;

/**
 * Author: 昕月
 * Date：2022/5/24 13:36
 * Desc:
 */
@Mapper
public interface TubeMaterialMapper extends BaseMapper<MailyMaterialTotalEntity> {

}
