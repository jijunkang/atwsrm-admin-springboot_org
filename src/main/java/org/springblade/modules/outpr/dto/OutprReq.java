package org.springblade.modules.outpr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.pr.entity.U9PrEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 请购单
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class OutprReq extends OutPrItemEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 多个状态用, 分割  50,30
	 */
	private String statuss;
    private Set<String> statusList;

	private String inquiryWays;

	private String purchCode;

	private String createTimeStart;
	private String createTimeEnd;

	private String source;
	private Integer isFlow;
    private Integer check;
    private String lastSupName;
    private Integer itemCodeCount;
}
