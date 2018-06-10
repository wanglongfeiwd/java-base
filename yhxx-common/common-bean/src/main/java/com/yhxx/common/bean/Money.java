package com.yhxx.common.bean;

/**
 * @Author: Wanglf
 * @Date: Created in 17:21 2018/6/9
 * @modified By:
 */

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class Money extends BaseBean implements Comparable<Money> {

    private static final long serialVersionUID = 1L;

    private static final BigDecimal NUM_100 = new BigDecimal(100);

    public static final String GENERIC = "0.##";
    public static final String COMPLETING = "0.00";

    public static final String FMT_GENERIC = "#,##0.##";
    public static final String FMT_COMPLETING = "#,##0.00";

    /**
     * 0金额
     */
    public static final Money ZERO = new Money();

    /**
     * 最大金额
     */
    public static final Money MAX = new Money(Integer.MAX_VALUE);

    /**
     * 币种
     */
    private final Currency cur = Currency.CNY;

    /**
     * 数值（人民币到元）
     */
    private final BigDecimal value;

    public Money() {
        this(BigDecimal.ZERO);
    }

    /**
     * @param value 常用单位数字类型(例如人民币:元)
     */
    public Money(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("money value is not empty!");
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException("money scale is invalid:" + value.scale());
        }
        this.value = value;
    }

    /**
     * @param genericUnit 常用单位字符(例如人民币:元)
     */
    public Money(String genericUnit) {
        if (genericUnit == null || genericUnit.length() <= 0) {
            throw new IllegalArgumentException("money string is not empty!");
        }
        // 去掉逗号
        genericUnit = genericUnit.replace(",", "");
        BigDecimal value = null;
        try {
            value = new BigDecimal(genericUnit);
        } catch (Exception e) {
            throw new IllegalArgumentException("money string is invalid:" + e.getMessage(), e);
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException("money scale is invalid:" + value.scale());
        }
        this.value = value;
    }

    /**
     * @param minUnitValue 支持最小单位整型值(例如人民币:分)
     */
    public Money(int minUnitValue) {
        this(new BigDecimal(minUnitValue).divide(NUM_100));
    }

    public Currency getCur() {
        return cur;
    }

    public BigDecimal getValue() {
        return value;
    }

    /**
     * @return 支持最小单位整型值(例如人民币 : 分)
     */
    public int intMinUnitValue() {
        return value.multiply(NUM_100).intValue();
    }

    public Money add(Money money) {
        return new Money(value.add(money.getValue()));
    }

    public Money subtract(Money money) {
        return new Money(value.subtract(money.getValue()));
    }

    public Money multiply(int num) {
        return new Money(value.multiply(new BigDecimal(num)));
    }

    public Money multiply(BigDecimal num) {
        return new Money(value.multiply(num));
    }

    public Money divide(BigDecimal num) {
        return new Money(value.divide(num));
    }

    public Money reverse() {
        return multiply(-1);
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat(GENERIC);
        return df.format(value);
    }

    public String toFmtString() {
        DecimalFormat df = new DecimalFormat(FMT_GENERIC);
        return df.format(value);
    }

    public String toFmtString(String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Money o) {
        return (value).compareTo(o.getValue());
    }

    public boolean equals(Money o) {
        return (value).equals(o.getValue());
    }

    public int hashCode() {
        return value.hashCode();
    }

    public static Money min(Money a, Money b) {
        BigDecimal min = a.getValue().min(b.getValue());
        return new Money(min);
    }

    public static Money max(Money a, Money b) {
        BigDecimal max = a.getValue().max(b.getValue());
        return new Money(max);
    }

    public static Money abs(Money a) {
        BigDecimal abs = a.getValue().abs();
        return new Money(abs);
    }

    public static void main(String[] args) {
//        Money m = new Money("12345678.10");
//        System.out.println(m.toFmtString());
//        System.out.println(m.multiply(2));
//        System.out.println(m.add(m));
//        System.out.println(m.subtract(m));
//        System.out.println(m.subtract(m));
//        System.out.println(m);
//
//        m = new Money("123456.1");
//        System.out.println(m);
//        System.out.println(m.multiply(2));
//        System.out.println(m.add(m));
//        System.out.println(m.subtract(m));
//        System.out.println(m.subtract(m));
//        System.out.println(m);
//        System.out.println("--------------------");
//        System.out.println(m.toFmtString(COMPLETING));
//        System.out.println(m.toFmtString(GENERIC));
//        System.out.println(m.toFmtString(FMT_COMPLETING));
//        System.out.println(m.toFmtString(FMT_GENERIC));

        Money a = new Money(100);
        Money b = new Money(200);

        Money d = a.add(b).multiply(new Money(300).getValue());
        System.out.println(d.getValue());

    }

}
