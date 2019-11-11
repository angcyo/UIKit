package retrofit2;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import android.text.TextUtils;
import com.angcyo.http.BuildConfig;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/11/06
 */
public class RetrofitServiceMapping {

    /**
     * 功能总开关
     */
    public static boolean enableMapping = BuildConfig.DEBUG;

    /**
     * 当方法名匹配不到url时, 是否使用声明的url匹配映射的url.
     * <p>
     * 关闭之后, 将只匹配方法名对应的url
     */
    public static boolean enableUrlMap = true;

    /**
     * 映射关系表, key 可以是方法名, 也是部分url字符串
     */
    public static Map<String, String> defaultMap = new ArrayMap<>();

    public static void init(boolean enableMapping, Map<String, String> methodMapping) {
        RetrofitServiceMapping.enableMapping = enableMapping;
        defaultMap = methodMapping;
    }

    /**
     * 请在调用
     * <pre>
     *     Retrofit.create()
     * </pre>
     * 之前调用.
     * <p>
     * 比如:
     * Retrofit.create()
     * 替换成
     * RetrofitServiceMapping.mapping().create()
     * <p>
     * 暂不支持 Retrofit 的单例模式.
     */
    public static Retrofit mapping(@NonNull Retrofit retrofit, @NonNull Class<?> service) {
        if (defaultMap != null && !defaultMap.isEmpty() && enableMapping) {
            configRetrofit(retrofit, service, defaultMap);
        } else {
            try {
                Map<Method, ServiceMethod> serviceMethodCache = (Map<Method, ServiceMethod>) Reflect.getMember(retrofit, "serviceMethodCache");
                serviceMethodCache.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retrofit;
    }

    private static void configRetrofit(@NonNull Retrofit retrofit, @NonNull Class<?> service, @NonNull Map<String, String> map) {
        for (Method method : service.getDeclaredMethods()) {
            try {
                String mapUrl = map.get(method.getName());

                ServiceMethod serviceMethod = ServiceMethod.parseAnnotations(retrofit, method);

                RequestFactory requestFactory = null;
                if (serviceMethod instanceof HttpServiceMethod) {
                    requestFactory = (RequestFactory) Reflect.getMember(HttpServiceMethod.class, serviceMethod, "requestFactory");
                }

                if (TextUtils.isEmpty(mapUrl) && enableUrlMap) {
                    //通过方法, 拿不到映射的url时, 则匹配url映射
                    if (requestFactory != null) {
                        String relativeUrl = (String) Reflect.getFieldValue(requestFactory, "relativeUrl");

                        if (!TextUtils.isEmpty(relativeUrl)) {
                            for (String key : map.keySet()) {
                                String url = map.get(key);
                                if (relativeUrl.contains(key)) {
                                    mapUrl = url;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    //retrofit 2.4
                    //ServiceMethod.Builder methodBuilder = new ServiceMethod.Builder(retrofit, method);
                    //ServiceMethod serviceMethod = methodBuilder.build();
                    //Reflect.setFieldValue(serviceMethod, "relativeUrl", mapUrl);
                    //Map<Method, ServiceMethod> serviceMethodCache = (Map<Method, ServiceMethod>) Reflect.getMember(retrofit, "serviceMethodCache");
                    //serviceMethodCache.put(method, serviceMethod);
                    //end

                    //Log.i("angcyo", "succeed");
                }

                if (requestFactory != null && !TextUtils.isEmpty(mapUrl)) {
                    Reflect.setFieldValue(requestFactory, "relativeUrl", mapUrl);

                    Map<Method, ServiceMethod> serviceMethodCache = (Map<Method, ServiceMethod>) Reflect.getMember(retrofit, "serviceMethodCache");
                    serviceMethodCache.put(method, serviceMethod);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
