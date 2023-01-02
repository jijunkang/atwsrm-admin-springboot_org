package org.springblade.modules.item.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author libin
 * @date 15:27 2020/9/21
 **/
@Data
public class ItemExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "主分类")
    private String mainName;

    @Excel(name = "物料编码")
    private String code;

    @Excel(name = "物料名称")
    private String name;

    @Excel(name = "采购单位")
    private String tcUom;

    @Excel(name = "计价单位")
    private String priceUom;

    @Excel(name = "采购倍量")
    private BigDecimal purchMultiple;

    @Excel(name = "最小起订量")
    private BigDecimal purchMix;

    @Excel(name = "采购预处理提前期")
    private Integer purchDisposeDate;

    @Excel(name = "采购处理提前期")
    private Integer purchBeforeDate;

    @Excel(name = "采购后提前期")
    private Integer purchAfterDate;

    @Excel(name = "安全库存量")
    private BigDecimal stockLowerLimit;

    @Excel(name = "价格属性")
    private String purchAttr;

    @Excel(name = "ABC分类")
    private String SORN;

    @Excel(name = "是否标准件")
    private String isStandard;

}
