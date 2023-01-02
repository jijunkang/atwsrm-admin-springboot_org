package org.springblade.modules.mathmodel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_mm_process_fee")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MmProcessFee对象", description = "")
public class MmProcessFeeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

    /**
     * 主分类
     */
    @ApiModelProperty(value = "主分类")
    private String mainCode;
    /**
     * 子分类
     */
    @ApiModelProperty(value = "子分类")
    private String childCode;
    /**
     * 物料编号
     */
    @ApiModelProperty(value = "物料编号")
    private String itemCode;
    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    private String itemName;
    /**
     * 材质
     */
    @ApiModelProperty(value = "材质")
    private String metal;
    /**
     * 加工费
     */
    @ApiModelProperty(value = "加工费")
    private BigDecimal price;
    /**
     * 历史记录ID
     */
    @ApiModelProperty(value = "历史记录ID")
    private Long historyId;
    private Date deletedTime;
}
