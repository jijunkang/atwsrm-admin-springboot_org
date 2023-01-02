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
@TableName("atw_po_remind_apply")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PoRemindApply对象", description = "")
public class PoRemindApplyEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * po_item_id
	 */
	@ApiModelProperty(value = "po_item_id")
	private Long poItemId;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

}
