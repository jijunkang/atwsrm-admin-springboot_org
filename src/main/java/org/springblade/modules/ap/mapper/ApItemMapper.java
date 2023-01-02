package org.springblade.modules.ap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.ap.entity.ApItemEntity;


/**
 * @author libin
 *
 * @date 15:16 2020/6/3
 **/
public interface ApItemMapper extends BaseMapper<ApItemEntity> {

    String getPoCodesByApId(Long id, String type);

    int getMinPayDateByBillId(Long id);
}
