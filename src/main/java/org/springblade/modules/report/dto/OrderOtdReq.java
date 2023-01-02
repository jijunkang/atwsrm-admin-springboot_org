package org.springblade.modules.report.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.report.entity.OrderOtdReport;
import org.springblade.modules.report.entity.QZReport;

@Data
@EqualsAndHashCode(callSuper = true)
public
class OrderOtdReq extends OrderOtdReport {

    private static final long serialVersionUID = 1L;

    private String poCheckDateStart;

    private String poCheckDateEnd;
}
