package org.springblade.modules.po.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;

import java.math.BigDecimal;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutIoDTO extends OutIoEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 请购日期
	 */
	@ApiModelProperty(value = "请购日期")
	private Integer prDate;

	@ApiModelProperty(value = "历史最高价")
	private BigDecimal highestPrice ;

	@ApiModelProperty(value = "历史最低价")
	private BigDecimal lowestPrice;

	@ApiModelProperty(value = "最近价")
	private BigDecimal lastPrice;

	@ApiModelProperty(value = "标准交期")
	private Integer standardDate;

	@ApiModelProperty(value = "标准价格")
	private BigDecimal standardPrice;

	@ApiModelProperty(value = "采购员工号")
	private String purchCode;

	@ApiModelProperty(value = "采购员名称")
	private String purchName;

	@ApiModelProperty(value = "下单员编号")
	private String     placeCode;

	@ApiModelProperty(value = "下单员名称")
	private String     placeName;

	@ApiModelProperty(value = "流标原因")
    private String flowType;

    @ApiModelProperty(value = "数学模型参考价")
    private BigDecimal referencePrice;

    @ApiModelProperty(value = "可用量")
    private BigDecimal availableQuantity;

    @ApiModelProperty(value = "项目占用量")
    private BigDecimal projectOccupancyNum;

    @ApiModelProperty(value = "请购单备注")
    private String requisitionRemark;

    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;
}
