package org.apache.skywalking.apm.plugin.thrift.tprotocol;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.plugin.thrift.utils.Tools;

import java.util.Map;


/**
 * Created by pengfeining on 2019/3/13 0013.
 */
public class TProtocolDecoratorInterceptor implements InstanceConstructorInterceptor {
    
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        // 获取协议字段值 写入动态字段中 concreteProtocol
        Map<String, Object> fields = Tools.getObjectFields(objInst);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if ("concreteProtocol".equalsIgnoreCase(entry.getKey())) {
                objInst.setSkyWalkingDynamicField(entry.getValue());
            }
        }
    }
}
