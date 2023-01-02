package org.springblade.modules.priceframe.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_price_frame")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PriceFrame对象", description = "")
public class PriceFrameEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 物料编号ID
	 */
	@ApiModelProperty(value = "物料编号ID")
	private Long itemId;
	/**
	 * 物料编码
	 */
	@ApiModelProperty(value = "物料编码")
	private String itemCode;
	/**
	 * 物料名称
	 */
	@ApiModelProperty(value = "物料名称")
	private String itemName;
	/**
	 * 供应商ID
	 */
	@ApiModelProperty(value = "供应商ID")
	private Long supId;
	/**
	 * 供应商编码
	 */
	@ApiModelProperty(value = "供应商编码")
	private String supCode;
	/**
	 * 供应商名称
	 */
	@ApiModelProperty(value = "供应商名称")
	private String supName;
	/**
	 * 采购最小值
	 */
	@ApiModelProperty(value = "采购最小值")
	private BigDecimal limitMin;
    /**
     * 采购最大值
     */
    @ApiModelProperty(value = "采购最大值")
    private BigDecimal limitMax;
	/**
	 * 单价
	 */
	@ApiModelProperty(value = "单价")
	private BigDecimal price;
	/**
	 * 计价单位
	 */
	@ApiModelProperty(value = "计价单位")
	private String     uom;
	/**
	 * 生效日期
	 */
	@ApiModelProperty(value = "生效日期")
	private Date       effectiveDate;
	/**
	 * 失效日期
	 */
	@ApiModelProperty(value = "失效日期")
	private Date       expirationDate;
	/**
	 * 提交采购员
	 */
	@ApiModelProperty(value = "提交采购员")
	private String     submitterCode;
	/**
	 * 附件
	 */
	@ApiModelProperty(value = "附件")
	private String attachment;

	@ApiModelProperty(value = "审核备注")
	private String checkRemark;

}
