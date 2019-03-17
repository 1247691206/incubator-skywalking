package org.apache.skywalking.apm.plugin.thrift.define.server;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;


/**
 * Created by pengfeining on 2019/3/12 0012.
 * 
 * 注意pinpoint添加了字段 target.addField(ThriftConstants.FIELD_ACCESSOR_SERVER_MARKER_FLAG);
 */
public class ProcessFunctionProcessInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {
    
    private static final String SERVICE_SERVER_ENHANCE_CLASS = "org.apache.thrift.ProcessFunction";
    
    private static final String SERVICE_SERVER_process_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.server.ProcessFunctionProcessInterceptor";
    
    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_SERVER_ENHANCE_CLASS);
    }
    
    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }
    
    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("process");
                }
                
                @Override
                public String getMethodsInterceptor() {
                    return SERVICE_SERVER_process_INTERCEPT_CLASS;
                }
                
                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
