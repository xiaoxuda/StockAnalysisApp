/**
 *
 */
package cn.orditech.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 周期枚举
 *
 * @author kimi
 */
public enum CycleEnum {
    HOUR ("hour", "小时", 60 * 60 * 1000L),
    DAY ("day", "天", 24 * 60 * 60 * 1000L),
    WEEK ("week", "周", 7 * 24 * 60 * 60 * 1000L),
    MONTH ("month", "月", 30 * 7 * 24 * 60 * 60 * 1000L),
    QUARTER ("quarter", "季度", 3 * 7 * 24 * 60 * 60 * 1000L);

    private String code;
    private String desc;
    private long micromillions;

    CycleEnum (String code, String desc, long micromillions) {
        this.code = code;
        this.desc = desc;
        this.micromillions = micromillions;
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
     * @return the micromillions
     */
    public long getMicromillions () {
        return micromillions;
    }

    /**
     * 根据编编码取得周期枚举
     *
     * @param code
     * @return
     */
    public static CycleEnum getByCode (String code) {
        if (StringUtils.isBlank (code)) {
            return null;
        }
        for (CycleEnum em : CycleEnum.values ()) {
            if (em.getCode ().equals (code)) {
                return em;
            }
        }
        return null;
    }
}
