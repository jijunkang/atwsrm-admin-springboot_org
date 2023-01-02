package org.springblade.modules.outpr.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.mapper.OutPrItemProcessMapper;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class OutPrItemProcessServiceImpl extends BaseServiceImpl<OutPrItemProcessMapper, OutPrItemProcessEntity> implements IOutPrItemProcessService {


    @Override public
    List<OutPrItemProcessEntity> getListByItemId(Long prItemId){
        OutPrItemProcessEntity queryEntity = new OutPrItemProcessEntity();
        queryEntity.setPrItemId(prItemId);
        return list(Condition.getQueryWrapper(queryEntity).orderBy(true,true,"pr_ln"));
    }


    @Override
    public IPage<OutPrItemProcessEntity> getPageByItemId(Query query, Long prItemId) {
        OutPrItemProcessEntity queryEntity = new OutPrItemProcessEntity();
        queryEntity.setPrItemId(prItemId);
        query.setAscs("pr_ln");
        return page(Condition.getPage(query),Condition.getQueryWrapper(queryEntity));
    }

    @Override public
    OutPrItemProcessEntity getByProcessCode(Long prItemId, String processCode){
        OutPrItemProcessEntity queryEntity = new OutPrItemProcessEntity();
        queryEntity.setPrItemId(prItemId);
        queryEntity.setProcessCode(processCode);
        return getOne(Condition.getQueryWrapper(queryEntity));
    }
}
