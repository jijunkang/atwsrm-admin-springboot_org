package org.springblade.modules.mathmodel.vo;

import lombok.Data;
import org.springblade.modules.mathmodel.entity.MmVolumeCalculateEntity;

import java.math.BigDecimal;


/**
 * @author libin
 *
 * @date 14:27 2020/9/14
 **/
@Data
public class MmVolumeCalculateVO extends MmVolumeCalculateEntity {


    private BigDecimal priceNum;

}
