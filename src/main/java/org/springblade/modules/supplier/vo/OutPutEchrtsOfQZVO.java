package org.springblade.modules.supplier.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商考核Echarts 模型VO 球座
 *
 * @author Will
 */
@Data
@ApiModel(value = "Echarts", description = "供应商考核Echarts 球座")
public class OutPutEchrtsOfQZVO implements Serializable{
    private String[] lpbzje;//冷喷本周金额
    private String[] lppjje;//冷喷平均金额
    private String[] lpbzgs;//冷喷本周工时
    private String[] lppjgs;//冷喷本周工时

    private String[] rpbzje;//热喷本周金额
    private String[] rppjje;//热喷平均金额
    private String[] rpbzgs;//热喷本周工时
    private String[] rppjgs;//热喷本周工时

    private String totalLpZje;//冷喷总金额
    private String totalLpZgs;//冷喷总工时

    private String totalRpZje;//热喷总金额
    private String totalRpZgs;//热喷总工时
}
