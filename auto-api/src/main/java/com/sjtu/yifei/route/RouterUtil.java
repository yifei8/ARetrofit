package com.sjtu.yifei.route;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/6/1
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

final class RouterUtil {

    protected static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    protected static boolean isSpecificClass(Class clazz, Class youSpecific) {
        while (clazz != Object.class) {
            if (clazz == youSpecific) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
