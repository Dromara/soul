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

package org.apache.shenyu.plugin.modify.response.handler;

import org.apache.shenyu.common.dto.RuleData;
import org.apache.shenyu.common.dto.convert.rule.impl.ModifyResponseRuleHandle;
import org.apache.shenyu.common.enums.PluginEnum;
import org.apache.shenyu.common.utils.GsonUtils;
import org.apache.shenyu.plugin.modify.response.cache.ModifyResponseRuleHandleCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The Test Case For ModifyResponsePluginDataHandler.
 */
public final class ModifyResponsePluginDataHandlerTest {

    private RuleData ruleData;

    private ModifyResponsePluginDataHandler modifyResponsePluginDataHandler;

    @Before
    public void setUp() {
        this.modifyResponsePluginDataHandler = new ModifyResponsePluginDataHandler();
        this.ruleData = mock(RuleData.class);
        when(ruleData.getSelectorId()).thenReturn("1");
        when(ruleData.getName()).thenReturn("modify");
        ModifyResponseRuleHandle modifyResponseRuleHandle = new ModifyResponseRuleHandle();
        modifyResponseRuleHandle.setStatusCode(400);
        when(ruleData.getHandle()).thenReturn(GsonUtils.getGson().toJson(modifyResponseRuleHandle));
    }

    /**
     * Handler selector test.
     */
    @Test
    public void handlerSelectorTest() {
        modifyResponsePluginDataHandler.handlerRule(ruleData);
        ModifyResponseRuleHandle modifyResponseRuleHandle = ModifyResponseRuleHandleCache.getInstance().obtainHandle(ModifyResponsePluginDataHandler.getResourceName(ruleData));
        Assert.assertEquals(400, modifyResponseRuleHandle.getStatusCode());
    }

    /**
     * Remove selector test.
     */
    @Test
    public void removeSelectorTest() {
        modifyResponsePluginDataHandler.removeRule(ruleData);
        ModifyResponseRuleHandle modifyResponseRuleHandle = ModifyResponseRuleHandleCache.getInstance().obtainHandle(ModifyResponsePluginDataHandler.getResourceName(ruleData));
        Assert.assertNull(modifyResponseRuleHandle);
    }

    /**
     * Plugin named test.
     */
    @Test
    public void pluginNamedTest() {
        Assert.assertEquals(modifyResponsePluginDataHandler.pluginNamed(), PluginEnum.MODIFY_RESPONSE.getName());
    }
}
