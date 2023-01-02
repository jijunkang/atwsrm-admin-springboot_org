package org.springblade.modules.supplier.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.supplier.entity.Supplier;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 供应商考核Echarts 模型VO
 *
 * @author Will
 */
@Data
@ApiModel(value = "Echarts", description = "供应商考核Echarts")
public class OmsEchrtsOfSupplierVO implements Serializable{
    private String[] monthList;
    private String[] needNum;
    private String[] orderNum;
    private String[] deliveryNum;
    private String[] preNum;
    private String[] undeliveredNum;

    private String[] otd;
    private String[] otdSeven;

    private String totalUndeliveredNum;



    private List<String> threeMonthOtd;

    private Map<String,List<String>> lineEChartsData;
}
