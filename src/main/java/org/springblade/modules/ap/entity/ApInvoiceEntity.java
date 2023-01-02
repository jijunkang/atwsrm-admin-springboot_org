package org.springblade.modules.ap.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;

/**
 * @author libin
 * @date 11:35 2020/6/3
 **/
@Data
@TableName("atw_ap_\u202Ainvoice")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ApInvoice对象", description = "")
public class ApInvoiceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 应付ID
     */
    @ApiModelProperty(value = "应付ID")
    private Long billId;
    /**
     * 应付单号
     */
    @ApiModelProperty(value = "应付单号")
    private String billCode;
    /**
     * 对账ID
     */
    @ApiModelProperty(value = "对账ID")
    private Long apId;
    /**
     * 对账单号
     */
    @ApiModelProperty(value = "对账单号")
    private String apCode;
    /**
     * 发票编号
     */
    @ApiModelProperty(value = "发票编号")
    private String invoiceCode;
    /**
     * 开票日期
     */
    @ApiModelProperty(value = "开票日期")
    private String invoiceDate;
    /**
     * 开票影像
     */
    @ApiModelProperty(value = "开票影像")
    private String attachment;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

}
