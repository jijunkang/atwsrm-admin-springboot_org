package org.springblade.modules.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author libin
 *
 * @date 11:30 2020/9/21
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ItemMoreReq extends Item {

    private String selectType;

    private String mainNameType;
    private String codeType;
    private String nameType;
    private String sOrNType;
    private String sOrNs;

    private BigDecimal purchMultipleMin;
    private BigDecimal purchMultipleMax;
    private String purchMultipleType;

    private BigDecimal purchMixMin;
    private BigDecimal purchMixMax;
    private String purchMixType;

    private BigDecimal stockLowerLimitMin;
    private BigDecimal stockLowerLimitMax;
    private String stockLowerLimitType;

    private Integer purchBeforeDateMin;
    private Integer purchBeforeDateMax;
    private String purchBeforeDateType;

    private Integer purchAfterDateMin;
    private Integer purchAfterDateMax;
    private String purchAfterDateType;

    private Integer purchDisposeDateMin;
    private Integer purchDisposeDateMax;
    private String purchDisposeDateType;

    private String purchAttrType;
    private String purchAttrs;
    private Set<String> purchAttrList;

    private String purchCodeType;
    private String purchCodes;
    private Set<String> purchCodeList;

    private String placeCodeType;
    private String placeCodes;
    private Set<String> placeCodeList;

    private String traceCodeType;
    private String traceCodes;
    private Set<String> traceCodeList;


    public Set<String> getPurchAttrList() {
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(purchAttrs)){
            if(purchAttrs.contains(",")){
                String[] status =  purchAttrs.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(purchAttrs);
            }
        }
        return result;
    }

    public Set<String> getPurchCodeList() {
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(purchCodes)){
            if(purchCodes.contains(",")){
                String[] status =  purchCodes.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(purchCodes);
            }
        }
        return result;
    }

    public Set<String> getPlaceCodeList() {
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(placeCodes)){
            if(placeCodes.contains(",")){
                String[] status =  placeCodes.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(placeCodes);
            }
        }
        return result;
    }

    public Set<String> getTraceCodeList() {
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(traceCodes)){
            if(traceCodes.contains(",")){
                String[] status =  traceCodes.split(",");
                Collections.addAll(result, status);
            }else{
                result.add(traceCodes);
            }
        }
        return result;
    }
}
