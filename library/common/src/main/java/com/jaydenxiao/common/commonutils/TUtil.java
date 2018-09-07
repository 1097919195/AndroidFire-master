package com.jaydenxiao.common.commonutils;

import java.lang.reflect.ParameterizedType;

/**
 * 类转换初始化
 */
public class TUtil {
    //(class)((ParameterizedType)(this.getClass(）.getGenericSuperclass()).getActualTypeArguments()[0] 这是一个获取类的方法
    //ParameterizedType实现泛型类类型参数化
    //getGenericSuperclass方法可以获取当前对象的直接超类的 Type
    //getActualTypeArguments 得到该泛型
    public static <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassCastException e) {
        }
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
