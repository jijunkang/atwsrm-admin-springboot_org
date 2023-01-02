package org.springblade.modules.po.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springblade.core.tool.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoItemReqRepotNexHalfYearDTO {
    private String month;
    private String title;
    private Integer qty;

    public PoItemReqRepotNexHalfYearDTO(String month) {
        this.month = month;
        this.qty = 0;
    }

    public String getTitle() {
        return date2Short(this.month).get(Calendar.MONTH) + "月";
    }

    private Calendar date2Short(String fullDate) {
        Date date = DateUtil.parse(fullDate, "yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
