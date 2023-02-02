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
public class OutPutEchrtsOfPtphVO implements Serializable{
    private String[] PtphNum;//喷涂喷焊产能
    private String[] PtphAvgNum;//喷涂喷焊周产能
    private String titleText;//标题

}
