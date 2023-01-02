package org.springblade.modules.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.mapper.PaywayMapper;
import org.springblade.modules.supplier.service.IPaywayService;
import org.springblade.modules.supplier.vo.PaywayVO;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class PaywayServiceImpl extends BaseServiceImpl<PaywayMapper, PaywayEntity> implements IPaywayService {

    @Override
    public PaywayEntity getBySupCode(String supCode) {
        QueryWrapper<PaywayEntity> queryWrapper = Condition.getQueryWrapper(new PaywayEntity());
        queryWrapper.eq("sup_code", supCode);
        queryWrapper.eq("is_default", 1);
        return getOne(queryWrapper);
    }


    @Override
    public IPage<PaywayVO> getPayWayAndTemplate(IPage<PaywayVO> page, String supCode) {
        return this.baseMapper.getPayWayAndTemplate(page,supCode);
    }
}
