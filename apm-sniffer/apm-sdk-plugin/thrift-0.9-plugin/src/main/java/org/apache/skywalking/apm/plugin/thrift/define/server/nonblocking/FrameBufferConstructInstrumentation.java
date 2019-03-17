package org.apache.skywalking.apm.plugin.thrift.define.server.nonblocking;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Created by pengfeining on 2019/3/13 0013.
 *
 *
 target.addField(ThriftConstants.FIELD_ACCESSOR_SOCKET);
 target.addGetter(ThriftConstants.FIELD_GETTER_T_NON_BLOCKING_TRANSPORT, ThriftConstants.FRAME_BUFFER_FIELD_TRANS_);

 */
public class FrameBufferConstructInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String SERVICE_SERVER_ENHANCE_CLASS = "org.apache.thrift.server.AbstractNonblockingServer$FrameBuffer";

    // [THRIFT-1972] - 0.9.1 added a field for the wrapper around trans_ field, while getting rid of getInputTransport() method
    // AbstractNonblockingServer$FrameBuffer(TNonblockingTransport, SelectionKey, AbstractSelectThread)
    // inner class - implicit reference to outer class instance
    private static final String SERVICE_SERVER_Construct_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.server.nonblocking.FrameBufferConstructInterceptor";


    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_SERVER_ENHANCE_CLASS);
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return any();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return SERVICE_SERVER_Construct_INTERCEPT_CLASS;
                    }
                }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[0];
    }
}
