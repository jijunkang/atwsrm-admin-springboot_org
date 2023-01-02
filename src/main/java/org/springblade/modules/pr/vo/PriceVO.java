package org.springblade.modules.pr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author libin
 *
 * @date 16:34 2020/9/25
 **/
@Data
public class PriceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "供应商默认值")
    private String supKey;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "单重")
    private String singleWeight ;

    @ApiModelProperty(value = "单价")
    private String singlePrice;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "数量区间")
    private String numberInterval;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "等级")
    private String grade;

    // 新内径余量
    private String newInnerRemain;

    private String outerRemain;

    private String innerRemain;

    private String heightRemain;

}
