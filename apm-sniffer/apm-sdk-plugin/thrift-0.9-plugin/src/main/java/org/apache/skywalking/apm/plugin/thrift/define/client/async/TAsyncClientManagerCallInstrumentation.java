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

import static net.bytebuddy.matcher.ElementMatchers.named;


/**
 * @author HyunGil Jeong
 */
public class TAsyncClientManagerCallInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {
    
    private static final String SERVICE_ASYN_CLIENT_ENHANCE_CLASS = "org.apache.thrift.async.TAsyncClientManager";
    
    private static final String SERVICE_ASYN_CLIENT_MANAGER_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.thrift.client.async.TAsyncClientManagerCallInterceptor";


    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(SERVICE_ASYN_CLIENT_ENHANCE_CLASS);
    }
    
    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }
    
    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("call");
                }
                
                @Override
                public String getMethodsInterceptor() {
                    return SERVICE_ASYN_CLIENT_MANAGER_INTERCEPT_CLASS;
                }
                
                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
