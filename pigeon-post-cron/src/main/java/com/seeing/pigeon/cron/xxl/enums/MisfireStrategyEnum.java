package com.seeing.pigeon.cron.xxl.enums;

/**
 * 调度过期策略
 *
 * @author zengxw
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW;

    MisfireStrategyEnum() {
    }
}
