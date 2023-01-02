package org.springblade.modules.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.finance.dto.PrepayOrderItemDTO;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.mapper.PrepayOrderItemMapper;
import org.springblade.modules.finance.service.IPrepayOrderItemService;
import org.springblade.modules.finance.vo.PrepayOrderItemVO;
import org.springblade.modules.finance.wrapper.PrepayOrderItemWrapper;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.service.IPoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class PrepayOrderItemServiceImpl extends BaseServiceImpl<PrepayOrderItemMapper, PrepayOrderItemEntity> implements IPrepayOrderItemService {

    @Autowired
    @Lazy
    IPoService poService;

    public
    QueryWrapper<PrepayOrderItemEntity>  getQueryWrapper(PrepayOrderItemDTO prepayOrderItem){
        // todo
        QueryWrapper<PrepayOrderItemEntity> queryWrapper = Wrappers.<PrepayOrderItemEntity>query()
                .like(StringUtil.isNotBlank(prepayOrderItem.getPrepayCode()), "prepay_code", prepayOrderItem.getPrepayCode())
                .eq(prepayOrderItem.getPrepayId() != null, "prepay_id", prepayOrderItem.getPrepayId());
        return queryWrapper;
    };


    @Override
    public
    PrepayOrderItemEntity createFirst(PrepayOrderEntity prepayOrder, PoEntity po){
        PrepayOrderItemEntity entity = new PrepayOrderItemEntity();
        entity.setPrepayId(prepayOrder.getId());
        entity.setPrepayCode(prepayOrder.getCode());
        entity.setPrepayLn(1);
        entity.setIsPoFirst(1);
        entity.setPrepayRate(po.getFirstPrepayRate());
        entity.setPoId(po.getId());
        entity.setPoCode(po.getOrderCode());
        entity.setSubtotal(po.getDocAmount().multiply(BigDecimal.valueOf(po.getFirstPrepayRate())));
        entity.setSysLog("首笔预付请款");
        save(entity);
        po.setPrepaidTotal( entity.getSubtotal());
        poService.updateById(po);
        return entity;
    }

    @Override
    public
    IPage<PrepayOrderItemVO> pageVo(PrepayOrderItemDTO prepayOrderItem, Query query){
        QueryWrapper<PrepayOrderItemEntity> qw         = getQueryWrapper(prepayOrderItem);
        IPage<PrepayOrderItemEntity>        entityPage = page(Condition.getPage(query), qw);
        IPage<PrepayOrderItemVO>            voPage     = new Page<PrepayOrderItemVO>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        if(voPage.getTotal() == 0){
            return voPage;
        }
        voPage.setRecords(PrepayOrderItemWrapper.build().listVO(entityPage.getRecords()));
        for(PrepayOrderItemVO vo : voPage.getRecords() ){
            PoEntity po = poService.getById(vo.getPoId());
            if(po == null){
               continue;
            }
            vo.setPayWay(po.getPayWay());
            vo.setPrepaidTotal(po.getPrepaidTotal());
            vo.setAccpaidTotal(po.getAccpaidTotal());
            vo.setAccumPrepay(po.getAccumPrepay());
            vo.setDocDate(po.getDocDate());
            vo.setDocAmount(po.getDocAmount());
        }
        return voPage;
    }

    @Override
    public
    List<PrepayOrderItemEntity> listByPreOrderId(Long id){
        QueryWrapper<PrepayOrderItemEntity> queryWrapper = Wrappers.<PrepayOrderItemEntity>query().eq("prepay_id", id);
        return list(queryWrapper);
    }

    @Override
    public List<PrepayOrderItemEntity> getByPoCode(String poCode) {
        QueryWrapper<PrepayOrderItemEntity> queryWrapper = Condition.getQueryWrapper(new PrepayOrderItemEntity());
        queryWrapper.eq("po_code", poCode);
        return list(queryWrapper);
    }

    @Override
    public String getPoCodesByPayId(Long prepayId) {
        return this.baseMapper.getPoCodesByPayId(prepayId);
    }
}
