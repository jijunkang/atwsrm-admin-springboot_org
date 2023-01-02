package org.springblade.modules.report.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.report.entity.DeliverReport;
import org.springblade.modules.report.entity.VmiReport;

@Data
@EqualsAndHashCode(callSuper = true)
public
class DeliverReportReq extends DeliverReport {

	private static final long serialVersionUID = 1L;

}
