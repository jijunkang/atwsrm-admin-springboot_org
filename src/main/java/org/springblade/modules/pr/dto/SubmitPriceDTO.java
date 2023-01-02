package org.springblade.modules.pr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 请购单 模型DTO
 * @author Will
 */
@Data
public
class SubmitPriceDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "请购单id")
	private Long id;

    @ApiModelProperty(value = "io id")
    private Long ioId;

	@ApiModelProperty(value = "供应商编号")
	private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

	@ApiModelProperty(value = "承诺交期")
	private Date promiseDate;

	@ApiModelProperty(value = "是否赠品")
	private Integer isPersent;

	@ApiModelProperty(value = "价格")
	private BigDecimal quotePrice;

	@ApiModelProperty(value = "生效日期")
	private Date effectiveDate;

	@ApiModelProperty(value = "失效日期")
	private Date expirationDate;

	@ApiModelProperty(value = "附件")
	private String attachment;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "是否加入白名单")
	private Integer isIntoPriceLib;

    @ApiModelProperty(value = "提交或者暂存类型 flowSubmit-暂存 winBid-提交")
    private String type;

    @ApiModelProperty(value = "是否按重量计算")
    private Integer isByWeight;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    // 物料名称
    private String itemName;

    @ApiModelProperty(value = "是否需要审批：0：需要,1: 不需要")
    private String isNeedCheck;

}
