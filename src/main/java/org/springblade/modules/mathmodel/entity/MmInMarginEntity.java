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
@TableName("atw_mm_in_margin")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MmInMargin对象", description = "")
public class MmInMarginEntity extends BaseEntity {

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
     * 内圆(小)
     */
    @ApiModelProperty(value = "内圆(小)")
    private BigDecimal inSmall;

    /**
     * 内圆(大)
     */
    @ApiModelProperty(value = "内圆(大)")
    private BigDecimal inBig;

    /**
     * 高度(小)
     */
    @ApiModelProperty(value = "高度(小)")
    private BigDecimal hightSmall;

    /**
     * 高度(大)
     */
    @ApiModelProperty(value = "高度(大)")
    private BigDecimal hightBig;

    /**
     * 余量
     */
    @ApiModelProperty(value = "余量")
    private BigDecimal margin;
    /**
     * 历史记录ID
     */
    @ApiModelProperty(value = "历史记录ID")
    private Long historyId;
    private Date deletedTime;
}
