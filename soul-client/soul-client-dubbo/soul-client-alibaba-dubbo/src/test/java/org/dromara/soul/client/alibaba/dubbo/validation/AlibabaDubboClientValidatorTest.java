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

package org.dromara.soul.client.alibaba.dubbo.validation;

import com.alibaba.dubbo.common.URL;
import org.dromara.soul.client.alibaba.dubbo.validation.mock.MockValidationParameter;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlibabaDubboClientValidatorTest.
 *
 * @author KevinClair
 */
public class AlibabaDubboClientValidatorTest {
    
    private static final String MOCK_SERVICE_URL = "mock://test:28000/org.dromara.soul.client.alibaba.dubbo.validation.mock.MockValidatorTarget";
    
    @Test
    public void testItWithNonExistMethod() {
        try {
            final URL url = URL.valueOf(MOCK_SERVICE_URL);
            new AlibabaDubboClientValidation().getValidator(url)
                    .validate("nonExistingMethod", new Class<?>[]{String.class}, new Object[]{"arg1"});
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NoSuchMethodException);
        }
    }
    
    @Test
    public void testItWithExistMethod() throws Exception {
        final URL url = URL.valueOf(MOCK_SERVICE_URL + "?soulValidation=org.hibernate.validator.HibernateValidator");
        new AlibabaDubboClientValidation().getValidator(url)
                .validate("method1", new Class<?>[]{String.class}, new Object[]{"anything"});
    }
    
    @Test
    public void testValidateWhenMeetsConstraintThenValidationFailed() {
        try {
            final URL url = URL.valueOf(MOCK_SERVICE_URL);
            new AlibabaDubboClientValidation().getValidator(url)
                    .validate("method2", new Class<?>[]{MockValidationParameter.class}, new Object[]{new MockValidationParameter("NotBeNull")});
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ValidationException);
        }
    }
    
    @Test
    public void testItWithArrayArg() throws Exception {
        final URL url = URL.valueOf(MOCK_SERVICE_URL);
        new AlibabaDubboClientValidation().getValidator(url)
                .validate("method3", new Class<?>[]{MockValidationParameter[].class}, new Object[]{new MockValidationParameter[]{new MockValidationParameter("parameter")}});
    }
    
    @Test
    public void testItWithCollectionArg() throws Exception {
        URL url = URL.valueOf(MOCK_SERVICE_URL);
        new AlibabaDubboClientValidation().getValidator(url)
                .validate("method4", new Class<?>[]{List.class}, new Object[]{Collections.singletonList("parameter")});
    }
    
    @Test
    public void testItWithMapArg() throws Exception {
        final URL url = URL.valueOf(MOCK_SERVICE_URL);
        final Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        new AlibabaDubboClientValidation().getValidator(url).validate("method5", new Class<?>[]{Map.class}, new Object[]{map});
    }
    
}
