package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_po_item")
@ApiModel(value = "PoItemReqRepotTotal对象", description = "")
public class PoItemReqRepotTotal extends BaseEntity {
    private String supCode;
    private String supName;
    private String itemCode;
    private String itemName;
    private Double total;
    private String proNo;
}

