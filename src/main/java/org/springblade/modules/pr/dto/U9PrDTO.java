package org.springblade.modules.pr.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.pr.entity.U9PrEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请购单 模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class U9PrDTO extends U9PrEntity {

    private static final long serialVersionUID = 1L;

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
    @JsonSerialize(nullsUsing = NullSerializer.class)
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

    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;

    @ApiModelProperty(value = "标准采购成本")
    private BigDecimal standardPrice;

    @ApiModelProperty(value = "是否是vmi")
    private String isVmi;

    @ApiModelProperty(value = "标准采购成本")
    private String codeType;

    @ApiModelProperty(value = "pi上面的最近采购供应商")
    private String piLastSupName;

    @ApiModelProperty(value = "阀座对应信息")
    private String relationFzItemInfo;

    private String prCode;

    private String jobDate;

    @ApiModelProperty(value = "齐套项目号")
    private String qtProNo;

    private Date promiseDateFromQt;

    private Date qtPlanDate;

}
