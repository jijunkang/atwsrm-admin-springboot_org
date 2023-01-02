package org.springblade.modules.forecast.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;


/**
 * 情报 实体类
 *
 * @author Will
 */
@Data
@TableName("wxx_forecast")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Forecast对象", description = "情报")
public class ForecastEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 物料编号
	 */
	@ApiModelProperty(value = "物料编号")
	private String itemCode;
	/**
	 * 物料名称
	 */
	@ApiModelProperty(value = "物料名称")
	private String itemName;
	/**
	 * 标记
	 */
	@ApiModelProperty(value = "标记")
	private String mark;
	/**
	 * 预测年份
	 */
	@ApiModelProperty(value = "预测年份")
	private Integer foreYear;
	/**
	 * 预测月份
	 */
	@ApiModelProperty(value = "预测月份")
	private Integer foreMonth;
	/**
	 * 预测数量
	 */
	@ApiModelProperty(value = "预测数量")
	private Integer foreQty;
	/**
	 * 排序号
	 */
	@ApiModelProperty(value = "排序号")
	private Integer seq;

}
