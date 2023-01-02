package org.springblade.modules.report.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.report.entity.ItemDailyReport;

@Data
@EqualsAndHashCode(callSuper = true)
public
class KeyItemReportReq extends ItemDailyReport {

	private static final long serialVersionUID = 1L;

    private String planDateStart;
    private String planDateEnd;

    private String pointedReqDate;
}
