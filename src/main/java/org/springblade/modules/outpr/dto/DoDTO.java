package org.springblade.modules.outpr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;

import java.io.Serializable;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class DoDTO  implements Serializable {

	private static final long serialVersionUID = 1L;

	private String rcvCode;
    private Integer rcvNum;
    private String heatCode;
    private Long produceDate;
    private String specs;
    private String matQuality;
    private String poCode;
    private String poLn;
    private String itemCode;
    private String itemName;
    private String remark;
    private String supCode;
    private String supName;
    private String priceUom;
    private String moNo;
}
