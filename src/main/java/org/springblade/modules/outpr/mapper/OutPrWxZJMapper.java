package org.springblade.modules.outpr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.outpr.entity.OutPrWxZJEntity;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface OutPrWxZJMapper extends BaseMapper<OutPrWxZJEntity> {
    List<OutPrWxZJEntity> getOutPrWxZJInfo(@Param("itemInfoEntity") ItemInfoEntityOfZDJ itemInfoEntityOfZDJ,@Param("supName") String supName);
}
