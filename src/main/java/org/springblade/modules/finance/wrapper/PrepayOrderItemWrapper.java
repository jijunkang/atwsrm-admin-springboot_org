package org.springblade.modules.finance.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.vo.PrepayOrderItemVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PrepayOrderItemWrapper extends BaseEntityWrapper<PrepayOrderItemEntity, PrepayOrderItemVO>  {

    public static PrepayOrderItemWrapper build() {
        return new PrepayOrderItemWrapper();
    }

    @Override
    public PrepayOrderItemVO entityVO(PrepayOrderItemEntity prepayOrderItem) {
        PrepayOrderItemVO prepayOrderItemVO = BeanUtil.copy(prepayOrderItem, PrepayOrderItemVO.class);

        return prepayOrderItemVO;
    }

}
