package org.apache.skywalking.apm.plugin.thrift.server;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftConstants;
import org.apache.thrift.ProcessFunction;
import org.apache.thrift.protocol.TProtocol;

import java.lang.reflect.Method;


/**
 * Created by pengfeining on 2019/3/12 0012.
 */
public class ProcessFunctionProcessInterceptor implements InstanceMethodsAroundInterceptor {
    
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInterceptResult result) throws Throwable {
        // process(int seqid, TProtocol iprot, TProtocol oprot, I iface)
        if (allArguments.length != 4) {
            return;
        }
        String methodName = ThriftConstants.UNKNOWN_METHOD_NAME;
        if (objInst instanceof ProcessFunction) {
            ProcessFunction<?, ?> processFunction = (ProcessFunction<?, ?>) objInst;
            methodName = processFunction.getMethodName();
        }
        
        // Set server marker - server handlers may create a client to call another Thrift server.
        // When this happens, TProtocol interceptors for clients are triggered since technically they're still within
        // THRIFT_SERVER_SCOPE.
        // We set the marker inside server's input protocol to safeguard against such cases.
        Object iprot = allArguments[1];
        // With the addition of TProtocolDecorator, iprot may actually be a wrapper around the actual input protocol
        Object rootInputProtocol = getRootInputProtocol(iprot);
        if (validateInputProtocol(rootInputProtocol)) {
            ((EnhancedInstance) rootInputProtocol).setSkyWalkingDynamicField(true);
        }
        // 留给读字段的readFieldBegin来用 以及methodName的作用
    }
    
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret)
        throws Throwable {
        if (allArguments.length != 4) {
            Object iprot = allArguments[1];
            if (validateInputProtocol(iprot)) {
                ((EnhancedInstance) iprot).setSkyWalkingDynamicField(false);
            }
        }
        return ret;
    }
    
    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Throwable t) {
        
    }
    
    private Object getRootInputProtocol(Object iprot) {
        if (iprot instanceof EnhancedInstance) {
            return getRootInputProtocol(((EnhancedInstance) iprot).getSkyWalkingDynamicField());
        } else {
            return iprot;
        }
    }
    
    private boolean validateInputProtocol(Object iprot) {
        if (iprot instanceof TProtocol) {
            return true;
        }
        return false;
    }
}
