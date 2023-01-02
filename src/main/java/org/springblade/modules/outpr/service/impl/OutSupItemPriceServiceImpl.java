package org.springblade.modules.outpr.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.outpr.mapper.OutSupItemPriceMapper;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.service.IOutSupItemPriceService;
import org.springblade.modules.outpr.service.IOutSupItemProcessPriceService;
import org.springblade.modules.outpr.service.IOutSupPreOrderService;
import org.springblade.modules.outpr.vo.OutSupItemPriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class OutSupItemPriceServiceImpl extends BaseServiceImpl<OutSupItemPriceMapper, OutSupItemPriceEntity> implements IOutSupItemPriceService{

    @Autowired
    @Lazy
    IOutPrItemService prItemService;

    @Autowired
    @Lazy
    IOutSupItemProcessPriceService processPriceService;

    @Autowired
    @Lazy
    IOutSupPreOrderService preOrderService;

    @Autowired
    @Lazy
    IMmVolumeCalculateService mmVolumeCalculateService;

    /**
     * 寻找‘下一个’供应商
     * @return
     */
    @Override public
    OutSupItemPriceEntity findNextSup(Long itemPriceId){
        OutSupItemPriceEntity dbEntity = getById(itemPriceId);
        if(dbEntity == null){
            return null;
        }
        OutSupItemPriceEntity queryEntity = new OutSupItemPriceEntity();
        Wrapper<OutSupItemPriceEntity> queryWraper = Condition.getQueryWrapper(queryEntity)
                .eq("pr_item_id", dbEntity.getPrItemId())
                .gt("bid_seq", dbEntity.getBidSeq())
                .orderByAsc("bid_seq");
        List<OutSupItemPriceEntity> list = list(queryWraper);
        if(list == null || list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    /**
     * 价格像列表
     * @return
     */
    @Override public
    List<OutSupItemPriceVO> getProcessPriceList(QueryWrapper<OutSupItemPriceEntity> queryWrapper){
        List<OutSupItemPriceEntity> entities = list(queryWrapper);
        List<OutSupItemPriceVO>     voList   = BeanUtil.copy(entities, OutSupItemPriceVO.class);
        for(OutSupItemPriceVO vo : voList){
            OutPrItemEntity prItem = prItemService.getById(vo.getPrItemId());
            vo.setAtwPrice(prItem.getAtwPrice());
            vo.setMaterialCost(prItem.getMaterialCost());

            OutSupPreOrderEntity preOrder = preOrderService.getByItemPriceId(vo.getId());
            if(preOrder!=null){
                vo.setSupDeliveryTime(preOrder.getSupDeliveryTime());
                vo.setSupStatus(preOrder.getSupStatus());
                vo.setSupRemark(preOrder.getRemark());
                vo.setStatus(preOrder.getStatus());
            }

            OutSupItemProcessPriceEntity queryEntity = new OutSupItemProcessPriceEntity();
            queryEntity.setItemPriceId(vo.getId());
            vo.setProcessPriceList(processPriceService.listVo(Condition.getQueryWrapper(queryEntity)));

            //数学模型参考价
            vo.setReferencePrice(mmVolumeCalculateService.getPrice(prItem.getItemCode(), vo.getSupCode()));
        }
        return voList;
    }
}
