package org.springblade.modules.system.service;

import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.system.entity.AuditRecordEntity;

/**
 * 服务类
 * @author Will
 */
public
interface IAuditRecordService extends BaseService<AuditRecordEntity>{

    String TYPE_PREPAY = "prepay";

    AuditRecordEntity record(String objType, Long objId, Integer auditStatus, String remark);

    AuditRecordEntity record(String objType, CheckDTO checkDTO);
}
