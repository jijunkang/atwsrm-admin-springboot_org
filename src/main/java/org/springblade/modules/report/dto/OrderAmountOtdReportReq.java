package org.springblade.modules.report.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.report.entity.AutoOrderOtdReport;
import org.springblade.modules.report.entity.OrderAmountOtdReport;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderAmountOtdReportReq extends OrderAmountOtdReport {

    private static final long serialVersionUID = 1L;

    private String creatimeStart;

    private String creatimeEnd;

    private String supName;




}
