package org.apache.skywalking.apm.plugin.thrift.define.tprotocol;

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
 * target.addGetter(ThriftConstants.FIELD_GETTER_T_PROTOCOL, "concreteProtocol");
 */
public class TProtocolDecoratorInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String SERVICE_TPROTOCOL_ENHANCE_CLASS = "org.apache.thrift.protocol.TProtocolDecorator";

   private static final String SERVICE_TPROTOCOL_Construct_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.tprotocol.TProtocolDecoratorInterceptor";


    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_TPROTOCOL_ENHANCE_CLASS);
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
                        return SERVICE_TPROTOCOL_Construct_INTERCEPT_CLASS;
                    }
                }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[0];
    }
}

