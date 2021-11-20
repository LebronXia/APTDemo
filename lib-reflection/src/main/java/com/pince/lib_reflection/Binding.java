package com.pince.lib_reflection;

import android.app.Activity;

import java.lang.reflect.Field;

/**
 * Created by zxb in 2021/11/19
 */
public class Binding {
    public static void bind(Activity activity){
        //反射获取注解注释
        for (Field field: activity.getClass().getDeclaredFields()){
            BindView bindView = field.getAnnotation(BindView.class);
            if (bindView != null){
                try {
                    //扩大范围
                    field.setAccessible(true);
                    field.set(activity, activity.findViewById(bindView.value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
