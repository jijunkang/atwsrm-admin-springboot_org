package org.springblade.modules.pr.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.pr.entity.U9PrEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 请购单 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class U9PrVO extends U9PrEntity {

	private static final long serialVersionUID = 1L;

	private String priceNums;
    private Set<String> priceNumList;
    private String codeType;

    public Set<String> getPriceNumList(){
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(priceNums)){
            if(priceNums.contains(",")){
                String[] status =  priceNums.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(priceNums);
            }
        }
        return result;
    }
}
