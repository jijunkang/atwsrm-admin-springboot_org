package org.springblade.modules.outpr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.outpr.entity.OutPrItemArtifactEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutPrItemArtifactVO extends OutPrItemArtifactEntity {

	private static final long serialVersionUID = 1L;

	private String statuss;
    private Set<String> statusList;

    @ApiModelProperty(value = "历史最高价")
    private BigDecimal highestPrice;

    @ApiModelProperty(value = "历史最低价")
    private BigDecimal lowestPrice;

    @ApiModelProperty(value = "最近价")
    private BigDecimal lastPrice;

    @ApiModelProperty(value = "可用量")
    private BigDecimal availableQuantity;

    @ApiModelProperty(value = "项目占用量")
    private BigDecimal projectOccupancyNum;

    @ApiModelProperty(value = "请购单备注")
    private String requisitionRemark;

    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;

    public Set<String> getStatusList(){
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(statuss)){
            if(statuss.contains(",")){
                String[] status =  statuss.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(statuss);
            }
        }
        return result;
    }
}
