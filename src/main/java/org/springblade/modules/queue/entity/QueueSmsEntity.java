package org.springblade.modules.queue.entity;

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
@TableName("blade_queue_sms")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "QueueSms对象", description = "")
public class QueueSmsEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号")
	private String mobile;
	/**
	 * 内容
	 */
	@ApiModelProperty(value = "内容")
	private String content;

}
