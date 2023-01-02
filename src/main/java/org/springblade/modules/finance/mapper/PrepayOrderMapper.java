package org.springblade.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.finance.entity.PrepayOrderEntity;

import java.util.Date;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PrepayOrderMapper extends BaseMapper<PrepayOrderEntity> {

    int getTodayCount(Date start,Date end);

    int getCountOfPoCode(@Param("po_code") String poCode, @Param("prepay_code") String code);

}
