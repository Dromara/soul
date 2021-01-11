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

package org.dromara.soul.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.soul.admin.entity.ResourceDO;
import org.dromara.soul.admin.query.ResourceQuery;

import java.util.List;

/**
 * this is resource mapper.
 *
 * @author nuo-promise
 **/
@Mapper
public interface ResourceMapper {

    /**
     * select resource by id.
     *
     * @param id primary key
     * @return {@linkplain ResourceDO}
     */
    ResourceDO selectById(String id);

    /**
     * select resource by query.
     *
     * @param resourceQuery {@linkplain ResourceQuery}
     * @return {@linkplain List}
     */
    List<ResourceDO> selectByQuery(ResourceQuery resourceQuery);

    /**
     * select resource by query.
     *
     * @param resourceQuery {@linkplain ResourceQuery}
     * @return {@linkplain List}
     */
    List<ResourceDO> findByQuery(ResourceQuery resourceQuery);

    /**
     * count resource by query.
     *
     * @param resourceQuery {@linkplain ResourceQuery}
     * @return {@linkplain Integer}
     */
    Integer countByQuery(ResourceQuery resourceQuery);

    /**
     * insert resource.
     *
     * @param resourceDO {@linkplain ResourceDO}
     * @return rows int
     */
    int insert(ResourceDO resourceDO);

    /**
     * insert selective resource.
     *
     * @param resourceDO {@linkplain ResourceDO}
     * @return rows int
     */
    int insertSelective(ResourceDO resourceDO);

    /**
     * update resource.
     *
     * @param resourceDO {@linkplain ResourceDO}
     * @return rows int
     */
    int update(ResourceDO resourceDO);

    /**
     * update selective resource.
     *
     * @param resourceDO {@linkplain ResourceDO}
     * @return rows int
     */
    int updateSelective(ResourceDO resourceDO);

    /**
     * delete resource.
     *
     * @param id primary key
     * @return rows int
     */
    int delete(String id);

    /**
     * list All.
     *
     * @return {@linkplain List}
     */
    List<ResourceDO> selectAll();
}
