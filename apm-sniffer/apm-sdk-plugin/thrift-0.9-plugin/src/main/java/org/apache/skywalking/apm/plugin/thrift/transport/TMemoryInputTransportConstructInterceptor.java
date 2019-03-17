package org.apache.skywalking.apm.plugin.thrift.transport;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;


/**
 * Created by pengfeining on 2019/3/13 0013.
 */
// 只是添加一个字段
public class TMemoryInputTransportConstructInterceptor implements InstanceConstructorInterceptor {
    
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        objInst.setSkyWalkingDynamicField(null);
    }
}
