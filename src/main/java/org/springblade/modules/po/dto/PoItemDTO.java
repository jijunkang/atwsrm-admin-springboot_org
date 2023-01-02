package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoItemEntity;

import java.util.List;

/**
 * 采购订单明细 模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class PoItemDTO extends PoItemEntity{

    private static final long serialVersionUID = 1L;

    @Excel(name = "ID")
    private Long   id;
    private Long reqDate;
    private Long   reqDateStart;
    private Long   reqDateEnd;
    private String isHavesup;
    private Integer isComBill;


    private String craftCtrlCode;
    private Long craftCtrlNodeName;
    private Integer nodeStatus;

    private Integer isByWeight;

    private String statuss;

    private String moNo;

    private Long   lastSyncTimeStart;
    private Long   lastSyncTimeEnd;

    private Integer bizType;
    private String isVmi;

    private String ids;

    private Integer isUrgent;

    private List<String> statusarray;

}
