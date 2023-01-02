package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_po_prono")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PoProno对象", description = "")
public class PoPronoEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 采购订单号
	 */
	@ApiModelProperty(value = "采购订单号")
	private String poCode;
	/**
	 * 采购订单行号
	 */
	@ApiModelProperty(value = "采购订单行号")
	private Integer poLn;


	/**
	 * 项目号
	 */
	@ApiModelProperty(value = "项目号")
	private String proNo;

	/**
	 * 运算交期
	 */
	@ApiModelProperty(value = "运算交期")
	private Long operationDate;

	/**
	 * 数量
	 */
	@ApiModelProperty(value = "数量")
	private Integer proNum;


}
