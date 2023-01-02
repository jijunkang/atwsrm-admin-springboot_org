package org.springblade.modules.outpr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_out_sup_item_process_price")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutSupItemProcessPrice对象", description = "")
public class OutSupItemProcessPriceEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "请购单-物料id")
	private Long prItemId;

	@ApiModelProperty(value = "委外物料报价id")
	private Long itemPriceId;
	/**
	 * 请购单行号
	 */
	@ApiModelProperty(value = "请购单行号")
	private Integer prLn;
	/**
	 * 工序代码
	 */
	@ApiModelProperty(value = "工序代码")
	private String processCode;
	/**
	 * 工序名称
	 */
	@ApiModelProperty(value = "工序名称")
	private String     processName;
	/**
	 * 标准价格
	 */
	@ApiModelProperty(value = "标准价格")
	private BigDecimal price;
	/**
	 * 工序准备工时费用 
	 */
	@ApiModelProperty(value = "工序准备工时费用 ")
	private BigDecimal firstSetupCost;
	/**
	 * 设备型号
	 */
	@ApiModelProperty(value = "设备型号")
	private String machineModel;
	/**
	 * 设备品牌
	 */
	@ApiModelProperty(value = "设备品牌")
	private String machineBrand;

}
