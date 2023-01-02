package org.springblade.modules.system.service.impl;

import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.modules.system.entity.AuditRecordEntity;
import org.springblade.modules.system.mapper.AuditRecordMapper;
import org.springblade.modules.system.service.IAuditRecordService;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class AuditRecordServiceImpl extends BaseServiceImpl<AuditRecordMapper, AuditRecordEntity> implements IAuditRecordService {

    @Override
    public
    AuditRecordEntity record(String objType, Long objId, Integer auditStatus, String remark){
        AuditRecordEntity entity = new AuditRecordEntity();
        entity.setObjType(objType);
        entity.setObjId(objId);
        entity.setAuditUser(AuthUtil.getUserId());
        entity.setAuditTime(System.currentTimeMillis()/1000);
        entity.setAuditStatus(auditStatus);
        entity.setRemark(remark);
        save(entity);
        return entity;
    }

    @Override
    public
    AuditRecordEntity record(String objType, CheckDTO checkDTO){
        return record(objType,checkDTO.getId(),checkDTO.getStatus(),checkDTO.getRemark());
    }
}
