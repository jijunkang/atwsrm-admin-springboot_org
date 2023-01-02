package org.springblade.modules.aps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.modules.aps.entity.DeliveryModifyAuditEntity;
import org.springblade.modules.aps.mapper.DeliveryModifyAuditMapper;
import org.springblade.modules.aps.service.IDeliveryModifyAuditService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class DeliveryModifyAuditServiceImpl extends BaseServiceImpl<DeliveryModifyAuditMapper, DeliveryModifyAuditEntity> implements IDeliveryModifyAuditService {

    @Override
    public DeliveryModifyAuditEntity getByProNoSubAndItemCodeLimit(String proNoSub, String itemCode) {
        return this.baseMapper.getByProNoSubAndItemCodeLimit(proNoSub, itemCode);
    }

    @Override
    public List<DeliveryModifyAuditEntity> getByPoCodeAndLns(String poCode, Integer poLn) {
        return this.baseMapper.getByPoCodeAndLns(poCode,poLn);
    }
}
