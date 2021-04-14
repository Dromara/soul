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

package org.dromara.soul.plugin.ratelimiter;

import org.dromara.soul.common.dto.RuleData;
import org.dromara.soul.common.dto.SelectorData;
import org.dromara.soul.common.dto.convert.RateLimiterHandle;
import org.dromara.soul.common.enums.PluginEnum;
import org.dromara.soul.common.utils.GsonUtils;
import org.dromara.soul.plugin.api.SoulPluginChain;
import org.dromara.soul.plugin.api.result.SoulResultEnum;
import org.dromara.soul.plugin.api.result.SoulResultWrap;
import org.dromara.soul.plugin.api.utils.WebFluxResultUtils;
import org.dromara.soul.plugin.base.AbstractSoulPlugin;
import org.dromara.soul.plugin.base.utils.Singleton;
import org.dromara.soul.plugin.ratelimiter.algorithm.ConcurrentRateLimiterAlgorithm;
import org.dromara.soul.plugin.ratelimiter.executor.RedisRateLimiter;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * RateLimiter Plugin.
 *
 * @author xiaoyu(Myth)
 */
public class RateLimiterPlugin extends AbstractSoulPlugin {

    private final RedisRateLimiter redisRateLimiter;

    /**
     * Instantiates a new Rate limiter plugin.
     *
     * @param redisRateLimiter  the redis rate limiter
     */
    public RateLimiterPlugin(final RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public String named() {
        return PluginEnum.RATE_LIMITER.getName();
    }

    @Override
    public int getOrder() {
        return PluginEnum.RATE_LIMITER.getCode();
    }

    @Override
    protected Mono<Void> doExecute(final ServerWebExchange exchange, final SoulPluginChain chain, final SelectorData selector, final RuleData rule) {
        String handle = rule.getHandle();
        RateLimiterHandle limiterHandle = GsonUtils.getInstance().fromJson(handle, RateLimiterHandle.class);
        String id = getLimiterId(exchange, rule, limiterHandle);
        return redisRateLimiter.isAllowed(id, limiterHandle)
                .flatMap(response -> {
                    if (!response.isAllowed()) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        Object error = SoulResultWrap.error(SoulResultEnum.TOO_MANY_REQUESTS.getCode(), SoulResultEnum.TOO_MANY_REQUESTS.getMsg(), null);
                        return WebFluxResultUtils.result(exchange, error);
                    }
                    return chain.execute(exchange);
                }).doFinally(signalType -> {
                    if (ConcurrentRateLimiterAlgorithm.CONCURRENT_RATE_LIMITER_NAME.equals(limiterHandle.getAlgorithmName())) {
                        String tokenKey = "concurrent_request_rate_limiter.{}.tokens";
                        Mono<Long> mono = Singleton.INST.get(ReactiveRedisTemplate.class).opsForZSet().remove(tokenKey, id);
                        mono.subscribe();
                    }
                });
    }

    private String getLimiterId(final ServerWebExchange exchange, final RuleData rule, final RateLimiterHandle limiterHandle) {
        String id;
        if (ConcurrentRateLimiterAlgorithm.CONCURRENT_RATE_LIMITER_NAME.equals(limiterHandle.getAlgorithmName())) {
            id = exchange.getRequest().getId();
        } else {
            id = rule.getId();
        }
        return id;
    }
}
