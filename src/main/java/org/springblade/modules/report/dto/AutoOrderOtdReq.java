package org.springblade.modules.report.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.report.entity.AutoOrderOtdReport;
import org.springblade.modules.report.entity.OrderOtdReport;

@Data
@EqualsAndHashCode(callSuper = true)
public
class AutoOrderOtdReq extends AutoOrderOtdReport {

    private static final long serialVersionUID = 1L;

    private String approvedonStart;

    private String approvedonEnd;

    private String autoordertype;

    private String purchname;

    private String isstandard;




}
