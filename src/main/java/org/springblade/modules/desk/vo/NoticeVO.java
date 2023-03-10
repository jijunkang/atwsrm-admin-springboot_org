package org.springblade.modules.desk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.desk.entity.Notice;

/**
 * 通知公告视图类
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeVO extends Notice {

	@ApiModelProperty(value = "通知类型名")
	private String categoryName;

	@ApiModelProperty(value = "租户编号")
	private String tenantId;

}
