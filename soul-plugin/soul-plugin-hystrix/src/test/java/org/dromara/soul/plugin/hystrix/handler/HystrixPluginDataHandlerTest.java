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

package org.dromara.soul.plugin.hystrix.handler;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties.Setter;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesFactory;
import org.dromara.soul.common.dto.RuleData;
import org.dromara.soul.common.enums.PluginEnum;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * The Test Case For HystrixPluginDataHandler.
 *
 * @author nuo-promise
 **/
public final class HystrixPluginDataHandlerTest {

    private HystrixPluginDataHandler hystrixPluginDataHandler;

    @Before
    public void setUp() {
        hystrixPluginDataHandler = new HystrixPluginDataHandler();
    }

    @Test
    public void testHandlerRUle() {
        hystrixPluginDataHandler.handlerRule(mock(RuleData.class));
        assertNotNull(HystrixPropertiesFactory.getCommandProperties(mock(HystrixCommandKey.class), mock(Setter.class)));
    }

    @Test
    public void testPluginNamed() {
        assertEquals(hystrixPluginDataHandler.pluginNamed(), PluginEnum.HYSTRIX.getName());
    }
}
