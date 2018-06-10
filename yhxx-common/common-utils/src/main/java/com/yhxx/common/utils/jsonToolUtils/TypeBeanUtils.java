package com.yhxx.common.utils.jsonToolUtils;

import com.yhxx.common.bean.Currency;
import com.yhxx.common.bean.TypeBean;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
/**
 * @Author: Wanglf
 * @Date: Created in 18:20 2018/6/9
 * @modified By:
 */
public class TypeBeanUtils {

    public static <E extends Enum<E>> E getType(Class<E> clazz,int code){
        E type = null;
        List<E> enumList = EnumUtils.getEnumList(clazz);
        for (E each:enumList) {
            if (((TypeBean) each).getCode() == code) {
                type = each;
                break;
            }
        }
        return type;
    }

    public static boolean equals(TypeBean type1, TypeBean type2) {
        if (type1 == type2) {
            return true;
        }
        if (type1 == null || type2 == null) {
            return false;
        }

        return type1.getCode() == type2.getCode();
    }

    public static void main(String[] args) {
        System.out.println(TypeBeanUtils.getType(Currency.class, 1));
    }
}
