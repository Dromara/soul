/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.dromara.soul.common.constant.Constants;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * GSONUtils.
 *
 * @author xiaoyu(Myth)
 */
public class GsonUtils {

    private static final GsonUtils INSTANCE = new GsonUtils();

    /**
     * The constant STRING.
     */
    private static final TypeAdapter<String> STRING = new StringTypeAdapter();

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(String.class, STRING).create();

    private static final Gson GSON_MAP = new GsonBuilder().serializeNulls().registerTypeHierarchyAdapter(new TypeToken<Map<String, Object>>() {
    }.getRawType(), new MapDeserializer<String, Object>()).create();

    private static final String DOT = ".";

    private static final String E = "e";

    /**
     * Gets gson instance.
     *
     * @return the instance
     */
    public static Gson getGson() {
        return GsonUtils.GSON;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static GsonUtils getInstance() {
        return INSTANCE;
    }

    /**
     * To json string.
     *
     * @param object the object
     * @return the string
     */
    public String toJson(final Object object) {
        return GSON.toJson(object);
    }

    /**
     * From json t.
     *
     * @param <T>    the type parameter
     * @param json   the json
     * @param tClass the t class
     * @return the t
     */
    public <T> T fromJson(final String json, final Class<T> tClass) {
        return GSON.fromJson(json, tClass);
    }

    /**
     * From json t.
     *
     * @param <T>         the type parameter
     * @param jsonElement the json element
     * @param tClass      the t class
     * @return the t
     */
    public <T> T fromJson(final JsonElement jsonElement, final Class<T> tClass) {
        return GSON.fromJson(jsonElement, tClass);
    }

    /**
     * From list list.
     *
     * @param <T>   the type parameter
     * @param json  the json
     * @param clazz the clazz
     * @return the list
     */
    public <T> List<T> fromList(final String json, final Class<T> clazz) {
        return GSON.fromJson(json, TypeToken.getParameterized(List.class, clazz).getType());
    }

    /**
     * toGetParam.
     *
     * @param json json
     * @return java.lang.String string
     */
    public String toGetParam(final String json) {
        if (StringUtils.isBlank(json)) {
            return "";
        }
        final Map<String, String> map = toStringMap(json);
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((k, v) -> {
            try {
                stringBuilder.append(k)
                        .append("=")
                        .append(URLDecoder.decode(v, Constants.DECODE))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        final String r = stringBuilder.toString();
        return r.substring(0, r.lastIndexOf("&"));

    }

    /**
     * toMap.
     *
     * @param json json
     * @return hashMap map
     */
    private Map<String, String> toStringMap(final String json) {
        return GSON.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    /**
     * toList Map.
     *
     * @param json json
     * @return hashMap list
     */
    public List<Map<String, Object>> toListMap(final String json) {
        return GSON.fromJson(json, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
    }

    /**
     * To object map map.
     *
     * @param json the json
     * @return the map
     */
    public Map<String, Object> toObjectMap(final String json) {
        return GSON_MAP.fromJson(json, new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType());
    }

    /**
     *  To object map map.
     *
     * @param json  the json
     * @param clazz the class
     * @param <T>   the class
     * @return the map
     */
    public <T> Map<String, T> toObjectMap(final String json, final Class<T> clazz) {
        return GSON.fromJson(json, TypeToken.getParameterized(Map.class, String.class, clazz).getType());
    }

    /**
     *  To object map list.
     *
     * @param json  the json
     * @param clazz the class
     * @param <T>   the class
     * @return the map
     */
    public <T> Map<String, List<T>> toObjectMapList(final String json, final Class<T> clazz) {
        return GSON.fromJson(json, TypeToken.getParameterized(Map.class, String.class, TypeToken.getParameterized(List.class, clazz).getType()).getType());
    }

    /**
     * To tree map tree map.
     *
     * @param json the json
     * @return the tree map
     */
    public ConcurrentNavigableMap<String, Object> toTreeMap(final String json) {
        return GSON_MAP.fromJson(json, new TypeToken<ConcurrentSkipListMap<String, Object>>() {
        }.getType());
    }

    /**
     * Convert to map map.
     *
     * @param json the json
     * @return the map
     */
    public Map<String, Object> convertToMap(final String json) {
        Map<String, Object> map = GSON_MAP.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

        if (map == null || map.isEmpty()) {
            return map;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                String valueStr = ((String) value).trim();
                if (valueStr.startsWith("{") && valueStr.endsWith("}")) {
                    Map<String, Object> mv = convertToMap(value.toString());
                    map.put(key, mv);
                }
            } else if (value instanceof JsonObject) {
                map.put(key, convertToMap(value.toString()));
            } else if (value instanceof JsonArray) {
                JsonArray jsonArray = (JsonArray) value;
                map.put(key, jsonArrayToListInConvertToMap(jsonArray));
            } else if (value instanceof JsonNull) {
                map.put(key, null);
            }
        }

        return map;
    }

    /**
     * translate JsonArray in covertToMap of Method.
     *
     * @param jsonArray the Gson's Object {@link com.google.gson.JsonArray}
     * @return list about translating jsonArray
     */
    private List<Object> jsonArrayToListInConvertToMap(final JsonArray jsonArray) {
        List<Object> list = new ArrayList<>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            String objStr = jsonElement.getAsString();
            if (objStr.startsWith("{") && objStr.endsWith("}")) {
                list.add(convertToMap(jsonElement.toString()));
            } else {
                list.add(objStr);
            }
        }

        return list;
    }

    private static class MapDeserializer<T, U> implements JsonDeserializer<Map<T, U>> {
        @SuppressWarnings("unchecked")
        @SneakyThrows
        @Override
        public Map<T, U> deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) {
            if (!json.isJsonObject()) {
                return null;
            }

            JsonObject jsonObject = json.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> jsonEntrySet = jsonObject.entrySet();

            String className = ((ParameterizedType) type).getRawType().getTypeName();
            Class<Map<?, ?>> mapClass = (Class<Map<?, ?>>) Class.forName(className);

            Map<T, U> resultMap;
            if (mapClass.isInterface()) {
                resultMap = new LinkedHashMap<>();
            } else {
                resultMap = (Map<T, U>) mapClass.getConstructor().newInstance();
            }

            for (Map.Entry<String, JsonElement> entry : jsonEntrySet) {
                if (entry.getValue().isJsonNull()) {
                    resultMap.put((T) entry.getKey(), null);
                } else {
                  U value = context.deserialize(entry.getValue(), this.getType(entry.getValue()));
                  resultMap.put((T) entry.getKey(), value);
                }
            }

            return resultMap;
        }

        /**
         * Get JsonElement class type.
         *
         * @param element the element
         * @return Class class
         */
        public Class<?> getType(final JsonElement element) {
            if (!element.isJsonPrimitive()) {
                return element.getClass();
            }

            final JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return String.class;
            }
            if (primitive.isNumber()) {
                String numStr = primitive.getAsString();
                if (numStr.contains(DOT) || numStr.contains(E)
                        || numStr.contains("E")) {
                    return Double.class;
                }
                return Long.class;
            }
            if (primitive.isBoolean()) {
                return Boolean.class;
            }
            return element.getClass();
        }
    }

    private static class StringTypeAdapter extends TypeAdapter<String> {
        @SneakyThrows
        @Override
        public void write(final JsonWriter out, final String value) {
            if (StringUtils.isBlank(value)) {
                out.nullValue();
                return;
            }
            out.value(value);
        }

        @SneakyThrows
        @Override
        public String read(final JsonReader reader) {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }
    }
}
