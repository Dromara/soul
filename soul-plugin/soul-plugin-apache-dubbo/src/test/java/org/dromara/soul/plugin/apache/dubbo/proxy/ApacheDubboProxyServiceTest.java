/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.plugin.apache.dubbo.proxy;

import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.dromara.soul.common.dto.MetaData;
import org.dromara.soul.common.enums.RpcTypeEnum;
import org.dromara.soul.plugin.apache.dubbo.cache.ApplicationConfigCache;
import org.dromara.soul.plugin.api.dubbo.DubboParamResolveService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The Test Case For ApacheDubboProxyService.
 *
 * @author nuo-promise
 **/
public final class ApacheDubboProxyServiceTest {

    private static final String PATH = "/dubbo/findAll";

    private static final String[] LEFT = new String[]{};

    private static final String METHOD_NAME = "findAll";

    private static final Object[] RIGHT = new Object[]{};

    private MetaData metaData;

    private ServerWebExchange exchange;

    @Before
    public void setUp() {
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("localhost").build());
        metaData = new MetaData();
        metaData.setId("1332017966661636096");
        metaData.setAppName("dubbo");
        metaData.setPath(PATH);
        metaData.setServiceName("org.dromara.soul.test.dubbo.api.service.DubboTestService");
        metaData.setMethodName(METHOD_NAME);
        metaData.setRpcType(RpcTypeEnum.DUBBO.getName());
    }

    @Test(expected = NullPointerException.class)
    public void genericInvoker() throws NoSuchFieldException, IllegalAccessException {
        ReferenceConfig referenceConfig = mock(ReferenceConfig.class);
        GenericService genericService = mock(GenericService.class);
        when(referenceConfig.get()).thenReturn(genericService);
        when(referenceConfig.getInterface()).thenReturn(PATH);
        when(genericService.$invoke(PATH, LEFT, RIGHT)).thenReturn(null);
        ApplicationConfigCache applicationConfigCache = ApplicationConfigCache.getInstance();
        Field field = ApplicationConfigCache.class.getDeclaredField("cache");
        field.setAccessible(true);
        ((LoadingCache) field.get(applicationConfigCache)).put(PATH, referenceConfig);
        ApacheDubboProxyService apacheDubboProxyService = new ApacheDubboProxyService(new DubboParamResolveServiceImpl());
        apacheDubboProxyService.genericInvoker("", metaData, exchange);
        System.out.println("response" + RpcContext.getContext().getResponse());
    }

    class DubboParamResolveServiceImpl implements DubboParamResolveService {

        @Override
        public Pair<String[], Object[]> buildParameter(final String body, final String parameterTypes) {
            return new ImmutablePair<>(LEFT, RIGHT);
        }
    }
}
