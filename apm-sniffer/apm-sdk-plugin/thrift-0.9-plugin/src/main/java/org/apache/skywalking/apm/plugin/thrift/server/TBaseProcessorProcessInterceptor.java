package org.apache.skywalking.apm.plugin.thrift.server;

import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftConstants;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftUtils;
import org.apache.thrift.TBaseProcessor;

import java.lang.reflect.Method;


public class TBaseProcessorProcessInterceptor implements InstanceMethodsAroundInterceptor {
    
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInterceptResult result) throws Throwable {
        String methodUri = ThriftConstants.UNKNOWN_METHOD_URI;
        //todo methodName 存在问题
        String methodName = ThriftConstants.UNKNOWN_METHOD_NAME;
        if (allArguments[0] instanceof String) {
            methodName = (String) allArguments[0];
        }
        if (objInst instanceof TBaseProcessor) {
            methodUri = ThriftUtils.getProcessorNameAsUri((TBaseProcessor<?>) objInst);
            StringBuilder sb = new StringBuilder(methodUri);
            if (!methodUri.endsWith("/")) {
                sb.append("/");
            }
            sb.append(methodName);
            methodUri = sb.toString();
        }
        ContextCarrier contextCarrier = new ContextCarrier();
        CarrierItem next = contextCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            //todo 从中获取 span 信息 next.getHeadKey()
            next.setHeadValue("");
        }
        
        AbstractSpan span = ContextManager.createEntrySpan(generateViewPoint(methodUri, methodName, ""), contextCarrier);
        SpanLayer.asRPCFramework(span);
        span.setComponent(ComponentsDefine.THRIFT);
    }
    
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret)
        throws Throwable {
        Boolean response = (Boolean) ret;
        if (response != null && !response) {
            AbstractSpan span = ContextManager.activeSpan();
            span.log(new Throwable("skywalking log process failed"));
            span.errorOccurred();
        }
        ContextManager.stopSpan();
        return ret;
    }
    
    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Throwable t) {
        AbstractSpan activeSpan = ContextManager.activeSpan();
        activeSpan.errorOccurred();
        activeSpan.log(t);
    }
    
    private static String generateViewPoint(String interfaceName, String methodName, String paramtersDesc) {
        StringBuilder viewPoint = new StringBuilder(interfaceName);
        viewPoint.append("." + methodName);
        viewPoint.append("(" + paramtersDesc + ")");
        return viewPoint.toString();
    }
}
