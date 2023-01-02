package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.po.entity.PoPronoEntity;

import java.util.Date;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PoPronoMapper extends BaseMapper<PoPronoEntity> {
     /**
      * 取找早的运算交期
      * @param supCode
      * @param itemCode
      * @return
      */
     Date findOptDate(@Param("supCode") String supCode, @Param("itemCode") String itemCode);

     /**
      * 取最晚的承诺交期
      * @param supCode
      * @param itemCode
      * @return
      */
     Date findComfDate(@Param("supCode") String supCode, @Param("itemCode") String itemCode);
}
