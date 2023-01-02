package org.springblade.modules.outpr.vo;

import lombok.Data;
import org.springblade.modules.outpr.dto.DoDTO;

import java.io.Serializable;
import java.util.List;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class ParamsVO implements Serializable {

	private static final long serialVersionUID = 1L;

    private String rcv_code;
    private Integer rcv_num;
    private String heat_code;
    private Long produce_date;
    private String specs;
    private String mat_quality;
    private String po_code;
    private String po_ln;
    private String item_code;
    private String item_name;
    private String remark;
    private String sup_code;
    private String sup_name;
    private String price_uom;
}
