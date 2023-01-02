package org.springblade.modules.pr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.pr.entity.U9PrEntityEx;
import org.springblade.modules.supplier.entity.CaiGouSchedule;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public
class U9PrExReq extends U9PrEntityEx {

	private static final long serialVersionUID = 1L;

    private List<U9PrEntityEx> u9prentityexlist;

    private String prCode;
    private String itemCode;
    private String itemName;
    private String subproject;
    private String isAps;
    private String prCreateUser;
    private String problemType;
    private String handleDept;
    private String dutyDept;
    private String proNeedDateStart;
    private String proNeedDateEnd;
    private String reqDateStart;
    private String reqDateEnd;
    private String planDateStart;
    private String planDateEnd;
    private String finishDateStart;
    private String finishDateEnd;

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
    private String supName;
    private Integer itemCodeCount;
    private Integer prCodeCount;
    private String isVmi;



}
