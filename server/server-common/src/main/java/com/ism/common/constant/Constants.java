package com.ism.common.constant;

public class Constants {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // 入库类型
    public static final String INBOUND_PURCHASE = "PURCHASE";
    public static final String INBOUND_RETURN = "RETURN";

    // 出库类型
    public static final String OUTBOUND_SALE = "SALE";
    public static final String OUTBOUND_USE = "USE";

    // 单据状态
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_PENDING_AUDIT = "PENDING_AUDIT";
    public static final String STATUS_AUDITED = "AUDITED";
    public static final String STATUS_REJECTED = "REJECTED";

    // 预警类型
    public static final String ALERT_SHORTAGE = "SHORTAGE";
    public static final String ALERT_OVERSTOCK = "OVERSTOCK";
    public static final String ALERT_EXPIRY = "EXPIRY";
    public static final String ALERT_SLOW_MOVING = "SLOW_MOVING";

    // 库存变动类型
    public static final String LOG_INBOUND = "INBOUND";
    public static final String LOG_OUTBOUND = "OUTBOUND";
    public static final String LOG_TRANSFER = "TRANSFER";
    public static final String LOG_CHECK = "CHECK";

    // 菜单类型
    public static final String MENU_TYPE_DIR = "M";
    public static final String MENU_TYPE_MENU = "C";
    public static final String MENU_TYPE_BUTTON = "F";
}
