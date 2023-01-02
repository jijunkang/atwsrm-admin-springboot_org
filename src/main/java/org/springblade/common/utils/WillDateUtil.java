package org.springblade.common.utils;

import org.springblade.core.tool.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public
class WillDateUtil{
    public static
    Date getTodayStart(){
        String fmt = "yyyy-MM-dd";
        return DateUtil.parse(DateUtil.format(new Date(),fmt),fmt);
    }
    public static
    Date getTodayEnd(){
        return DateUtil.parse(DateUtil.format(new Date(),"yyyy-MM-dd 23:59:59"),"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 是否是周日
     * @param date
     * @return
     */
    public static
    boolean isSunday(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }
    /**
     * 是否是周日
     * @param date
     * @return
     */
    public static
    boolean isSunday(String date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parse(date, DateUtil.PATTERN_DATE));
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    /**
     * unix时间戳（秒）格式格式化
     * @param second
     * @param fmt
     * @return
     */
    public static
    String unixTimeToStr(Long second, String fmt){
        if(second == null || second <= 0){
            return "";
        }
        return DateUtil.format(new Date(second*1000),fmt);
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 　　 *字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }
}
