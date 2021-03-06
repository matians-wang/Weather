package com.test.carweather.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

public class ModelManager {
    private static Map<String, BaseModel> sModelMap = new ConcurrentHashMap<String, BaseModel>();

    private ModelManager() {
    }

    public static <T extends BaseModel> T getModel(Class<T> modelType) {
        String modelName = modelType.getName();
        T model;
        BaseModel baseModel = sModelMap.get(modelName);
        if (modelType.isInstance(baseModel)) {
            model = (T) baseModel;
            return model;
        }
        try {
            model = modelType.newInstance();
            model.onCreate();
            sModelMap.put(modelName, model);
        } catch (Exception e) {
            model = null;
        }
        return model;
    }

    public static <T extends BaseModel> void unSubscribe(Class<T> modelType) {

        BaseModel model = sModelMap.get(modelType.getName());
        if (model != null) {
            model.onDestroy();
        }
        sModelMap.remove(modelType.getName());
    }

    static void unSubscribeAll() {
        for (String clsName : sModelMap.keySet()) {
            BaseModel model = sModelMap.get(clsName);
            if (model != null) {
                model.onDestroy();
            }
            sModelMap.remove(clsName);
        }
        sModelMap.clear();
    }
}
