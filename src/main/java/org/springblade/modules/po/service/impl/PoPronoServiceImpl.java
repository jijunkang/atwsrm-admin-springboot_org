package org.springblade.modules.po.service.impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.po.entity.PoPronoEntity;
import org.springblade.modules.po.mapper.PoPronoMapper;
import org.springblade.modules.po.service.IPoPronoService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class PoPronoServiceImpl extends BaseServiceImpl<PoPronoMapper, PoPronoEntity> implements IPoPronoService {

    @Override
    public boolean isMeetOptDate(String supCode, String itemCode, String compareDate) {
        Date optDate =  this.baseMapper.findOptDate(supCode, itemCode);
        Date comfDate =  this.baseMapper.findComfDate(supCode, itemCode);
        if(optDate == null){
            optDate = new Date();
        }
        return optDate.after(comfDate);
    }
}
