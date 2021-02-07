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

import org.dromara.soul.admin.dto.RoleDTO;
import org.dromara.soul.admin.entity.PermissionDO;
import org.dromara.soul.admin.entity.RoleDO;
import org.dromara.soul.admin.mapper.PermissionMapper;
import org.dromara.soul.admin.mapper.ResourceMapper;
import org.dromara.soul.admin.mapper.RoleMapper;
import org.dromara.soul.admin.page.CommonPager;
import org.dromara.soul.admin.page.PageParameter;
import org.dromara.soul.admin.query.RoleQuery;
import org.dromara.soul.admin.service.impl.RoleServiceImpl;
import org.dromara.soul.admin.vo.RoleEditVO;
import org.dromara.soul.admin.vo.RoleVO;
import org.dromara.soul.common.utils.UUIDUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Test cases for RoleService.
 *
 * @author wincher
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private ResourceMapper resourceMapper;

    @Test
    public void testCreateOrUpdate() {
        //test save
        given(this.roleMapper.insertSelective(any())).willReturn(1);
        RoleDTO roleDTO = buildRoleDTOWithoutId();
        int count = roleService.createOrUpdate(roleDTO);
        assertThat(count, equalTo(1));
        verify(roleMapper, times(1)).insertSelective(any());

        //test update with no existing permissions and no passing permissions
        reset(permissionMapper, roleMapper);
        given(this.roleMapper.updateSelective(any())).willReturn(1);
        roleDTO = buildRoleDTO();
        count = roleService.createOrUpdate(roleDTO);
        assertThat(count, equalTo(1));
        verify(permissionMapper, times(1)).findByObjectId(anyString());
        verify(permissionMapper, times(0)).deleteByObjectIdAndResourceId(any());
        verify(permissionMapper, times(0)).insertSelective(any());

        //test update with delete permissions
        reset(permissionMapper, roleMapper);
        given(this.roleMapper.updateSelective(any())).willReturn(1);
        given(this.permissionMapper.findByObjectId(anyString())).willReturn(Arrays.asList(PermissionDO.builder().resourceId("1").build()));
        count = roleService.createOrUpdate(roleDTO);
        verify(permissionMapper, times(1)).findByObjectId(anyString());
        verify(permissionMapper, atLeastOnce()).deleteByObjectIdAndResourceId(any());
        verify(permissionMapper, times(0)).insertSelective(any());

        //test update with insert permissions
        reset(permissionMapper, roleMapper);
        given(this.roleMapper.updateSelective(any())).willReturn(1);
        roleDTO.setCurrentPermissionIds(Arrays.asList("1", "2"));
        count = roleService.createOrUpdate(roleDTO);
        assertThat(count, equalTo(1));
        verify(permissionMapper, times(1)).findByObjectId(anyString());
        verify(permissionMapper, times(0)).deleteByObjectIdAndResourceId(any());
        verify(permissionMapper, atLeastOnce()).insertSelective(any());

        //test update with exist difference between existing and current permissions
        reset(permissionMapper, roleMapper);
        given(this.roleMapper.updateSelective(any())).willReturn(1);
        given(this.permissionMapper.findByObjectId(anyString())).willReturn(Arrays.asList(PermissionDO.builder().resourceId("3").build()));
        count = roleService.createOrUpdate(roleDTO);
        assertThat(count, equalTo(1));
        verify(permissionMapper, times(1)).findByObjectId(anyString());
        verify(permissionMapper, atLeastOnce()).deleteByObjectIdAndResourceId(any());
        verify(permissionMapper, atLeastOnce()).insertSelective(any());
    }

    @Test
    public void testDelete() {
        List<String> ids = Arrays.asList("1", "2");
        roleService.delete(ids);
        verify(permissionMapper, times(1)).deleteByObjectIds(ids);
        verify(roleMapper, times(1)).delete(ids);
    }

    @Test
    public void testFindById() {
        RoleDO roleDO = buildRoleDO();
        given(roleMapper.selectById(roleDO.getId())).willReturn(roleDO);
        RoleEditVO result = roleService.findById(roleDO.getId());
        assertEquals(RoleVO.buildRoleVO(roleDO), result.getSysRole());
    }

    @Test
    public void testFindByQuery() {
        RoleDO roleDO = buildRoleDO();
        given(roleMapper.findByRoleName(roleDO.getRoleName())).willReturn(roleDO);
        RoleVO result = roleService.findByQuery(roleDO.getRoleName());
        assertEquals(RoleVO.buildRoleVO(roleDO), result);
    }

    @Test
    public void testListByPage() {
        RoleDO roleDO = buildRoleDO();
        PageParameter pageParameter = new PageParameter(0, 5, 10);
        RoleQuery query = new RoleQuery(roleDO.getRoleName(), pageParameter);
        List<RoleDO> roleDOs = Collections.singletonList(roleDO);
        given(roleMapper.selectByQuery(query)).willReturn(roleDOs);
        given(roleMapper.countByQuery(query)).willReturn(roleDOs.size());
        CommonPager<RoleVO> roleVOCommonPager = roleService.listByPage(query);
        assertEquals(roleDOs.stream().map(RoleVO::buildRoleVO).collect(Collectors.toList()), roleVOCommonPager.getDataList());
    }

    @Test
    public void testSelectAll() {
        List<RoleDO> roleDOs = Collections.singletonList(buildRoleDO());
        given(roleMapper.selectAll()).willReturn(roleDOs);
        List<RoleVO> roleVOS = roleService.selectAll();
        assertEquals(roleDOs.stream().map(RoleVO::buildRoleVO).collect(Collectors.toList()), roleVOS);

    }

    private RoleDTO buildRoleDTO() {
        return RoleDTO.builder()
            .id(UUIDUtils.getInstance().generateShortUuid())
            .roleName("test-role")
            .description("role desc")
            .build();
    }

    private RoleDTO buildRoleDTOWithoutId() {
        return RoleDTO.builder()
            .roleName("test-role")
            .description("role desc")
            .build();
    }

    private RoleDO buildRoleDO() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        return RoleDO.builder()
            .id(UUIDUtils.getInstance().generateShortUuid())
            .roleName("test-role")
            .description("role desc")
            .dateCreated(now)
            .dateUpdated(now)
            .build();
    }

}
