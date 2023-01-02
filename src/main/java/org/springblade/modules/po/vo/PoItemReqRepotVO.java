package org.springblade.modules.po.vo;

import lombok.Data;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.dto.PoItemReqRepotNexHalfYearDTO;
import org.springblade.modules.po.entity.PoItemEntity;

import java.util.*;

@Data
public class PoItemReqRepotVO {
    private static final long serialVersionUID = 1L;
    private String supCode;
    private String supName;
    private String itemCode;
    private String itemName;
    private Double total;
    private String proNo;
    private List<PoItemEntity> poItems;
    private List<PoItemReqRepotCurrMonthDTO> columnValues;

    public PoItemReqRepotVO(String supCode, String supName, String itemCode, String itemName, Double total, String proNo, List<PoItemEntity> poItems) {
        this.supCode = supCode;
        this.supName = supName;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.total = total;
        this.proNo = proNo;
        this.poItems = poItems;
        this.columnValues = new ArrayList<>();
    }
}
