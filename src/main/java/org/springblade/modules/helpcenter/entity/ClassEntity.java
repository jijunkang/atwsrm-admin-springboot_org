package org.springblade.modules.helpcenter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 * 帮助分类 实体类
 *
 * @author Will
 */
@Data
@TableName("support_class")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Class对象", description = "帮助分类")
public class ClassEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "code")
	private String code;

	/**
	 * 父级id
	 */
	@ApiModelProperty(value = "父级id")
	private Long parentId;
	/**
	 * 分类名称
	 */
	@ApiModelProperty(value = "分类名称")
	private String className;
	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	private Integer sort;

}
