package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;

/**
 * Author: 昕月
 * Date：2022/5/13 19:04
 * Desc:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "atw_maily_processing_fee")
@ApiModel(value = "加工费" ,description = "")
public class ItemInfoProcessingfeiMaily extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "序号id")
    private Long id;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @ApiModelProperty(value ="外径" )
    private int externalDiameter;

    @ApiModelProperty(value ="内径" )
    private int internalDiamete;

    @ApiModelProperty(value = "长度")
    private int length;

    @ApiModelProperty(value = "加工费")
    private int processingFee;

}
