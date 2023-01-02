package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.vo.PoItemVO;

import java.util.Date;

/**
 * 采购订单明细 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoItemWrapper extends BaseEntityWrapper<PoItemEntity, PoItemVO> {

    public static PoItemWrapper build() {
        return new PoItemWrapper();
    }

    @Override
    public PoItemVO entityVO(PoItemEntity entity) {
        PoItemVO vo = BeanUtil.copy(entity, PoItemVO.class);
        vo.setReqDateFmt(entity.getReqDate() != null ? DateUtil.formatDate(new Date(entity.getReqDate() * 1000)) : "");
        vo.setSupConfirmDateFmt(entity.getSupConfirmDate() != null ? DateUtil.formatDate(new Date(entity.getSupConfirmDate() * 1000)) : "");
        vo.setSupUpdateDateFmt(entity.getSupUpdateDate() != null ? DateUtil.formatDate(new Date(entity.getSupUpdateDate() * 1000)) : "");
        vo.setFirstDeliveryDateFmt(entity.getFirstDeliveryDate() != null ? DateUtil.formatDate(new Date(entity.getFirstDeliveryDate() * 1000)) : "");
        vo.setSecondDeliveryDateFmt(entity.getSecondDeliveryDate() != null ? DateUtil.formatDate(new Date(entity.getSecondDeliveryDate() * 1000)) : "");
        vo.setThirdDeliveryDateFmt(entity.getThirdDeliveryDate() != null ? DateUtil.formatDate(new Date(entity.getThirdDeliveryDate() * 1000)) : "");
        vo.setIsDeliverablesFullFmt(entity.getIsDeliverablesFull() == 1 ? "是" : "否");
        return vo;
    }

}
