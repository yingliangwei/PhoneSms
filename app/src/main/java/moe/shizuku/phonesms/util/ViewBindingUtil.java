package moe.shizuku.phonesms.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class ViewBindingUtil {
    public static <Binding extends ViewBinding> Binding inflate(Class<?> clazz, LayoutInflater inflater) {
        return inflate(clazz, inflater, null);
    }

    public static <Binding extends ViewBinding> Binding inflate(Class<?> clazz, LayoutInflater inflater, ViewGroup root) {
        return inflate(clazz, inflater, root, false);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <Binding extends ViewBinding> Binding inflate(Class<?> clazz, LayoutInflater inflater, ViewGroup root, boolean attachToRoot) {
        Class<?> bindingClass = getBindingClass(clazz);
        if (bindingClass != null) {
            try {
                Method method = bindingClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                return (Binding) method.invoke(null, inflater, root, attachToRoot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Class<?> getBindingClass(Class<?> clazz) {
        Type[] types = null;
        Class<?> bindingClass = null;
        if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
            types = parameterizedType.getActualTypeArguments();
        }
        if (types == null) {
            return null;
        }
        for (Type type : types) {
            if (type instanceof Class<?>) {
                Class<?> temp = (Class<?>) type;
                if (ViewBinding.class.isAssignableFrom(temp)) {
                    bindingClass = temp;
                }
            }
        }
        return bindingClass;
    }
}