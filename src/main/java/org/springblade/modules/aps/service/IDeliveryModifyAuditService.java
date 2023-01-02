package org.springblade.modules.aps.service;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.aps.entity.DeliveryModifyAuditEntity;

import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IDeliveryModifyAuditService extends BaseService<DeliveryModifyAuditEntity> {

    DeliveryModifyAuditEntity getByProNoSubAndItemCodeLimit(String proNoSub, String itemCode);

    List<DeliveryModifyAuditEntity> getByPoCodeAndLns(String poCode,Integer poLn);
}
