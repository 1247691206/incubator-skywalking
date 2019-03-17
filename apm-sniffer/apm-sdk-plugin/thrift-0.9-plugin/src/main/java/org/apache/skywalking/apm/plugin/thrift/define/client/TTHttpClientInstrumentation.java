package org.apache.skywalking.apm.plugin.thrift.define.client;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Created by pengfeining on 2019/3/12 0012.
 *
 target.addGetter(ThriftConstants.FIELD_GETTER_URL, ThriftConstants.T_HTTP_CLIENT_FIELD_URL_);
 */
public class TTHttpClientInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String SERVICE_CLIENT_ENHANCE_CLASS = "org.apache.thrift.transport.THttpClient";
    private static final String SERVICE_CLIENT_any_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.THttpClientInterceptor";



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
                        return SERVICE_CLIENT_any_INTERCEPT_CLASS;
                    }
                }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[0];
    }

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_CLIENT_ENHANCE_CLASS);
    }
}