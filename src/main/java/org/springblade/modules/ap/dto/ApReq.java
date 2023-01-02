package org.springblade.modules.ap.dto;

import lombok.Data;
import org.springblade.modules.ap.entity.ApEntity;
import org.springblade.modules.ap.entity.ApInvoiceEntity;
import org.springblade.modules.ap.entity.ApItemEntity;
import org.springblade.modules.ap.vo.ApRcvVO;

import java.util.List;

/**
 * @author libin
 *
 * @date 11:39 2020/6/3
 **/
@Data
public class ApReq {

    private Long id;
    private String supCode;
    private String supName;
    private Long apId;
    private Long apItemId;
    private String apCode;
    private String poCode;
    private String poLn;
    private String rcvCode;
    private Long rcvDateStart;
    private Long rcvDateEnd;
    private String createTimeStart;
    private String createTimeEnd;
    private String srmDateStart;
    private String srmDateEnd;
    private Long backDateStart;
    private Long backDateEnd;
    private Integer status;
    private Integer isAgree;
    private String proNo;
    private Long prepayDate;
    private Long invoiceDate;
    private String remark;
    private String backReason;
    private List<ApItemEntity> itemEntities;
    private List<ApInvoiceEntity> invoiceEntities;
    private List<ApEntity> apEntities;
    private List<ApRcvVO> apRcvVos;
    private ApRcvVO apRcvVo;
    private String itemCode;
    private String itemName;
    private String billCode;
    private String isVmi;
    private String selectionIds;
    private String vmiStatus;
    private String orgCode;
}
