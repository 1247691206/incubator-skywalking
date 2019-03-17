/*
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skywalking.apm.plugin.thrift.define.client.async;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.named;


/**
 * @author HyunGil Jeong
 *
 *  pinpoint 在此处添加了字段  skyWalking也要注意添加字段
target.addField(AsyncContextAccessor.class.getName());
target.addField(ThriftConstants.FIELD_ACCESSOR_SOCKET_ADDRESS);
target.addGetter(ThriftConstants.FIELD_GETTER_T_NON_BLOCKING_TRANSPORT, ThriftConstants.T_ASYNC_METHOD_CALL_FIELD_TRANSPORT);

 */
public class TAsyncMethodCallInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {
    
    private static final String SERVICE_ASYN_CLIENT_ENHANCE_CLASS = "org.apache.thrift.async.TAsyncMethodCall";
    private static final String SERVICE_ASYN_CLIENT_Construct_ENHANCE_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.async.TAsyncMethodCallConstructInterceptor";

    private static final String SERVICE_ASYN_CLIENT_CleanUpAndFireCallback_ENHANCE_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.async.TAsyncMethodCallCleanUpAndFireCallbackInterceptor";
    private static final String SERVICE_ASYN_CLIENT_CallOnError_ENHANCE_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.async.TAsyncMethodCallOnErrorInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_ASYN_CLIENT_ENHANCE_CLASS);
    }


    //构造函数监听
    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        //构造函数只要一个 所以any
                        return any();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return SERVICE_ASYN_CLIENT_Construct_ENHANCE_CLASS;
                    }
                }
        };
    }
    
    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("cleanUpAndFireCallback");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_ASYN_CLIENT_CleanUpAndFireCallback_ENHANCE_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("onError");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return SERVICE_ASYN_CLIENT_CallOnError_ENHANCE_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }
}
