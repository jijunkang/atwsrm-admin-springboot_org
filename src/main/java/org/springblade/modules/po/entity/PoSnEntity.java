package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * SN
 *
 * @author Will
 */
@Data
@TableName("atw_po_sn")
@ApiModel(value = "sn", description = "sn")
public class PoSnEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String rcvCode;

    private String snCode;

    private Integer snLn;

    private String poCode;

    private Integer poLn;

    private String itemCode;

    private String heatCode;

    private BigDecimal rcvNum;

    private BigDecimal unqualifiedNum;

    private String checker;

    private Integer endFlag;

    private Date create_time;

    private Date update_time;

    private Integer isDeleted;
}
