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

package org.apache.shenyu.admin.listener.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.PutResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The testCase for {@link EtcdClient}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EtcdClientTest {

    private static final String TEST_KEY = "KEY";

    private static final String TEST_VALUE = "VALUE";

    @Mock
    private Client client;

    private EtcdClient etcdClient;

    @Before
    public void setUp() {
        KV kvClient = mock(KV.class);
        when(client.getKVClient()).thenReturn(kvClient);
        etcdClient = new EtcdClient(client);
        Assert.assertNotNull(etcdClient);
    }

    @Test
    public void close() {
        etcdClient.close();
    }

    @Test
    public void put() {
        CompletableFuture<PutResponse> put = mock(CompletableFuture.class);
        when(client.getKVClient().put(ByteSequence.from(TEST_KEY, StandardCharsets.UTF_8), ByteSequence.from(TEST_VALUE, StandardCharsets.UTF_8))).thenReturn(put);
        etcdClient.put(TEST_KEY, TEST_VALUE);
    }

    @Test
    public void delete() {
        CompletableFuture<DeleteResponse> delete = mock(CompletableFuture.class);
        when(client.getKVClient().delete(ByteSequence.from(TEST_KEY, StandardCharsets.UTF_8))).thenReturn(delete);
        etcdClient.delete(TEST_KEY);
    }

    @Test
    public void deleteEtcdPathRecursive() {
        etcdClient.deleteEtcdPathRecursive(TEST_KEY);
    }
}
