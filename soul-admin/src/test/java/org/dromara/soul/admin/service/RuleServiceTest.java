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

package org.dromara.soul.admin.service;

import org.apache.commons.lang3.StringUtils;
import org.dromara.soul.admin.dto.RuleConditionDTO;
import org.dromara.soul.admin.dto.RuleDTO;
import org.dromara.soul.admin.entity.PluginDO;
import org.dromara.soul.admin.entity.RuleConditionDO;
import org.dromara.soul.admin.entity.RuleDO;
import org.dromara.soul.admin.entity.SelectorDO;
import org.dromara.soul.admin.mapper.PluginMapper;
import org.dromara.soul.admin.mapper.RuleConditionMapper;
import org.dromara.soul.admin.mapper.RuleMapper;
import org.dromara.soul.admin.mapper.SelectorMapper;
import org.dromara.soul.admin.page.CommonPager;
import org.dromara.soul.admin.page.PageParameter;
import org.dromara.soul.admin.query.RuleConditionQuery;
import org.dromara.soul.admin.query.RuleQuery;
import org.dromara.soul.admin.service.impl.RuleServiceImpl;
import org.dromara.soul.admin.vo.RuleVO;
import org.dromara.soul.common.dto.RuleData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Test cases for RuleService.
 *
 * @author kennhua
 * @author nuo-promise
 */
@RunWith(MockitoJUnitRunner.class)
public final class RuleServiceTest {

    @InjectMocks
    private RuleServiceImpl ruleService;

    @Mock
    private RuleMapper ruleMapper;

    @Mock
    private RuleConditionMapper ruleConditionMapper;

    @Mock
    private SelectorMapper selectorMapper;

    @Mock
    private PluginMapper pluginMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Before
    public void setUp() {
        ruleService = new RuleServiceImpl(ruleMapper, ruleConditionMapper, selectorMapper, pluginMapper, eventPublisher);
    }

    @Test
    public void testRegister() {
        publishEvent();
        testRegisterCreate();
        testRegisterUpdate();
    }

    @Test
    public void testCreateOrUpdate() {
        publishEvent();
        testCreate();
        testUpdate();
    }

    @Test
    public void testDelete() {
        publishEvent();
        RuleDO ruleDO = buildRuleDO("123");
        given(this.ruleMapper.selectById("123")).willReturn(ruleDO);
        final List<String> ids = Collections.singletonList(ruleDO.getId());
        assertEquals(this.ruleService.delete(ids), ids.size());
    }

    @Test
    public void testFindById() {
        RuleDO ruleDO = buildRuleDO("123");
        given(this.ruleMapper.selectById("123")).willReturn(ruleDO);
        RuleConditionQuery ruleConditionQuery = buildRuleConditionQuery();
        RuleConditionDO ruleCondition = buildRuleConditionDO();
        given(this.ruleConditionMapper.selectByQuery(ruleConditionQuery)).willReturn(Collections.singletonList(ruleCondition));
        RuleVO ruleVO = buildRuleVO("123");
        final RuleVO ruleVOById = this.ruleService.findById("123");
        assertNotNull(ruleVOById);
        assertEquals(ruleVOById.getId(), ruleVO.getId());
    }

    @Test
    public void testListByPage() {
        PageParameter parameter = new PageParameter();
        parameter.setPageSize(5);
        parameter.setTotalCount(10);
        parameter.setTotalPage(parameter.getTotalCount() / parameter.getPageSize());
        RuleQuery ruleQuery = new RuleQuery("456", parameter);
        given(this.ruleMapper.countByQuery(ruleQuery)).willReturn(10);
        List<RuleDO> ruleDOList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            RuleDO ruleDO = buildRuleDO(String.valueOf(i));
            ruleDOList.add(ruleDO);
        }
        given(this.ruleMapper.selectByQuery(ruleQuery)).willReturn(ruleDOList);
        final CommonPager<RuleVO> ruleVOCommonPager = this.ruleService.listByPage(ruleQuery);
        assertEquals(ruleVOCommonPager.getDataList().size(), ruleDOList.size());
    }

    @Test
    public void testListAll() {
        publishEvent();
        RuleConditionQuery ruleConditionQuery = buildRuleConditionQuery();
        RuleConditionDO ruleCondition = buildRuleConditionDO();
        given(this.ruleConditionMapper.selectByQuery(ruleConditionQuery)).willReturn(Collections.singletonList(ruleCondition));
        RuleDO ruleDO = buildRuleDO("123");
        List<RuleDO> ruleDOList = Collections.singletonList(ruleDO);
        given(this.ruleMapper.selectAll()).willReturn(ruleDOList);
        List<RuleData> dataList = this.ruleService.listAll();
        assertNotNull(dataList);
        assertEquals(ruleDOList.size(), dataList.size());
    }

    @Test
    public void testFindBySelectorId() {
        publishEvent();
        RuleConditionQuery ruleConditionQuery = buildRuleConditionQuery();
        RuleConditionDO ruleCondition = buildRuleConditionDO();
        given(this.ruleConditionMapper.selectByQuery(ruleConditionQuery)).willReturn(Collections.singletonList(ruleCondition));
        RuleDO ruleDO = buildRuleDO("123");
        List<RuleDO> ruleDOList = Collections.singletonList(ruleDO);
        given(this.ruleMapper.findBySelectorId("456")).willReturn(ruleDOList);
        List<RuleData> dataList = this.ruleService.findBySelectorId("456");
        assertNotNull(dataList);
        assertEquals(ruleDOList.size(), dataList.size());
    }

    private void publishEvent() {
        PluginDO pluginDO = buildPluginDO();
        SelectorDO selectorDO = buildSelectorDO();
        given(this.selectorMapper.selectById("456")).willReturn(selectorDO);
        given(this.pluginMapper.selectById("789")).willReturn(pluginDO);
    }

    private void testRegisterCreate() {
        RuleDTO ruleDTO = buildRuleDTO("");
        RuleDO ruleDO = RuleDO.buildRuleDO(ruleDTO);
        String ruleId = this.ruleService.register(ruleDTO);
        assertNotNull(ruleId);
        assertEquals(ruleId.length(), ruleDO.getId().length());
    }

    private void testRegisterUpdate() {
        RuleDTO ruleDTO = buildRuleDTO("123");
        String ruleId = this.ruleService.register(ruleDTO);
        assertNotNull(ruleId);
        assertEquals(ruleId, ruleDTO.getId());
    }

    private void testCreate() {
        RuleDTO ruleDTO = buildRuleDTO("");
        given(this.ruleMapper.insertSelective(any())).willReturn(1);
        assertThat(this.ruleService.createOrUpdate(ruleDTO), greaterThan(0));
    }

    private void testUpdate() {
        RuleDTO ruleDTO = buildRuleDTO("123");
        given(this.ruleMapper.updateSelective(any())).willReturn(1);
        assertThat(this.ruleService.createOrUpdate(ruleDTO), greaterThan(0));
    }

    private RuleDO buildRuleDO(final String id) {
        RuleDTO ruleDTO = RuleDTO.builder()
                .selectorId("456")
                .matchMode(0)
                .build();
        if (StringUtils.isNotBlank(id)) {
            ruleDTO.setId(id);
        }
        RuleConditionDTO ruleConditionDTO1 = RuleConditionDTO.builder().id("111").build();
        RuleConditionDTO ruleConditionDTO2 = RuleConditionDTO.builder().id("222").build();
        ruleDTO.setRuleConditions(Arrays.asList(ruleConditionDTO1, ruleConditionDTO2));
        RuleDO ruleDO = RuleDO.buildRuleDO(ruleDTO);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        ruleDO.setDateCreated(now);
        ruleDO.setDateUpdated(now);
        return ruleDO;
    }

    private RuleDTO buildRuleDTO(final String id) {
        RuleDTO ruleDTO = RuleDTO.builder()
                .selectorId("456")
                .matchMode(0)
                .build();
        if (StringUtils.isNotBlank(id)) {
            ruleDTO.setId(id);
        }
        RuleConditionDTO ruleConditionDTO1 = RuleConditionDTO.builder().id("111").build();
        RuleConditionDTO ruleConditionDTO2 = RuleConditionDTO.builder().id("222").build();
        ruleDTO.setRuleConditions(Arrays.asList(ruleConditionDTO1, ruleConditionDTO2));
        return ruleDTO;
    }

    private RuleVO buildRuleVO(final String id) {
        RuleVO ruleVO = new RuleVO();
        ruleVO.setId(id);
        return ruleVO;
    }

    private PluginDO buildPluginDO() {
        return PluginDO.builder()
                .name("test")
                .id("789")
                .build();
    }

    private SelectorDO buildSelectorDO() {
        return SelectorDO.builder()
                .pluginId("789")
                .id("456")
                .build();
    }

    private RuleConditionDO buildRuleConditionDO() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        return RuleConditionDO.builder()
                .ruleId("123")
                .paramType("post")
                .operator("match")
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    private RuleConditionQuery buildRuleConditionQuery() {
        RuleConditionQuery ruleConditionQuery = new RuleConditionQuery();
        ruleConditionQuery.setRuleId("123");
        return ruleConditionQuery;
    }
}
