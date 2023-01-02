package org.springblade.modules.po.dto;

import lombok.Data;

/**
 * Created by liujibo on 2021/4/1 19:38
 */
@Data
public class PoReq {
    public String getSupCode() {
        return supCode;
    }

    public void setSupCode(String supCode) {
        this.supCode = supCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public PoReq() {
    }

    private String supCode;
    private String itemCode;
}
