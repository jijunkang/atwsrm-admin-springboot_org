package org.springblade.modules.outpr.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 模型VO
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class OutPrItemVO extends OutPrItemEntity{

    private static final long serialVersionUID = 1L;

    private List<OutPrItemProcessEntity> processList;

    @ApiModelProperty(value = "工序")
    private String processCode;

    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;

    @ApiModelProperty(value = "发出去的io总数")
    private Integer ioTotal;

    @ApiModelProperty(value = "已报价的io总数")
    private Integer quotedIoTotal;

    @ApiModelProperty(value = "历史最高价")
    private BigDecimal highestPrice;

    @ApiModelProperty(value = "历史最低价")
    private BigDecimal lowestPrice;

    @ApiModelProperty(value = "最近价")
    private BigDecimal lastPrice;

    @ApiModelProperty(value = "报价日期")
    private Long quoteDate;

    @ApiModelProperty(value = "承诺交期")
    private Long promiseDate;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "报价")
    private BigDecimal quotePrice;

    @ApiModelProperty(value = "供应商备注")
    private String supRemark;

    @ApiModelProperty(value = "是否是赠品")
    private Integer isPersent;

    @ApiModelProperty(value = "数学模型参考价")
    private BigDecimal referencePrice;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "IOID")
    private Long ioId;

    @ApiModelProperty(value = "io的附件")
    private String ioAttachment;

    @ApiModelProperty(value = "io的状态")
    private String ioStatus;

    @ApiModelProperty(value = "标准采购成本")
    private BigDecimal standardPrice;

    @ApiModelProperty(value = "是否是vmi")
    private String isVmi;

    @ApiModelProperty(value = "标准采购成本")
    private String codeType;

}
