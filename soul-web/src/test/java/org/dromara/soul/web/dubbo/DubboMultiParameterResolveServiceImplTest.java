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

package org.dromara.soul.web.dubbo;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Test cases for WebSocketParamFilter.
 *
 * @author candyYu
 */
@RunWith(MockitoJUnitRunner.class)
public final class DubboMultiParameterResolveServiceImplTest {

    @InjectMocks
    private DubboMultiParameterResolveServiceImpl impl;

    @Test
    public void testBuildParameter() {
        String body = "{\"id\": \"12345\",\"name\": \"candyYu\"}";
        String parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.Student";
        Pair<String[], Object[]> pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));

        body = "{\"testArray\":[{\"id\":\"123\",\"name\":\"candy\"},{\"id\":\"456\",\"name\":\"myth\"}]}";
        parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.Student[]";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));

        body = "{\"ids\":[\"123\",\"456\"],\"name\":\"hello world\"}\n";
        parameterTypes = "java.lang.Integer[],java.lang.String";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(2));
        assertThat(pair.getRight().length, is(2));

        body = "{\"idMaps\":{\"id2\":\"2\",\"id1\":\"1\"},\"name\":\"hello world\"}\n";
        parameterTypes = "java.util.HashMap,java.lang.String";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(2));
        assertThat(pair.getRight().length, is(2));

        body = "{\"dubboTest\":{\"id\":\"123\",\"name\":\"xiaoyu\"},\"idLists\":[\"456\",\"789\"],\"idMaps\":{\"id2\":\"2\",\"id1\":\"1\"}}";
        parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.ComplexBean";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));

        body = "{\"complexBeanTest\":{\"dubboTest\":{\"id\":\"123\",\"name\":\"xiaoyu\"},\"idLists\":[\"456\",\"789\"],\"idMaps\":{\"id2\":\"2\",\"id1\":\"1\"}},\"name\":\"xiaoyu\"}";
        parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.ComplexBean, java.lang.String";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(2));
        assertThat(pair.getRight().length, is(2));

        body = "{\"ids\":[\"123\",\"456\"],\"id\":123,\"name\":\"hello world\",\"testArray\":[{\"id\":\"123\",\"name\":\"candy\"},{\"id\":\"456\",\"name\":\"myth\"}]}\n";
        parameterTypes = "java.lang.Integer[],java.lang.Integer,java.lang.String,org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.Student[]";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(4));
        assertThat(pair.getRight().length, is(4));

    }

    @Test
    public void testBuildParameterWithNull() {
        String body = "{\"student\":{\"id\":null,\"name\":null}}";
        String parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.Student";
        Pair<String[], Object[]> pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));
        Map map = (Map) pair.getRight()[0];
        map = (Map) map.get("student");
        assertNull(map.get("id"));
        assertNull(map.get("name"));

        body = "{\"students\":[{\"id\":null,\"name\":null}]}";
        parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.Student[]";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));
        map = (Map) pair.getRight()[0];
        List list = (List) map.get("students");
        map = (Map) list.get(0);
        assertNull(map.get("id"));
        assertNull(map.get("name"));

        body = "{\"dubboTest\":{\"id\":null,\"name\":null},\"idLists\":[null,null],\"idMaps\":{\"id2\":null,\"id1\":null}}";
        parameterTypes = "org.dromara.soul.web.dubbo.DubboMultiParameterResolveServiceImplTest.ComplexBean";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));
        map = (Map) pair.getRight()[0];
        Map dubboTest = (Map) map.get("dubboTest");
        assertNull(dubboTest.get("id"));
        assertNull(dubboTest.get("name"));
        list = (List) map.get("idLists");
        assertNull(list.get(0));
        assertNull(list.get(1));

        body = "{\"name\":null}";
        parameterTypes = "java.lang.String";
        pair = impl.buildParameter(body, parameterTypes);
        assertThat(pair.getLeft().length, is(1));
        assertThat(pair.getRight().length, is(1));
        assertNull(pair.getRight()[0]);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private final class Student {
        private String id;

        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private final class ComplexBean {
        private Student dubboTest;

        private List<String> idLists;

        private Map<String, String> idMaps;
    }
}
