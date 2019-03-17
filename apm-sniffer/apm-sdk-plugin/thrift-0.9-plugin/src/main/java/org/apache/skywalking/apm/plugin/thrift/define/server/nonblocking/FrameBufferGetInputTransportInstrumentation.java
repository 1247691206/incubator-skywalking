package org.apache.skywalking.apm.plugin.thrift.define.server.nonblocking;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Created by pengfeining on 2019/3/13 0013.
 *
 target.addField(ThriftConstants.FIELD_ACCESSOR_SOCKET);
 target.addGetter(ThriftConstants.FIELD_GETTER_T_NON_BLOCKING_TRANSPORT, ThriftConstants.FRAME_BUFFER_FIELD_TRANS_);

 */
public class FrameBufferGetInputTransportInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String SERVICE_SERVER_ENHANCE_CLASS = "org.apache.thrift.server.AbstractNonblockingServer$FrameBuffer";

    // 0.8.0, 0.9.0 doesn't have a separate trans_ field - hook getInputTransport() method
    // AbstractNonblockingServer$FrameBuffer.getInputTransport(TTransport)
    private static final String SERVICE_SERVER_Construct_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.server.nonblocking.FrameBufferGetInputTransportInterceptor";


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
        return new InstanceMethodsInterceptPoint[]{new InstanceMethodsInterceptPoint() {
            @Override
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("getInputTransport");
            }

            @Override
            public String getMethodsInterceptor() {
                return SERVICE_SERVER_Construct_INTERCEPT_CLASS;
            }

            @Override
            public boolean isOverrideArgs() {
                return false;
            }
        }};
    }
}
