package org.springblade.modules.helpcenter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import javax.validation.constraints.NotNull;


/**
 * 帮助内容 实体类
 *
 * @author Will
 */
@Data
@TableName("support_content")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Content对象", description = "帮助内容")
public class ContentEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@NotNull
    @JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "分类id")
	private Long classId;
	/**
	 * 标题
	 */
	@ApiModelProperty(value = "标题")
	private String title;
	/**
	 * 路径
	 */
	@ApiModelProperty(value = "路径")
	private String path;

	/**
	 * 路径名称
	 */
	@ApiModelProperty(value = "路径名称")
	private String pathName;
	/**
	 * 内容
	 */
	@ApiModelProperty(value = "内容")
	private String content;
	/**
	 * 视频
	 */
	@ApiModelProperty(value = "视频")
	private String video;

}
