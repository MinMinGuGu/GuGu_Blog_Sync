package com.gugumin.halo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * The type Json util.
 *
 * @author minmin
 * @date 2021 /08/14
 */
public class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Obj 2 json str string.
     *
     * @param obj the obj
     * @return the string
     */
    @SneakyThrows
    public static String obj2Json(Object obj) {
        return JsonTool.INSTANCE.objectMapper.writeValueAsString(obj);
    }

    /**
     * Json 2 obj t.
     *
     * @param <T>         the type parameter
     * @param json        the json
     * @param targetClass the target class
     * @return t
     */
    @SneakyThrows
    public static <T> T json2Obj(String json, Class<T> targetClass) {
        return JsonTool.INSTANCE.objectMapper.readValue(json, targetClass);
    }

    private enum JsonTool {
        /**
         * Instance json tool.
         */
        INSTANCE;

        private final ObjectMapper objectMapper = new ObjectMapper();
    }
}
