package org.springblade.modules.pr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
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
class PrReq extends U9PrEntity{

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
    private String supName;
    private Integer itemCodeCount;
    private Integer prCodeCount;
    private String isVmi;
    private String orgCode;


    public Set<String> getStatusList(){
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(statuss)){
            if(statuss.contains(",")){
                String[] status =  statuss.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(statuss);
            }
        }
        return result;
    }
}
