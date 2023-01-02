package org.springblade.modules.po.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springblade.core.tool.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoItemReqRepotCurrMonthDTO {
    private String supCode;
    private String itemCode;
    private String date;
    private Integer qty;
    private String poCode;
    private Integer poLn;
    private String proNo;
    private Integer isMeetOptDate = 1;

    public PoItemReqRepotCurrMonthDTO(String date) {
        this.date = date;
        this.qty = 0;
    }
}
