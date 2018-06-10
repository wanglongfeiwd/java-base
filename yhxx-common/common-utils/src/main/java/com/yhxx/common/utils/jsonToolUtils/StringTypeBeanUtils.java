package com.yhxx.common.utils.jsonToolUtils;

import com.yhxx.common.bean.StringTypeBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;

import java.util.List;

/**
 * @Author: Wanglf
 * @Date: Created in 18:32 2018/6/9
 * @modified By:
 */
public class StringTypeBeanUtils {

    public static <E extends Enum<E>> E getType(Class<E> clazz, String code) {
        E type = null;
        List<E> enumList = EnumUtils.getEnumList(clazz);
        for (E each : enumList) {
            if (StringUtils.equalsIgnoreCase(((StringTypeBean) each).getCodeString(), code)) {
                type = each;
                break;
            }
        }
        return type;
    }

    public static boolean equals(StringTypeBean type1, StringTypeBean type2) {
        if (type1 == type2) {
            return true;
        }
        if (type1 == null || type2 == null) {
            return false;
        }

        return type1.getCodeString().equals(type2.getCodeString());
    }
}
