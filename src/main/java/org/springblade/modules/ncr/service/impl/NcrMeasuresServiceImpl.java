package org.springblade.modules.ncr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ncr.entity.NcrMeasuresEntity;
import org.springblade.modules.ncr.mapper.NcrMeasuresMapper;
import org.springblade.modules.ncr.service.INcrMeasuresService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author libin
 *
 * @date 18:00 2020/8/4
 **/
@Service
public class NcrMeasuresServiceImpl extends BaseServiceImpl<NcrMeasuresMapper, NcrMeasuresEntity> implements INcrMeasuresService  {

    @Override
    public List<NcrMeasuresEntity> getList(NcrMeasuresEntity entity) {
        QueryWrapper<NcrMeasuresEntity> queryWrapper = Condition.getQueryWrapper(new NcrMeasuresEntity());
        if(StringUtil.isNotBlank(entity.getType())){
            queryWrapper.eq("type", entity.getType());
        }
        if(StringUtil.isNotBlank(entity.getDeptType())){
            queryWrapper.eq("dept_type", entity.getDeptType());
        }
        if(!StringUtil.isEmpty(entity.getNcrId())){
            queryWrapper.eq("ncr_id", entity.getNcrId());
        }
        queryWrapper.orderByAsc("create_time");
        return list(queryWrapper);
    }


}
