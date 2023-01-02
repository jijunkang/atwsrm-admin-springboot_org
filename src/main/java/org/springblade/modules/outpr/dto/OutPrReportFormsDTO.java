package org.springblade.modules.outpr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import sun.java2d.pipe.OutlineTextRenderer;

import java.util.Date;
import java.util.List;

/**
 * 模型DTO
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class OutPrReportFormsDTO extends OutPrReportFormsEntity {

	private static final long serialVersionUID = 1L;

	private String doCodes;

    private String itemCodes;

    private String checkStatus;

    private String supCode;

    private String supName;

    private String rcvCode;

    private String poCode;

    private String itemCode;

    private String itemName;

    private String moNo;

	private List<PoReceiveEntity> doList;

}
