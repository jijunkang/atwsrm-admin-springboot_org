package org.springblade.modules.mathmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.mathmodel.entity.MmSizeEntity;
import org.springblade.modules.mathmodel.entity.MmVolumeCalculateEntity;

/**
 * @author libin
 *
 * @date 11:18 2020/9/11
 **/
public interface MmVolumeCalculateMapper extends BaseMapper<MmVolumeCalculateEntity> {

    void deleteByMmSize(@Param("entity") MmSizeEntity entity);

}
