package com.angcyo.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import com.angcyo.http.log.LogUtil;
import com.angcyo.http.type.TypeBuilder;
import com.google.gson.*;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
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
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, TypeBuilder.newInstance(List.class).addTypeParam(type).build());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static <T> T from(String json, Class<T> type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public interface Call {
        void call(Builder builder);
    }

    public static class Builder {
        final static String TAG = "JsonBuilder";
        JsonElement rootElement;
        Stack<JsonElement> subElementStack;

        /**
         * 当对象的值为null时, 是否忽略, 如果是json对象, 那么"{}"也会被忽略
         */
        boolean ignoreNull = true;

        /**
         * 当调用 get() 或者 build() 时, 是否自动调用 endAdd().
         * <p>
         * 这种情况只适合 末尾全是 endAdd().endAdd().endAdd().endAdd()...的情况
         */
        boolean autoEnd = true;

        private Builder() {
            subElementStack = new Stack<>();
        }

        /**
         * 操作[JsonObject]
         */
        private static void operateElement(@Nullable JsonElement element, @NonNull String key, @Nullable Object obj, boolean ignoreNull) {
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
                    if (ignoreNull) {
                        if (!TextUtils.equals(obj.toString(), "{}")) {
                            ((JsonObject) element).add(key, (JsonElement) obj);
                        }
                    } else {
                        ((JsonObject) element).add(key, (JsonElement) obj);
                    }
                } else {
                    ((JsonObject) element).addProperty(key, obj.toString());
                }
            } else if (element instanceof WrapJsonObject) {
                operateElement(((WrapJsonObject) element).originElement, key, obj, ignoreNull);
            } else {
                LogUtil.w(TAG, " 当前操作已被忽略:" + key + "->" + obj);
            }
        }

        /**
         * 操作[JsonArray]
         */
        private static void operateElement(@Nullable JsonElement element, @Nullable Object obj, boolean ignoreNull) {
            if (element instanceof JsonArray) {
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
                    if (ignoreNull) {
                        if (!TextUtils.equals(obj.toString(), "{}")) {
                            ((JsonArray) element).add((JsonElement) obj);
                        }
                    } else {
                        ((JsonArray) element).add((JsonElement) obj);
                    }
                } else {
                    ((JsonArray) element).add(obj.toString());
                }
            } else if (element instanceof WrapJsonArray) {
                operateElement(((WrapJsonArray) element).originElement, obj, ignoreNull);
            } else {
                LogUtil.w(TAG, " 当前操作已被忽略:" + obj);
            }
        }

        private static boolean isArray(JsonElement element) {
            return (element instanceof JsonArray ||
                    element instanceof WrapJsonArray);
        }

        private static boolean isObj(JsonElement element) {
            return (element instanceof JsonObject ||
                    element instanceof WrapJsonObject);
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

        public Builder autoEnd(boolean autoEnd) {
            this.autoEnd = autoEnd;
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

        private boolean isArray() {
            return isArray(getOperateElement());
        }

        private boolean isObj() {
            return isObj(getOperateElement());
        }

        /**
         * 产生一个新的Json对象子集, 适用于 Json对象中 key 对应的 值是json
         */
        public Builder addJson(@NonNull String key) {
            if (isArray()) {
                throw new IllegalArgumentException("不允许在Json数组中, 使用此方法. 请尝试使用 addJson() 方法");
            }
            subElementStack.push(new WrapJsonObject(getOperateElement(), key));
            return this;
        }

        /**
         * 产生一个新的Json对象子集, 适用于 Json 数组中 值是json
         */
        public Builder addJson() {
            if (isObj()) {
                throw new IllegalArgumentException("不允许在Json对象中, 使用此方法. 请尝试使用 addJson(String) 方法");
            }
            subElementStack.push(new WrapJsonObject(getOperateElement(), null));
            return this;
        }

        /**
         * 产生一个新的Json数组对象子集, 适用于Json中添加数组
         */
        public Builder addArray(@NonNull String key) {
            if (isArray()) {
                throw new IllegalArgumentException("不允许在Json数组中, 使用此方法. 请尝试使用 addArray() 方法");
            }
            subElementStack.push(new WrapJsonArray(getOperateElement(), key));
            return this;
        }

        /**
         * 产生一个新的Json数组对象子集, 适用于数组中添加数组
         */
        public Builder addArray() {
            if (isObj()) {
                throw new IllegalArgumentException("不允许在Json对象中, 使用此方法. 请尝试使用 addArray(String) 方法");
            }
            subElementStack.push(new WrapJsonArray(getOperateElement(), null));
            return this;
        }

        public Builder addArray(@NonNull String key, @NonNull Call call) {
            addArray(key);
            call(call);
            return this;
        }

        public Builder addArray(@NonNull Call call) {
            addArray();
            call(call);
            return this;
        }

        /**
         * 结束新对象, 有多少个add操作, 就需要有多少个end操作
         */
        public Builder endAdd() {
            if (!subElementStack.isEmpty()) {
                JsonElement pop = subElementStack.pop();
                if (pop instanceof WrapJsonElement) {
                    JsonElement origin = ((WrapJsonElement) pop).originElement;
                    JsonElement parent = ((WrapJsonElement) pop).parentElement;
                    String key = ((WrapJsonElement) pop).key;

                    if (isArray(parent)) {
                        operateElement(parent, origin, ignoreNull);
                    } else {
                        operateElement(parent, key, origin, ignoreNull);
                    }
                }
            } else {
                LogUtil.w(TAG, "不合法的操作, 请检查!");
            }
            return this;
        }

        public Builder add(@NonNull String key, @Nullable Boolean bool) {
            operateElement(getOperateElement(), key, bool, ignoreNull);
            return this;

        }

        public Builder add(@NonNull String key, @Nullable Character character) {
            operateElement(getOperateElement(), key, character, ignoreNull);
            return this;

        }

        public Builder add(@NonNull String key, @Nullable Number number) {
            operateElement(getOperateElement(), key, number, ignoreNull);
            return this;

        }

        public Builder add(@NonNull String key, @Nullable String string) {
            operateElement(getOperateElement(), key, string, ignoreNull);
            return this;
        }

        public Builder add(@NonNull String key, @Nullable JsonElement element) {
            operateElement(getOperateElement(), key, element, ignoreNull);
            return this;
        }

        public Builder add(@Nullable Boolean bool) {
            operateElement(getOperateElement(), bool, ignoreNull);
            return this;

        }

        public Builder add(@Nullable Character character) {
            operateElement(getOperateElement(), character, ignoreNull);
            return this;

        }

        public Builder add(@Nullable Number number) {
            operateElement(getOperateElement(), number, ignoreNull);
            return this;

        }

        public Builder add(@Nullable String string) {
            operateElement(getOperateElement(), string, ignoreNull);
            return this;
        }

        public Builder add(@Nullable JsonElement element) {
            operateElement(getOperateElement(), element, ignoreNull);
            return this;
        }

        /**
         * 回调出去
         */
        public Builder call(@NonNull Call action) {
            action.call(this);
            return this;
        }

        /**
         * 结束所有
         */
        public Builder endAll() {
            while (!subElementStack.isEmpty()) {
                endAdd();
            }
            return this;
        }

        private void checkEnd() {
            if (autoEnd) {
                endAll();
            }
        }

        public JsonElement build() {
            checkEnd();
            return rootElement;
        }

        public String get() {
            checkEnd();
            return rootElement.toString();
        }

        private void checkRootElement() {
            if (rootElement == null) {
                throw new NullPointerException("你需要先调用 json() or array() 方法.");
            }
        }

        private static class WrapJsonElement extends JsonElement {
            String key;
            JsonElement originElement;
            JsonElement parentElement;

            public WrapJsonElement(JsonElement parentElement, JsonElement originElement, @Nullable String key) {
                this.originElement = originElement;
                this.parentElement = parentElement;
                this.key = key;
            }

            @Override
            public JsonElement deepCopy() {
                return originElement.deepCopy();
            }


            @Override
            public boolean isJsonArray() {
                return originElement.isJsonArray();
            }

            @Override
            public boolean isJsonObject() {
                return originElement.isJsonObject();
            }

            @Override
            public boolean isJsonPrimitive() {
                return originElement.isJsonPrimitive();
            }

            @Override
            public boolean isJsonNull() {
                return originElement.isJsonNull();
            }

            @Override
            public JsonObject getAsJsonObject() {
                return originElement.getAsJsonObject();
            }

            @Override
            public JsonArray getAsJsonArray() {
                return originElement.getAsJsonArray();
            }

            @Override
            public JsonPrimitive getAsJsonPrimitive() {
                return originElement.getAsJsonPrimitive();
            }

            @Override
            public JsonNull getAsJsonNull() {
                return originElement.getAsJsonNull();
            }

            @Override
            public boolean getAsBoolean() {
                return originElement.getAsBoolean();
            }

            @Override
            public Number getAsNumber() {
                return originElement.getAsNumber();
            }

            @Override
            public String getAsString() {
                return originElement.getAsString();
            }

            @Override
            public double getAsDouble() {
                return originElement.getAsDouble();
            }

            @Override
            public float getAsFloat() {
                return originElement.getAsFloat();
            }

            @Override
            public long getAsLong() {
                return originElement.getAsLong();
            }

            @Override
            public int getAsInt() {
                return originElement.getAsInt();
            }

            @Override
            public byte getAsByte() {
                return originElement.getAsByte();
            }

            @Override
            public char getAsCharacter() {
                return originElement.getAsCharacter();
            }

            @Override
            public BigDecimal getAsBigDecimal() {
                return originElement.getAsBigDecimal();
            }

            @Override
            public BigInteger getAsBigInteger() {
                return originElement.getAsBigInteger();
            }

            @Override
            public short getAsShort() {
                return originElement.getAsShort();
            }

            @Override
            public String toString() {
                return originElement.toString();
            }
        }

        private static class WrapJsonArray extends WrapJsonElement {

            public WrapJsonArray(@NonNull JsonElement parentElement, @Nullable String key) {
                super(parentElement, new JsonArray(), key);
            }

            public void add(JsonElement value) {
                ((JsonArray) originElement).add(value);
            }

            public void addProperty(String value) {
                ((JsonArray) originElement).add(value);
            }

            public void addProperty(Number value) {
                ((JsonArray) originElement).add(value);
            }

            public void addProperty(Boolean value) {
                ((JsonArray) originElement).add(value);
            }

            public void addProperty(Character value) {
                ((JsonArray) originElement).add(value);
            }
        }

        private static class WrapJsonObject extends WrapJsonElement {

            public WrapJsonObject(@NonNull JsonElement parentElement, @Nullable String key) {
                super(parentElement, new JsonObject(), key);
            }

            public void add(String property, JsonElement value) {
                ((JsonObject) originElement).add(property, value);
            }

            public void addProperty(String property, String value) {
                ((JsonObject) originElement).addProperty(property, value);
            }

            public void addProperty(String property, Number value) {
                ((JsonObject) originElement).addProperty(property, value);
            }

            public void addProperty(String property, Boolean value) {
                ((JsonObject) originElement).addProperty(property, value);
            }

            public void addProperty(String property, Character value) {
                ((JsonObject) originElement).addProperty(property, value);
            }
        }
    }

    public static class MapBuilder extends Builder {
        ArrayMap<String, Object> map;

        public MapBuilder(ArrayMap<String, Object> map) {
            this.map = map;
        }

        @Override
        public Builder ignoreNull(boolean ignoreNull) {
            return super.ignoreNull(ignoreNull);
        }

        @Override
        public Builder autoEnd(boolean autoEnd) {
            // no op
            return this;
        }

        @Override
        public Builder addJson(@NonNull String key) {
            // no op
            return this;
        }

        @Override
        public Builder addJson() {
            // no op
            return this;
        }

        @Override
        public Builder addArray(@NonNull String key) {
            // no op
            return this;
        }

        @Override
        public Builder addArray() {
            // no op
            return this;
        }

        @Override
        public Builder addArray(@NonNull String key, @NonNull Call call) {
            // no op
            return this;
        }

        @Override
        public Builder addArray(@NonNull Call call) {
            // no op
            return this;
        }

        @Override
        public Builder endAdd() {
            // no op
            return this;
        }

        @Override
        public Builder add(@NonNull String key, @Nullable Boolean bool) {
            map.put(key, bool);
            return this;
        }

        @Override
        public Builder add(@NonNull String key, @Nullable Character character) {
            map.put(key, character);
            return this;
        }

        @Override
        public Builder add(@NonNull String key, @Nullable Number number) {
            map.put(key, number);
            return this;
        }

        @Override
        public Builder add(@NonNull String key, @Nullable String string) {
            map.put(key, string);
            return this;
        }

        @Override
        public Builder add(@NonNull String key, @Nullable JsonElement element) {
            map.put(key, element);
            return this;
        }

        @Override
        public Builder add(@Nullable Boolean bool) {
            // no op
            return this;
        }

        @Override
        public Builder add(@Nullable Character character) {
            // no op
            return this;
        }

        @Override
        public Builder add(@Nullable Number number) {
            // no op
            return this;
        }

        @Override
        public Builder add(@Nullable String string) {
            // no op
            return this;
        }

        @Override
        public Builder add(@Nullable JsonElement element) {
            // no op
            return this;
        }

        @Override
        public Builder call(@NonNull Call action) {
            // no op
            return this;
        }

        @Override
        public Builder endAll() {
            // no op
            return this;
        }

        @Override
        public JsonElement build() {
            // no op
            return super.build();
        }

        @Override
        public String get() {
            return super.get();
        }
    }
}
