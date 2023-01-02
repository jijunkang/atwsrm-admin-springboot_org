package org.springblade.modules.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.ap.entity.ApRcvEntity;

import java.math.BigDecimal;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApRcvVO extends ApRcvEntity {

    private static final long serialVersionUID = 1L;

    /**
     * apItemId
     */
    @ApiModelProperty(value = "apItemId")
    private String apItemId;
    /**
     * 本次对账数量
     */
    @ApiModelProperty(value = "本次对账数量")
    private BigDecimal recThisQty;
    /**
     * 税额
     */
    @ApiModelProperty(value = "税额")
    private BigDecimal tax;
    /**
     * 预付冲应付金额
     */
    @ApiModelProperty(value = "预付冲应付金额")
    private BigDecimal pipAmount;
    /**
     * 请购金额
     */
    @ApiModelProperty(value = "请购金额")
    private BigDecimal purAmount;

    /**
     * 付款方式
     */
    @ApiModelProperty(value = "付款方式")
    private String payWay;
    /**
     * 账期
     */
    @ApiModelProperty(value = "账期")
    private Integer payDate;

    private String reqId;

    private String reqRcvCode;

    private String reqRcvLn;

    private String reqPoCode;

    private String reqPoLn;

    private String reqRcvNum;

    private String vmiContractNew;

    private String vmiStatusNew;

    private String reqAccumRecQty;

}
