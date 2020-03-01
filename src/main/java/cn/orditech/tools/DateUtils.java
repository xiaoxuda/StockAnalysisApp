/**
 *
 */
package cn.orditech.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author kimi
 */
public class DateUtils {

    public static final SimpleDateFormat DAY = new SimpleDateFormat ("yyyy-MM-dd");

    /**
     * 获取去年同季度日期
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getThisDateInLastYear (String date) throws Exception {
        String year_s = date.split ("-")[0];
        int year_i = Integer.valueOf (year_s);
        return (year_i - 1) + date.substring (4);
    }

    /**
     * 基于当前日期获取指定季度的财务报告日期
     *
     * @param quarterGap 相对于当前季度的向前偏移量，必须大于0
     * @return
     * @throws Exception
     */
    public static String getQuarterFinanceReportDate (int quarterGap) {
        if(quarterGap <= 0){
            throw new IllegalArgumentException("quarterGap must bigger than zero!");
        }
        try {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            int gap = 0 - (month + 1) % 3;
            if(gap == 0){
                gap = 0 - (month + 1);
            }
            calendar.add(Calendar.MONTH, gap - (quarterGap - 1) * 3);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return DAY.format(calendar.getTime());
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 基于当前时间获取指定年度的财务报告日期
     *
     * @param yearGap 相对于当前日期的财务报告向后偏移量
     * @return
     */
    public static String getYearFinanceReportDate (int yearGap) {
        if(yearGap <= 0){
            throw new IllegalArgumentException("yearGap must bigger than zero!");
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -yearGap);
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DAY_OF_MONTH, 31);
            return DAY.format(calendar.getTime());
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 格式化日期，格式yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDayStr (Date date) {
        if (date == null) {
            return null;
        }
        return DAY.format (date);
    }
}
