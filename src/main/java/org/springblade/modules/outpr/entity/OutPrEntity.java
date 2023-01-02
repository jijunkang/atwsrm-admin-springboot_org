package org.springblade.modules.outpr.entity;

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
@TableName("atw_out_pr")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutPr对象", description = "")
public class OutPrEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 请购单号
	 */
	@ApiModelProperty(value = "请购单号")
	private String prCode;
	/**
	 * 请购日期
	 */
	@ApiModelProperty(value = "请购日期")
	private Long prDate;
	/**
	 * 询价日期
	 */
	@ApiModelProperty(value = "询价日期")
	private Long enquiryTime;
	/**
	 * 报文
	 */
	@ApiModelProperty(value = "报文")
	private String datagram;

}
