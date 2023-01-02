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
@TableName("blade_queue_email")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "QueueEmail对象", description = "")
public class QueueEmailEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 发件人
	 */
	@ApiModelProperty(value = "发件人")
	private String sender;
	/**
	 * 收件人
	 */
	@ApiModelProperty(value = "收件人")
	private String receiver;
	/**
	 * 主题
	 */
	@ApiModelProperty(value = "主题")
	private String subject;
	/**
	 * 内容
	 */
	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty
	private Integer sendCount;

}
