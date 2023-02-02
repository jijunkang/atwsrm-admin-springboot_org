package org.springblade.modules.supplier.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商考核Echarts 模型VO
 *
 * @author Will
 */
@Data
@ApiModel(value = "Echarts", description = "供应商考核Echarts")
public class OutPutEchrtsOfDjVO implements Serializable{
    private String[] DjNum1;//锻件产能
    private String[] DjNum2;//锻件产能
    private String[] DjNum3;//锻件产能
    private String[] DjNum4;//锻件产能
    private String[] DjNum5;//锻件产能
    private String[] DjNum6;//锻件产能
    private String[] DjNum7;//锻件产能
    private Double[] DjAvgNum1;//锻件周产能
    private Double[] DjAvgNum2;//锻件周产能
    private Double[] DjAvgNum3;//锻件周产能
    private Double[] DjAvgNum4;//锻件周产能
    private Double[] DjAvgNum5;//锻件周产能
    private Double[] DjAvgNum6;//锻件周产能
    private Double[] DjAvgNum7;//锻件周产能

}
