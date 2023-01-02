package org.springblade.modules.supplier.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 供应商考核Echarts 模型VO
 *
 * @author Will
 */
@Data
@ApiModel(value = "Echarts", description = "供应商考核Echarts")
public class OutPutEchrtsOfSupplierVO implements Serializable{
    private String[] dwlNum;//低温蜡产能
    private String[] zwlNum;//中温蜡产能
    private String[] szNum;//砂铸产能

    private String[] dwlGsNum;//低温蜡产能
    private String[] zwlGsNum;//中温蜡产能
    private String[] szGsNum;//砂铸产能

    private String[] dwlZyNum;//低温蜡产能占用
    private String[] zwlZyNum;//中温蜡产能占用
    private String[] szZyNum;//砂铸产能占用

    private String[] dwlZyGsNum;//低温蜡产能占用
    private String[] zwlZyGsNum;//中温蜡产能占用
    private String[] szZyGsNum;//砂铸产能占用

    private String totalCnNum;//产能总数量
    private String totalDwlCnNum;//低温蜡产能总数量
    private String totalZwlCnNum;//中温蜡产能总数量
    private String totalSzCnNum;//砂铸产能总数量

    private String totalGsNum;//工时总数量
    private String totalDwlGsNum;//低温蜡工时总数量
    private String totalZwlGsNum;//中温蜡工时总数量
    private String totalSzGsNum;//砂铸工时总数量

}
