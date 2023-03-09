package org.springblade.modules.po.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 采购订单表头 模型DTO
 * @author Will
 */
@Data
@AllArgsConstructor
public
class PromiseDateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String DocNo;
    private String DocLineNo;
    private Long ConfirmDate;
    private String OrgCode;


}
