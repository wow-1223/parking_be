package com.parking.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("转换JSON字符串失败", e);
            throw new RuntimeException("转换JSON字符串失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("解析JSON字符串失败", e);
            throw new RuntimeException("解析JSON字符串失败", e);
        }
    }

    public static <T> T fromJson(String json, TypeToken<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type.getType());
    }

//    public static <T> T fromJson(String json, Class<T> t) {
//        Type type = new TypeToken<T>(){}.getType();
//        Gson gson = new Gson();
//        return gson.fromJson(json, type);
//    }

    public static <T> List<T> fromListJson(String json, Class<T> t) {
        Type type = new TypeToken<List<T>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static <K, V> Map<K, V> fromMapJson(String json, Class<K> k, Class<V> v) {
        Type type = new TypeToken<Map<K, V>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
}