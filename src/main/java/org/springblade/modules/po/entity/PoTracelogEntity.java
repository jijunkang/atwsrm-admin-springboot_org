package org.springblade.modules.po.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
@TableName("atw_po_tracelog")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PoTracelog对象", description = "")
public class PoTracelogEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String poCode;
	/**
	 * 行号
	 */
	@ApiModelProperty(value = "行号")
	private Integer poLn;
	/**
	 * 项目号
	 */
	@ApiModelProperty(value = "项目号")
	private String proNo;
	/**
	 * 跟单内容
	 */
	@ApiModelProperty(value = "跟单内容")
	private String content;
	/**
	 * 采购员编号
	 */
	@ApiModelProperty(value = "采购员编号")
	private String purchCode;
	/**
	 * 采购员名称
	 */
	@ApiModelProperty(value = "采购员名称")
	private String purchName;

	@ApiModelProperty(value = "跟单员编号")
	private String     traceCode;

	@ApiModelProperty(value = "跟单员名称")
	private String     traceName;

}
