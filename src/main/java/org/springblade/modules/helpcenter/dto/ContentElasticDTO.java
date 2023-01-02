package org.springblade.modules.helpcenter.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yh
 */
@Data
public class ContentElasticDTO {

	private Long id;

	/**
	 * 分类id
	 */
	@NotNull
	@ApiModelProperty(value = "分类id")
	private Long classId;

	@ApiModelProperty(value = "code")
	private String code;

	@ApiModelProperty(value = "分类名称")
	private String className;
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


