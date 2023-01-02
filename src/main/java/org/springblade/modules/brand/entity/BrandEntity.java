package org.springblade.modules.brand.entity;

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
@TableName("atw_brand")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Brand对象", description = "")
public class BrandEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	private String name;
	/**
	 * 是否生效
	 */
	@ApiModelProperty(value = "是否生效")
	private Integer isEnable;

}
