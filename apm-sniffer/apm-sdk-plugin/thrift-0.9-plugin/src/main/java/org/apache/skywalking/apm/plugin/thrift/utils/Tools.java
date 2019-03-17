package org.apache.skywalking.apm.plugin.thrift.utils;

import org.apache.skywalking.apm.plugin.thrift.field.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by pengfeining on 2019/3/4 0004.
 */
public class Tools {
    
    public final static Logger logger = LoggerFactory.getLogger(Tools.class);

    /**
     * 获取字段名 以及字段值
     * 
     * @param object
     * @return
     */
    public static Map<String, Object> getObjectFields(Object object) {
        Map<String, Object> fields = new HashMap<String, Object>();
        if (object == null) {
            return fields;
        }
        Field[] field = object.getClass().getDeclaredFields();
        for (int i = 0; i < field.length; i++) {
            // 设置是否允许访问，不是修改原来的访问权限修饰词。
            field[i].setAccessible(true);
            // 返回输出指定对象a上此 Field表示的字段名和字段值
            try {
                fields.put(field[i].getName(), field[i].get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }
        return fields;
    }
    public static ByteBuffer stringToByteBuffer(String s) {
        return ByteBuffer.wrap(s.getBytes(HEADER_CHARSET_ENCODING));
    }

    private static final Charset HEADER_CHARSET_ENCODING = Charsets.UTF_8;

}
