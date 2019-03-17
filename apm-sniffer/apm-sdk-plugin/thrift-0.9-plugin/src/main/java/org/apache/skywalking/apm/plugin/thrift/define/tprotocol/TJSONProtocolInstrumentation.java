package org.apache.skywalking.apm.plugin.thrift.define.tprotocol;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Created by pengfeining on 2019/3/13 0013.
 */
public class TJSONProtocolInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String SERVICE_TPROTOCOL_ENHANCE_CLASS = "org.apache.thrift.protocol.TJSONProtocol";
    // TProtocol.writeFieldStop()
    private static final String SERVICE_TPROTOCOL_writeFieldStop_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.tprotocol.client.TProtocolWriteFieldStopInterceptor";
    // TProtocol.readFieldBegin()
    private static final String SERVICE_TPROTOCOL_readFieldBegin_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.tprotocol.server.TProtocolReadFieldBeginInterceptor";

    //TProtocol.readBool, TProtocol.readBinary, TProtocol.readI16, TProtocol.readI64 注意匹配问题
    private static final String SERVICE_TPROTOCOL_ReadTType_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.tprotocol.server.TProtocolReadTTypeInterceptor";

    // TProtocol.readMessageEnd()
    private static final String SERVICE_TPROTOCOL_readMessageEnd_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.tprotocol.server.TProtocolReadMessageEndInterceptor";
    // TProtocol.readMessageBegin()
    private static final String SERVICE_TPROTOCOL_readMessageBegin_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.tprotocol.server.TProtocolReadMessageBeginInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_TPROTOCOL_ENHANCE_CLASS);
    }

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
                        return named("writeFieldStop");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_TPROTOCOL_writeFieldStop_INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("readFieldBegin");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_TPROTOCOL_readFieldBegin_INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return nameStartsWith("read");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_TPROTOCOL_ReadTType_INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("readMessageEnd");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_TPROTOCOL_readMessageEnd_INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("readMessageBegin");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_TPROTOCOL_readMessageBegin_INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }
}

