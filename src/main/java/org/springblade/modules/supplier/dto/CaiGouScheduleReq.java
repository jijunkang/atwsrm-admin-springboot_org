package org.springblade.modules.supplier.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.SupplierSchedule;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
public
class CaiGouScheduleReq extends CaiGouSchedule {

	private static final long serialVersionUID = 1L;

    private List<CaiGouSchedule> scheduleList;

    private String lackItemNums;

    private String doStatuss;
    private String isToSendStatus;

    private String itemCodeBatch;
    private String poCodeLnBatch;
    private String proNoBatch;

    private String overdue;
    private String itemType;

    private String planDateStart;
    private String planDateEnd;

    private String checkUpdateDateStart;
    private String checkUpdateDateEnd;

    private String pointedReqDate;

}
