package org.springblade.modules.outpr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;

import java.util.List;

/**
 * 模型Req
 * @author Will
 */
@Data
public class OutPrReportFormsReq {
	private String doCodes;

    private String checkStatus;

    private String supCode;

    private String supName;

    private String rcvCode;

	private List<PoReceiveEntity> poReceiveEntities;
}
