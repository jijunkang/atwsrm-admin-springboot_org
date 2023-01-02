package org.springblade.modules.aps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.aps.entity.DeliveryModifyAuditEntity;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface DeliveryModifyAuditMapper extends BaseMapper<DeliveryModifyAuditEntity> {

    DeliveryModifyAuditEntity  getByProNoSubAndItemCodeLimit(@Param("proNoSub") String proNoSub,@Param("itemCode") String itemCode);

    @Select({
        "select * from atw_delivery_modify_audit where po_code = #{poCode} and po_ln = #{poLn}"
    })
    List<DeliveryModifyAuditEntity> getByPoCodeAndLns(@Param("poCode") String poCode, @Param("poLn") Integer poLn);
}
