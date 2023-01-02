package org.springblade.modules.ap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.modules.ap.entity.ApInvoiceEntity;
import org.springblade.modules.ap.mapper.ApInvoiceMapper;
import org.springblade.modules.ap.service.IApInvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author libin
 *
 * @date 15:16 2020/6/3
 **/
@Service
public class IApInvoiceServiceImpl extends BaseServiceImpl<ApInvoiceMapper, ApInvoiceEntity> implements IApInvoiceService {

    @Override
    public List<ApInvoiceEntity> getApInvoiceEntities(Long id, String type) {
        QueryWrapper<ApInvoiceEntity> queryWrapper = Condition.getQueryWrapper(new ApInvoiceEntity());
        if("AP".equals(type)){
            queryWrapper.eq("ap_id", id);
        }
        if("BI".equals(type)){
            queryWrapper.eq("bill_id", id);
        }
        return list(queryWrapper);
    }

    @Override
    public String getInvoiceCodesByApId(Long apId, String type) {
        return this.baseMapper.getInvoiceCodeByApId(apId,type);
    }

    @Override
    public
    Long getLastInvoiceDate(Long id, String type){
        return this.baseMapper.getLastInvoiceDate(id,type);
    }
}
