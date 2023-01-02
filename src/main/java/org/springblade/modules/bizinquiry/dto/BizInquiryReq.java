package org.springblade.modules.bizinquiry.dto;

import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author libin
 *
 * @date 11:39 2020/6/18
 **/
@Data
public class BizInquiryReq {

    private Long id;
    private Long qoId;
    private Long winioId;
    private Integer status;
    private String qoCode;
    private String model;
    private String brand;
    private String statuss;
    private Set<String> statusList;
    private String supName;
    private BigDecimal price;
    private Integer confirmDate;
    private String remark;
    private String attachment;
    private String ids;
    private Set<String> idList;
    private String supFeedback;
    private String endUser;
    private List<BizInquiryEntity> bizInquiryEntities;
    private List<BizInquiryIoEntity> bizInquiryIoEntities;


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

    public Set<String> getIdList(){
        Set<String> result = new HashSet<>();
        if(!StringUtil.isEmpty(ids)){
            if(ids.contains(",")){
                String[] id =  ids.split(",");
                Collections.addAll(result, id);
            }else{
                result.add(ids);
            }
        }
        return result;
    }
}
