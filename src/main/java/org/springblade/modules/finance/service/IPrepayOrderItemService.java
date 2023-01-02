package org.springblade.modules.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.finance.dto.PrepayOrderItemDTO;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.vo.PrepayOrderItemVO;
import org.springblade.modules.po.entity.PoEntity;

import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IPrepayOrderItemService extends BaseService<PrepayOrderItemEntity> {

    PrepayOrderItemEntity createFirst(PrepayOrderEntity entity, PoEntity po);

    IPage<PrepayOrderItemVO> pageVo(PrepayOrderItemDTO prepayOrderItem, Query query);

    List<PrepayOrderItemEntity> listByPreOrderId(Long id);

    List<PrepayOrderItemEntity> getByPoCode(String poCode);

    String getPoCodesByPayId(Long prepayId);
}
