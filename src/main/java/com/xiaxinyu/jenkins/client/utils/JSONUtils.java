package com.xiaxinyu.jenkins.client.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaxinyu.jenkins.client.core.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Json操作工具类
 *
 * @author XIAXINYU3
 * @date 2019.6.8
 */
public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);
    public static final JSONObject EMPTY = JSONObject.parseObject("{}");

    /**
     * 构造正确处理返回格式
     *
     * @param data 返回数据
     * @return
     */
    public static JSONObject getSuccess(Object data) {
        JSONObject ret = new JSONObject();
        ret.put("code", Constants.SUCCESS);
        ret.put("data", data);
        return ret;
    }

    /**
     * 构造错误处理返回格式
     *
     * @param msg 错误信息
     * @return
     */
    public static JSONObject getFailure(String msg) {
        JSONObject ret = new JSONObject();
        ret.put("code", Constants.FAILURE);
        ret.put("msg", msg);
        return ret;
    }

    /**
     * 构造无权限处理返回格式
     *
     * @param msg 错误信息
     * @return
     */
    public static JSONObject getUnAuthorized(String msg) {
        JSONObject ret = new JSONObject();
        ret.put("code", Constants.UNAUTHORIZED);
        ret.put("msg", msg);
        return ret;
    }

    /**
     * 根据指定的属性重新组装JSONArray数组
     *
     * @param data
     * @param attrs
     * @return
     */
    public static JSONArray copyProperties(JSONArray data, String[] attrs) {
        JSONArray ret = new JSONArray();
        for (int i = 0; i < data.size(); i++) {
            ret.add(copyProperties(data.getJSONObject(i), attrs));
        }
        return ret;
    }

    /**
     * 根据指定属性重新组装JSON对象
     *
     * @param data
     * @param attrs
     * @return
     */
    public static JSONObject copyProperties(JSONObject data, String[] attrs) {
        JSONObject ret = new JSONObject();
        for (String key : attrs) {
            if (data.containsKey(key)) {
                ret.put(key, data.get(key));
            } else {
                logger.info("属性(" + key + ")不存在，被忽略!");
            }
        }
        return ret;
    }

    /**
     * @param obj
     * @param attrName
     * @return
     * @Description 判断属性是否为空
     */
    public static boolean isNotBlank(JSONObject obj, String attrName) {
        if (obj.containsKey(attrName)) {
            Object temp = obj.get(attrName);
            if (temp instanceof String) {
                if (StringUtils.isNotBlank((String) temp)) {
                    return true;
                }
            } else if (temp instanceof JSONObject) {
                if (!((JSONObject) temp).isEmpty()) {
                    return true;
                }
            } else if (temp instanceof JSONArray) {
                if (!((JSONArray) temp).isEmpty()) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * @param obj
     * @param attrName
     * @return
     * @Description 判定属性是否是JSONObject
     */
    public static boolean isJSONObject(JSONObject obj, String attrName) {
        if (obj != null && obj.containsKey(attrName)) {
            return (obj.get(attrName) instanceof JSONObject);
        }
        return false;
    }

    /**
     * 检查属性是否存在以及类型是否匹配
     *
     * @param obj
     * @param attrName
     * @param cls
     */
    public static <T> T checkNullAndType(JSONObject obj, String attrName, Class<? extends T> cls) {
        if (obj != null) {
            if (obj.containsKey(attrName)) {
                Object value = obj.get(attrName);
                Class<?> realCls = value.getClass();
                if (realCls.isAssignableFrom(cls)) {
                    return (T) value;
                }
                throw new RuntimeException("属性(" + attrName + ")类型错误，期望的类型:" + cls.getName() + ",实际类型:" + realCls.getName());
            } else {
                throw new RuntimeException("属性(" + attrName + ")不存在!");
            }

        } else {
            throw new RuntimeException("第一个参数obj不能为空!");
        }
    }

    /**
     * 获取值
     *
     * @param obj      JSON对象
     * @param attrName JSON对象中的属性名称
     * @return
     */
    public static Object getValue(JSONObject obj, String attrName) {
        Object result = null;
        if (obj != null) {
            if (obj.containsKey(attrName)) {
                result = obj.get(attrName);
            }
        } else {
            throw new RuntimeException("第一个参数obj不能为空!");
        }
        return result;
    }

    /**
     * 校验JSON不能为空的属性
     *
     * @param obj
     * @param properties
     * @return
     */
    public static String checkPropertiesRetMsg(JSONObject obj, Object[] properties) {
        StringBuilder msg = new StringBuilder();
        for (Object key : properties) {
            if (!obj.containsKey(key)) {
                msg.append("属性" + key + "不能为空,");
                continue;
            }
        }
        if (msg.length() > 0) {
            return msg.delete(msg.length() - 1, msg.length()).toString();
        }
        return null;
    }

    /**
     * 校验JSON不能为空的属性
     *
     * @param obj
     * @param properties
     * @return
     */
    public static void checkProperties(JSONObject obj, Object[] properties) {
        String msg = checkPropertiesRetMsg(obj, properties);
        if (msg != null && msg.length() > 0) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * 将对象转换成JSON字符串
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String obj2JsonStr(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    /**
     * 将对象转换成json对象
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static JSONObject obj2Json(Object obj) throws JsonProcessingException {
        String strJson = obj2JsonStr(obj);
        return com.alibaba.fastjson.JSON.parseObject(strJson);
    }

    /**
     * 将集合转换成JSON数组
     *
     * @param ls
     * @return
     * @throws JsonProcessingException
     */
    public static JSONArray ls2Json(List ls) throws JsonProcessingException {
        String strJson = obj2JsonStr(ls);
        return com.alibaba.fastjson.JSON.parseArray(strJson);
    }


    /**
     * 解析jsonObject 为目标类
     *
     * @param jsonObject
     * @param targetClass
     * @param map         里面嵌套的实体类
     * @return
     */
    public static <T> T jsonToObject(JSONObject jsonObject, Class<T> targetClass, Map<String, Class> map) throws Exception {

        if (null != jsonObject) {

            return (T) net.sf.json.JSONObject.toBean(fastJsonToSfJson(jsonObject), targetClass, map);
        }
        return null;

    }


    public static net.sf.json.JSONObject fastJsonToSfJson(JSONObject jsonObject) {

        if (jsonObject != null) {

            return net.sf.json.JSONObject.fromObject(jsonObject);
        }
        return null;
    }
}
