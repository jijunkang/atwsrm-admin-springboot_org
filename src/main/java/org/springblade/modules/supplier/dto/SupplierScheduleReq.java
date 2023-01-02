package org.springblade.modules.supplier.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.supplier.entity.SupplierSchedule;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public
class SupplierScheduleReq extends SupplierSchedule {

	private static final long serialVersionUID = 1L;

	private String statuss;

	private List<SupplierSchedule> scheduleList;

    private String supCode;

	private String year;

    private String itemType;


    private String otdType;
    private String dateType;
    private String date;
    private String seriesName;
    private String gy;
}
