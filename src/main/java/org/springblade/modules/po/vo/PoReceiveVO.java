package org.springblade.modules.po.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoReceiveEntity;

import java.math.BigDecimal;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoReceiveVO extends PoReceiveEntity {

	private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "采购订单号")
    private String     poCode;

    @ApiModelProperty(value = "采购订单行号")
    private Integer    poLn;

    @ApiModelProperty(value = "料品号")
    private String     itemCode;

    @ApiModelProperty(value = "料品描述")
    private String     itemName;

    @ApiModelProperty(value = "未到货数量")
    private BigDecimal proGoodsNum;

    @ApiModelProperty(value = "计量单位")
    private String     priceUom;

    @ApiModelProperty(value = "采购单位（php 叫 交易单位）")
    private String tcUom;

    @ApiModelProperty(value = "创建人")
    private String creater;

    @ApiModelProperty(value = "更新人")
    private String updater;

    @ApiModelProperty(value = "未送货数量")
    private BigDecimal notSendNum;

    private String templateType;
}
