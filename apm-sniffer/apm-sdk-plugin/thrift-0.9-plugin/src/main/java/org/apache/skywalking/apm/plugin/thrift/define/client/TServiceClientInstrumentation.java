package org.apache.skywalking.apm.plugin.thrift.define.client;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class TServiceClientInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String SERVICE_CLIENT_ENHANCE_CLASS = "org.apache.thrift.TServiceClient";
    private static final String SERVICE_CLIENT_SendBase_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.TServiceClientSendBaseInterceptor";

    private static final String SERVICE_CLIENT_receiveBase_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.TServiceClientReceiveBaseInterceptor";



    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("sendBase");
                }

                @Override
                public String getMethodsInterceptor() {
                    return SERVICE_CLIENT_SendBase_INTERCEPT_CLASS;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            },
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("receiveBase");
                }

                @Override
                public String getMethodsInterceptor() {
                    return SERVICE_CLIENT_receiveBase_INTERCEPT_CLASS;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_CLIENT_ENHANCE_CLASS);
    }
}
