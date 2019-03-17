package org.apache.skywalking.apm.plugin.thrift.client;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftConstants;
import org.apache.skywalking.apm.plugin.thrift.utils.Tools;

import java.net.URL;
import java.util.Map;


/**
 * Created by pengfeining on 2019/3/12 0012.
 */

// 添加一个字段获取逻辑 url_
public class THttpClientInterceptor implements InstanceConstructorInterceptor {
    
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        Map<String, Object> fields = Tools.getObjectFields(objInst);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(ThriftConstants.T_HTTP_CLIENT_FIELD_URL_)) {
                if (entry.getValue() != null && entry.getValue() instanceof URL) {
                    objInst.setSkyWalkingDynamicField((URL) entry.getValue());
                }
            }
        }
    }
}
