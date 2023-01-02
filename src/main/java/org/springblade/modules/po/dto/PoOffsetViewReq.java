package org.springblade.modules.po.dto;

import lombok.Data;
import org.springblade.modules.po.entity.PoOffsetViewEntity;

import java.math.BigDecimal;


/**
 * @author libin
 * @date 16:13 2020/8/31
 **/
@Data
public class PoOffsetViewReq extends PoOffsetViewEntity {

    private static final long serialVersionUID = 1L;

    private String selectType;
    private String type;

    private String proNo;
    private String proNoType;

    private String poCode;
    private String poCodeType;

    private Integer poLnMax;
    private Integer poLnMin;
    private String poLnType;

    private String itemCode;
    private String itemCodeType;

    private String itemName;
    private String itemNameType;

    private BigDecimal priceNumMin;
    private BigDecimal priceNumMax;
    private String priceNumType;

    private Long reqDateMax;
    private Long reqDateMin;
    private String reqDateType;

    private Long supConfirmDateMax;
    private Long supConfirmDateMin;
    private String supConfirmDateType;

    private Long supUpdateDateMax;
    private Long supUpdateDateMin;
    private String supUpdateDateType;

    private Long operationDateMax;
    private Long operationDateMin;
    private String operationDateType;

    private String remark;
    private String remarkType;

    private String supName;
    private String supNameType;

    private String supContact;
    private String supContactType;

    private String supMobile;
    private String supMobileType;



}
