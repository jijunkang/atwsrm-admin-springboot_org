package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.Lists;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.outpr.dto.OutSupPreOrderDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.outpr.vo.OutSupPreOrderVO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 服务类
 * @author Will
 */
public
interface IOutSupPreOrderService extends BaseService<OutSupPreOrderEntity>{

    Integer STATUS_INIT        = 10; //待接单
    Integer STATUS_SUPACCEPT   = 20; //供应商接单
    Integer STATUS_SUPUNACCEPT = 30; //供应商拒绝接单
    Integer STATUS_CHECK1      = 40; //一级审核通过
    Integer STATUS_CHECK2      = 41; //一级审核已阅
    Integer STATUS_CHECKREJECT = 50; //拒绝
    Integer STATUS_ACCORD      = 60; //待下单
    Integer STATUS_ORDER       = 70; //已下单

    //询价方式
    String INQUIRYWAY_PRICELIB = "price_lib";           //  '白名单',

    IPage<OutSupPreOrderVO> toConfirmVoPage(IPage<OutSupPreOrderEntity> page, QueryWrapper<OutSupPreOrderEntity> queryWrapper);
    /**
     * 白名单交期确认数量
     * @return
     */
    int toConfirmCount();

    IPage<OutSupPreOrderVO> voPage(IPage<OutSupPreOrderEntity> page, QueryWrapper<OutSupPreOrderEntity> queryWrapper);

    List<Map<String, Object>> centerCount();

    int getCount();

    @Transactional
    boolean check(CheckDTO checkDto);

    @Transactional
    boolean check(List<CheckDTO> checkDtos);

    OutSupPreOrderEntity getByItemPriceId(Long itemPriceId);

    BigDecimal comTaxPrice(OutPrItemEntity item, OutSupItemPriceEntity itemPrice);

    boolean assignSup(OutSupPreOrderEntity preOrder);

    QueryWrapper<OutSupPreOrderEntity> getQueryWrapper(OutSupPreOrderDTO outsuppreorder);
}
