package org.springblade.modules.pr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springblade.core.mp.base.BaseEntity;

/**
 * Author: 昕月
 * Date：2022/6/21 9:06
 * Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class U9DjPriceDTO extends BaseEntity {

    private Double totalHeightSize; // 总高度
    private Double bigHeightSize; // 大头高度
    private Double bigOuterSize; // 大头外径
    private Double smallOuterSize; // 小头外径
    private Double outerSize; // 锻件外圆
    private Double weightOfBasic;
    private Double heightSize; //锻件高度
    private Double innerSize; // 锻件内径
    private Double innerRemain; // 内径余量
    private Double heightRemain; // 高度余量
    private Double outerRemain; // 外径余量
    private Double newInner;  // 新内径
    private Double newInnerRemain; // 新内径余量
    private String material; // 材质
}
