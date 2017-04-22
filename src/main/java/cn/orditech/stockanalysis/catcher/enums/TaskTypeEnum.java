package cn.orditech.stockanalysis.catcher.enums;

import cn.orditech.enums.CycleEnum;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kimi on 2017/1/11.
 */
public enum TaskTypeEnum {
    /**
     * 抓取财务报表
     **/
    EASTMONEYNET_STATEMENT ("eastmoney_statement", "财务报表", CycleEnum.HOUR, new CycleRule () {
        public boolean decide () {
            Calendar calendar = Calendar.getInstance ();
            //每天早上5点工作
            return calendar.get (Calendar.HOUR_OF_DAY) == 5;
        }
    }),

    /**
     * 抓取上市公司列表
     **/
    JUCAONET_COMPANY_LIST ("jucaonet_company_list", "股票列表", CycleEnum.HOUR, new CycleRule () {
        public boolean decide () {
            Calendar calendar = Calendar.getInstance ();
            //每天早上2点工作
            return calendar.get (Calendar.HOUR_OF_DAY) == 2;
        }
    }),

    /**
     * 获取公司股本数量
     **/
    JUCAONET_COMPANY_SHARECAPITAL ("jucaonet_company_sharecapital", "股本", CycleEnum.HOUR, new CycleRule () {
        public boolean decide () {
            Calendar calendar = Calendar.getInstance ();
            //每天早上4点工作
            return calendar.get (Calendar.HOUR_OF_DAY) == 4;
        }
    }),

    /**
     * 获取股票当前交易信息
     **/
    SINAJS_PRICE ("sinajs_price", "股票实时价格", CycleEnum.HOUR, new CycleRule () {
        public boolean decide () {
            Calendar calendar = Calendar.getInstance ();
            int dayOfWeek = calendar.get (Calendar.DAY_OF_WEEK);
            //周末不开市啊！！！
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                return false;
            }
            //每天工作八小时
            return calendar.get (Calendar.HOUR_OF_DAY) >= 8 && calendar.get (Calendar.HOUR_OF_DAY) <= 16;
        }
    }),

    /**
     * 获取股票本季度历史交易信息
     **/
    SINAJS_HISTORY_TRADE_DETAIL ("sinajs_history_trade_detail", "股票历史交易信息", CycleEnum.DAY, new CycleRule () {
        public boolean decide () {
            Calendar calendar = Calendar.getInstance ();
            //每天晚上十点开始工作
            return calendar.get (Calendar.HOUR_OF_DAY) >= 22;
        }
    }),

    /**
     * 获取股票上季度历史交易记录
     */
    SINAJS_LAST_QUATER_TRADE_DETAIL ("sinajs_history_trade_detail", "股票历史交易信息", CycleEnum.DAY, new CycleRule () {
        public boolean decide () {
            Calendar calendar = Calendar.getInstance ();
            //每天晚上十点开始工作
            return calendar.get (Calendar.HOUR_OF_DAY) >= 23;
        }
    });

    /**
     * 任务代码
     **/
    private String code;
    /**
     * 任务描述
     **/
    private String desc;
    /**
     * 调度周期类型
     **/
    private CycleEnum cycle;

    /**
     * 决策规则
     **/
    private CycleRule cycleRule;

    TaskTypeEnum (String code, String desc, CycleEnum cycle) {
        this.code = code;
        this.desc = desc;
        this.cycle = cycle;
        this.cycleRule = null;
    }

    TaskTypeEnum (String code, String desc, CycleEnum cycle, CycleRule cycleRule) {
        this (code, desc, cycle);
        this.cycleRule = cycleRule;
    }

    /**
     * 决策规则，不同任务需要自行定制
     *
     * @author kimi
     */
    interface CycleRule {
        boolean decide ();
    }

    /**
     * @return the code
     */
    public String getCode () {
        return code;
    }

    /**
     * @return the desc
     */
    public String getDesc () {
        return desc;
    }

    /**
     * @return the cycle
     */
    public CycleEnum getCycle () {
        return cycle;
    }

    public static TaskTypeEnum getByCode (String code) {
        for (TaskTypeEnum type : TaskTypeEnum.values ()) {
            if (type.getCode ().equals (code)) {
                return type;
            }
        }

        return null;
    }

    /**
     * 根据上次执行的时间点及决策逻辑判断是否开启下一个周期
     *
     * @param lastTimePoint
     * @return
     */
    public boolean isInNextCycle (Date lastTimePoint) {
        // 根据决策规则决策
        if (this.cycleRule != null && !this.cycleRule.decide ()) {
            return false;
        }

        // 根据时间决策，无上次执行时间点则默认执行
        if (null == lastTimePoint) {
            return true;
        }

        return (System.currentTimeMillis () - lastTimePoint.getTime ()) >= this.getCycle ().getMicromillions ();
    }
}
