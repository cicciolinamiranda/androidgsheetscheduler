package com.google.scheduler.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.scheduler.client.ApiException;
import com.google.scheduler.view.DataModel;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Created by cicciolina on 6/11/18.
 */

public class JsonUtil {
    public static GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        gsonBuilder.excludeFieldsWithoutExposeAnnotation();

    }

    public static Gson getGson() {
        return gsonBuilder.create();
    }

    public static <T> T deserializeToList(String jsonString, Class cls){
        return getGson().fromJson(jsonString, getListTypeForDeserialization(cls));
    }

    public static <T> T deserializeToObject(String jsonString, Class cls){
        return getGson().fromJson(jsonString, getTypeForDeserialization(cls));
    }

    public static Type getListTypeForDeserialization(Class cls) {
        String className = cls.getSimpleName();

        if ("DataModel".equalsIgnoreCase(className)) {
            return new TypeToken<List<DataModel>>(){}.getType();
        }

        if ("DateTime".equalsIgnoreCase(className)) {
            return new TypeToken<List<DateTime>>(){}.getType();
        }


        return new TypeToken<List<Object>>(){}.getType();
    }

    public static Type getTypeForDeserialization(Class cls) {
        String className = cls.getSimpleName();

        if ("DataModel".equalsIgnoreCase(className)) {
            return new TypeToken<DataModel>(){}.getType();
        }

        if ("DateTime".equalsIgnoreCase(className)) {
            return new TypeToken<DateTime>(){}.getType();
        }
        return new TypeToken<Object>(){}.getType();
    }

    public static Object deserialize(String json, String containerType, Class cls) throws ApiException {
        try {
            if ("list".equalsIgnoreCase(containerType) || "array".equalsIgnoreCase(containerType)) {
                return JsonUtil.deserializeToList(json, cls);
            } else if (String.class.equals(cls)) {
                if (json != null && json.startsWith("\"") && json.endsWith("\"") && json.length() > 1)
                    return json.substring(1, json.length() - 1);
                else
                    return json;
            }
            else {
                return JsonUtil.deserializeToObject(json, cls);
            }
        } catch (JsonParseException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public static String serializeJSonStringToGSon(Object obj){
        return getGson().toJson(obj);
    }

    public static String serialize(Object obj) throws ApiException {
        try {
            if (obj != null)
                return JsonUtil.serializeJSonStringToGSon(obj);
            else
                return null;
        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

};
