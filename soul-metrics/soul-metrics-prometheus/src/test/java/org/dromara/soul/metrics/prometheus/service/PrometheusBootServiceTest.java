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

package org.dromara.soul.metrics.prometheus.service;

import io.prometheus.client.CollectorRegistry;
import java.util.concurrent.atomic.AtomicBoolean;
import org.dromara.soul.common.utils.GsonUtils;
import org.dromara.soul.common.utils.ReflectUtils;
import org.dromara.soul.metrics.config.MetricsConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test cases for PrometheusMetricsTrackerManager.
 *
 * @author dengliming
 */
public final class PrometheusBootServiceTest {

    private PrometheusBootService prometheusMetricsTrackerManager;

    @Before
    public void init() {
        prometheusMetricsTrackerManager = new PrometheusBootService();
        CollectorRegistry.defaultRegistry.clear();
    }

    @Test
    public void startWithNormal() {
        MetricsConfig metricsConfig = new MetricsConfig("test", "", 10109, false, 1, "{}", null);
        prometheusMetricsTrackerManager.start(metricsConfig);
        assertTrue(prometheusMetricsTrackerManager.getRegistered().get());
        prometheusMetricsTrackerManager.stop();
    }

    @Test
    public void testRegistered() {
        AtomicBoolean registered = (AtomicBoolean) ReflectUtils.getFieldValue(prometheusMetricsTrackerManager, "registered");
        registered.set(true);
        String jmxConfig = GsonUtils.getInstance().toJson("whitelistObjectNames:org.apache.cassandra.metrics:type=ColumnFamily");
        MetricsConfig metricsConfig = new MetricsConfig("test", "", 10119, false, 1, jmxConfig, null);
        prometheusMetricsTrackerManager.start(metricsConfig);
        assertTrue(prometheusMetricsTrackerManager.getRegistered().get());
    }
}
