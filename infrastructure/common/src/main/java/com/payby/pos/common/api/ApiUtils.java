package com.payby.pos.common.api;

import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ApiUtils {

    private static final String TAG = "ApiUtils";

    private Map<Class, Class> injectApiImplMap = new HashMap<>();
    private Map<Class, BaseApi> apiMap = new ConcurrentHashMap<>();

    private ApiUtils() {
        init();
    }

    /**
     * It'll be injected the implClasses who have {@link Api} annotation
     * by function of {@link ApiUtils#registerImpl} when execute transform task.
     */
    private void init() {/*inject*/}

    private void registerImpl(Class implClass) {
        injectApiImplMap.put(implClass.getSuperclass(), implClass);
    }

    /**
     * Get api.
     *
     * @param apiClass The class of api.
     * @param <T>      The type.
     * @return the api
     */
    @Nullable
    public static <T extends BaseApi> T getApi(final Class<T> apiClass) {
        return getInstance().getApiInner(apiClass);
    }

    public static void register(@Nullable Class<? extends BaseApi> implClass) {
        if (implClass == null) {
            return;
        }
        getInstance().registerImpl(implClass);
    }

    public static String toString_() {
        return getInstance().toString();
    }

    @Override
    public String toString() {
        return "ApiUtils: " + injectApiImplMap;
    }

    private static ApiUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private <Result> Result getApiInner(Class apiClass) {
        BaseApi api = apiMap.get(apiClass);
        if (api != null) {
            return (Result) api;
        }
        synchronized (apiClass) {
            api = apiMap.get(apiClass);
            if (api != null) {
                return (Result) api;
            }
            Class implClass = injectApiImplMap.get(apiClass);
            if (implClass != null) {
                try {
                    api = (BaseApi) implClass.newInstance();
                    apiMap.put(apiClass, api);
                    return (Result) api;
                } catch (Exception ignore) {
                    Log.e(TAG, "The <" + implClass + "> has no parameterless constructor.");
                    return null;
                }
            } else {
                Log.e(TAG, "The <" + apiClass + "> doesn't implement.");
                return null;
            }
        }
    }

    private static class LazyHolder {

        private static final ApiUtils INSTANCE = new ApiUtils();

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    public @interface Api {

        boolean isMock() default false;

    }

    public static class BaseApi {

    }

}