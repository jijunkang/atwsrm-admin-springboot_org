package org.springblade.modules.ap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.modules.ap.entity.ApEntity;
import org.springblade.modules.ap.entity.ApItemEntity;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.mapper.ApItemMapper;
import org.springblade.modules.ap.service.IApItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author libin
 * @date 15:16 2020/6/3
 **/
@Service
public class IApItemServiceImpl extends BaseServiceImpl<ApItemMapper, ApItemEntity> implements IApItemService {

    @Override
    public List<ApItemEntity> getApItemEntities(Long id, String type) {
        QueryWrapper<ApItemEntity> queryWrapper = Condition.getQueryWrapper(new ApItemEntity());

        if ("AP".equals(type)) {
            queryWrapper.eq("ap_id", id);
        }
        if ("BI".equals(type)) {
            queryWrapper.eq("bill_id", id);
        }
        return list(queryWrapper);
    }

    @Override
    public List<ApItemEntity> getGroupBy(Long id) {
        QueryWrapper<ApItemEntity> queryWrapper = Condition.getQueryWrapper(new ApItemEntity());
        queryWrapper.eq("bill_id", id);
        queryWrapper.groupBy("po_code");
        return list(queryWrapper);
    }

    @Override
    public List<ApItemEntity> getByRcvId(ApRcvEntity apRcvEntity) {
        QueryWrapper<ApItemEntity> queryWrapper = Condition.getQueryWrapper(new ApItemEntity());
        queryWrapper.eq("rcv_id", apRcvEntity.getId());
        return list(queryWrapper);
    }

    @Override
    public String getPoCodesByApId(Long apId, String type) {
        return this.baseMapper.getPoCodesByApId(apId, type);
    }

    @Override
    public int getMinPayDateByBillId(Long id) {
        return this.baseMapper.getMinPayDateByBillId(id);
    }
}
