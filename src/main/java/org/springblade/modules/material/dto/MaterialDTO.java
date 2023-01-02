package org.springblade.modules.material.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.material.entity.MaterialEntity;

import java.math.BigDecimal;

/**
 * 原材料尺寸表 模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialDTO extends MaterialEntity {

	private static final long serialVersionUID = 1L;
    /**
     * 体积
     */
    @ApiModelProperty(value = "体积")
    private BigDecimal volume;


    public BigDecimal  getVolume(){
        //todo
        return this.volume;
    }

}
