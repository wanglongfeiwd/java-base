package com.yhxx.common.bean;

/**
 * @Author: Wanglf
 * @Date: Created in 22:57 2017/11/7
 * @modified By:
 */
public class ZSetTypedTuple<V> implements Comparable<ZSetTypedTuple<V>> {

    private V value;

    private Double score;

    public ZSetTypedTuple() {
    }

    public ZSetTypedTuple(V value, Double score) {
        this.value = value;
        this.score = score;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public int compareTo(ZSetTypedTuple<V> o) {
        return this.getScore().compareTo(o.getScore());
    }
}
