package com.angcyo.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import com.angcyo.http.type.TypeBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/01 14:42
 * 修改人员：Robi
 * 修改时间：2016/12/01 14:42
 * 修改备注：
 * Version: 1.0.0
 */
public class Json {

    public static <T> List<T> fromList(String json, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, TypeBuilder.newInstance(List.class).addTypeParam(type).build());
    }

    public static <T> T from(String json, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static <T> T from(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static String to(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T from2(String json, Class<T> type) {
        T result = null;
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        try {
            result = readerObject(jsonReader, type);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jsonReader.close();
            } catch (IOException e) {

            }
        }
        return result;
    }

    private static <T> T readerObject(JsonReader jsonReader, Class<T> type) {
        T result = null;
        try {
            result = (T) Class.forName(type.getName()).newInstance();
            Field[] fields = result.getClass().getDeclaredFields();
            Map<String, Field> fieldsMap = new HashMap<>();
            for (Field field : fields) {
                fieldsMap.put(field.getName(), field);
            }

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                try {
                    String nextName = jsonReader.nextName();
                    Field field = fieldsMap.get(nextName);
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        //具有泛型的type, 比如List<String>
                        Type subType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        Class<?> subClass = Class.forName(((Class) subType).getName());
                        if (fieldType.isAssignableFrom(List.class)) {
                            //field.set(result, );
                            List list = new ArrayList<>();
                            field.set(result, list);

                            readerList(jsonReader, list, subClass);
                        } else {
                            //不支持的类型, 跳过
                            jsonReader.skipValue();
                        }
                    } else {
                        if (fieldType.isAssignableFrom(int.class)) {
                            field.set(result, jsonReader.nextInt());
                        } else if (fieldType.isAssignableFrom(long.class)) {
                            field.set(result, jsonReader.nextLong());
                        } else if (fieldType.isAssignableFrom(float.class) ||
                                fieldType.isAssignableFrom(double.class)) {
                            field.set(result, jsonReader.nextDouble());
                        } else if (fieldType.isAssignableFrom(boolean.class)) {
                            field.set(result, jsonReader.nextBoolean());
                        } else if (fieldType.isAssignableFrom(String.class)) {
                            field.set(result, jsonReader.nextString());
                        } else {
                            field.set(result, readerObject(jsonReader, fieldType));
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

        } catch (Exception e) {
            //e.printStackTrace();
            try {
                jsonReader.skipValue();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
        }
        return result;
    }

    private static void readerList(JsonReader jsonReader, List list, Class<?> fieldType) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                try {
                    if (fieldType.isAssignableFrom(int.class)) {
                        list.add(jsonReader.nextInt());
                    } else if (fieldType.isAssignableFrom(long.class)) {
                        list.add(jsonReader.nextLong());
                    } else if (fieldType.isAssignableFrom(float.class) ||
                            fieldType.isAssignableFrom(double.class)) {
                        list.add(jsonReader.nextDouble());
                    } else if (fieldType.isAssignableFrom(boolean.class)) {
                        list.add(jsonReader.nextBoolean());
                    } else if (fieldType.isAssignableFrom(String.class)) {
                        list.add(jsonReader.nextString());
                    } else {
                        list.add(readerObject(jsonReader, fieldType));
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    jsonReader.skipValue();
                }
            }
            jsonReader.endArray();
        } catch (IOException e) {
            //e.printStackTrace();
            try {
                jsonReader.skipValue();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
        }
    }

    public static Builder build() {
        return json();
    }

    public static Builder array() {
        return new Builder().array();
    }

    public static Builder json() {
        return new Builder().json();
    }

    public static class Builder {
        JsonElement rootElement;
        Stack<JsonElement> subElementStack;

        /**
         * 当对象的值为null时, 是否忽略
         */
        boolean ignoreNull = true;

        private Builder() {
            subElementStack = new Stack<>();
        }

        /**
         * 构建一个Json对象
         */
        private Builder json() {
            if (rootElement == null) {
                rootElement = new JsonObject();
            }
            return this;
        }

        /**
         * 构建一个Json数组
         */
        private Builder array() {
            if (rootElement == null) {
                rootElement = new JsonArray();
            }
            return this;
        }

        public Builder ignoreNull(boolean ignoreNull) {
            this.ignoreNull = ignoreNull;
            return this;
        }

        private JsonElement getOperateElement() {
            checkRootElement();
            if (subElementStack.isEmpty()) {
                return rootElement;
            } else {
                return subElementStack.lastElement();
            }
        }

        private void operateElement(@Nullable JsonElement element, @NonNull String key, @Nullable Object obj) {
            if (element instanceof JsonObject) {
                if (obj == null) {
                    if (!ignoreNull) {
                        ((JsonObject) element).add(key, null);
                    }
                } else if (obj instanceof String) {
                    ((JsonObject) element).addProperty(key, (String) obj);
                } else if (obj instanceof Number) {
                    ((JsonObject) element).addProperty(key, (Number) obj);
                } else if (obj instanceof Character) {
                    ((JsonObject) element).addProperty(key, (Character) obj);
                } else if (obj instanceof Boolean) {
                    ((JsonObject) element).addProperty(key, (Boolean) obj);
                } else if (obj instanceof JsonElement) {
                    ((JsonObject) element).add(key, (JsonElement) obj);
                } else {
                    ((JsonObject) element).addProperty(key, obj.toString());
                }
            } else if (element instanceof JsonArray) {
                if (obj == null) {
                    if (!ignoreNull) {
                        ((JsonArray) element).add(((String) null));
                    }
                } else if (obj instanceof String) {
                    ((JsonArray) element).add((String) obj);
                } else if (obj instanceof Number) {
                    ((JsonArray) element).add((Number) obj);
                } else if (obj instanceof Character) {
                    ((JsonArray) element).add((Character) obj);
                } else if (obj instanceof Boolean) {
                    ((JsonArray) element).add((Boolean) obj);
                } else if (obj instanceof JsonElement) {
                    ((JsonArray) element).add((JsonElement) obj);
                } else {
                    ((JsonArray) element).add(obj.toString());
                }
            }

        }

        /**
         * 产生一个新的Json对象子集
         */
        public Builder groupJson(@NonNull String key) {
            JsonObject element = new JsonObject();
            add(key, element);
            subElementStack.push(element);
            return this;
        }

        /**
         * 产生一个新的Json数组对象子集
         */
        public Builder groupArray(@NonNull String key) {
            JsonArray element = new JsonArray();
            add(key, element);
            subElementStack.push(element);
            return this;
        }

        /**
         * 结束新对象
         */
        public Builder endGroup() {
            subElementStack.pop();
            return this;
        }

        public Builder add(@NonNull String key, @Nullable Boolean bool) {
            operateElement(getOperateElement(), key, bool);
            return this;

        }

        public Builder add(@NonNull String key, @Nullable Character character) {
            operateElement(getOperateElement(), key, character);
            return this;

        }

        public Builder add(@NonNull String key, @Nullable Number number) {
            operateElement(getOperateElement(), key, number);
            return this;

        }

        public Builder add(@NonNull String key, @Nullable String string) {
            operateElement(getOperateElement(), key, string);
            return this;
        }

        public Builder add(@NonNull String key, @Nullable JsonElement element) {
            operateElement(getOperateElement(), key, element);
            return this;
        }

        public JsonElement build() {
            return rootElement;
        }

        public String get() {
            return rootElement.toString();
        }

        private void checkRootElement() {
            if (rootElement == null) {
                throw new NullPointerException("你需要先调用 json() or array() 方法.");
            }
        }
    }
}
