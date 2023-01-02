package org.springblade.modules.outpr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.mapper.OutSupItemProcessPriceMapper;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springblade.modules.outpr.service.IOutSupItemProcessPriceService;
import org.springblade.modules.outpr.vo.OutSupItemProcessPriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class OutSupItemProcessPriceServiceImpl extends BaseServiceImpl<OutSupItemProcessPriceMapper, OutSupItemProcessPriceEntity> implements IOutSupItemProcessPriceService {

    @Autowired
    IOutPrItemProcessService processService;

    @Override public
    List<OutSupItemProcessPriceVO> listVo(QueryWrapper<OutSupItemProcessPriceEntity> queryWrapper){
        List<OutSupItemProcessPriceEntity>  list  = list(queryWrapper);
        List<OutSupItemProcessPriceVO> voList = BeanUtil.copy(list,OutSupItemProcessPriceVO.class);
        for(OutSupItemProcessPriceVO vo:  voList){
            OutPrItemProcessEntity process = processService.getByProcessCode(vo.getPrItemId(), vo.getProcessCode());
            vo.setAtwPrice(process.getAtwPrice());
        }
        return voList;
    }
}
