package com.yhxx.common.utils;


import com.yhxx.common.bean.BeanException;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingzhen on 17/9/24.
 */
public class PropertyUtils {

    /**
     * 线程安全的类属性CACHE（忽略大小写）(小心因为ClassLoader的问题造成同样一个Class的多个Class实例)
     */
    private static Map<Class, Map<String, PropertyDescriptor>> beanPropIgnoreCaseCache = new ConcurrentHashMap<>();

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        return org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors(beanClass);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        return org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors(bean);
    }

    public static PropertyDescriptor getPropertyDescriptor(Object bean, String name) {
        try {
            return org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptor(bean, name);
        } catch (Exception e) {
            throw new BeanException(e);
        }
    }

    public static PropertyDescriptor getPropertyDescriptorIgnoreCase(Object bean, String name) {
        if (!beanPropIgnoreCaseCache.containsKey(bean.getClass())) {
            // 已经缓存了则取缓存中的数据
            fetchBeanPropertyIgnoreCase(bean.getClass());
        }
        return beanPropIgnoreCaseCache.get(bean.getClass()).get(name.toLowerCase());
    }

    public static Class<?> getPropertyType(Object bean, String name) {
        try {
            return org.apache.commons.beanutils.PropertyUtils.getPropertyType(bean, name);
        } catch (Exception e) {
            throw new BeanException(e);
        }
    }

    public static Class<?> getPropertyTypeIgnoreCase(Object bean, String name) {
        PropertyDescriptor pd = getPropertyDescriptorIgnoreCase(bean, name);
        return pd.getPropertyType();
    }

    public static Object getProperty(Object bean, String name) {
        try {
            return org.apache.commons.beanutils.PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            throw new BeanException(e);
        }
    }

    public static Object getPropertyIgnoreCase(Object bean, String name) {
        PropertyDescriptor pd = getPropertyDescriptorIgnoreCase(bean, name);
        try {
            return pd.getReadMethod().invoke(bean);
        } catch (Exception e) {
            throw new BeanException(e);
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            org.apache.commons.beanutils.PropertyUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            throw new BeanException(e);
        }
    }

    public static void setPropertyIgnoreCase(Object bean, String name, Object value) {
        PropertyDescriptor pd = getPropertyDescriptorIgnoreCase(bean, name);
        try {
            pd.getWriteMethod().invoke(bean, value);
        } catch (Exception e) {
            throw new BeanException(e);
        }
    }

    private static void fetchBeanPropertyIgnoreCase(Class clazz) {
        Map<String, PropertyDescriptor> propMap = new HashMap<>();
        PropertyDescriptor[] propArray = org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor prop : propArray) {
            propMap.put(prop.getName().toLowerCase(), prop);
        }
        beanPropIgnoreCaseCache.put(clazz, propMap);
    }
}
