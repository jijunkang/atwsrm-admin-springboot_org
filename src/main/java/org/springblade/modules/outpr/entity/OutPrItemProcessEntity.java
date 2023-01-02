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
@TableName("atw_out_pr_item_process")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutPrItemProcess对象", description = "")
public class OutPrItemProcessEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 委外物料id
	 */
	@ApiModelProperty(value = "委外物料id")
	private Long prItemId;
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
	 * 内部估价
	 */
	@ApiModelProperty(value = "内部估价")
	private BigDecimal atwPrice;
	/**
	 * u9状态码
	 */
	@ApiModelProperty(value = "u9状态码")
	private String     u9StatusCode;
	/**
	 * u9状态
	 */
	@ApiModelProperty(value = "u9状态")
	private String u9StatusText;

    /**
     * moNo
     */
    @ApiModelProperty(value = "moNo")
    private String moNo;

}
