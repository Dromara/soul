package org.dromara.soul.plugin.debug;

import java.net.InetSocketAddress;
import org.dromara.soul.common.dto.RuleData;
import org.dromara.soul.common.dto.SelectorData;
import org.dromara.soul.common.enums.PluginEnum;
import org.dromara.soul.plugin.api.SoulPluginChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * The Test Case For DebugPlugin.
 *
 * @author xuxd
 **/
@RunWith(MockitoJUnitRunner.class)
public final class DebugPluginTest {

    private DebugPlugin debugPlugin;

    private ServerWebExchange exchange;

    private RuleData ruleData;

    private SoulPluginChain chain;

    private SelectorData selectorData;

    @Before
    public void setUp() {
        this.debugPlugin = new DebugPlugin();
        this.ruleData = mock(RuleData.class);
        this.chain = mock(SoulPluginChain.class);
        this.selectorData = mock(SelectorData.class);
        MockServerHttpRequest request = MockServerHttpRequest
            .post("/path")
            .body("request body")
            .get("localhost")
            .remoteAddress(new InetSocketAddress(8090))
            .header("X-source", "mock test")
            .queryParam("queryParam", "Hello,World")
            .build();
        this.exchange = spy(MockServerWebExchange.from(request));
    }

    @Test
    public void testDoExecute() {
        ServerWebExchange.Builder builder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(builder);
        when(builder.request(any(DebugPlugin.DebugServerHttpRequest.class))).thenReturn(builder);
        when(builder.response(any(DebugPlugin.DebugServerHttpResponse.class))).thenReturn(builder);
        when(builder.build()).thenReturn(exchange);
        when(chain.execute(any())).thenReturn(Mono.empty());
        Mono<Void> result = debugPlugin.doExecute(exchange, chain, selectorData, ruleData);
        // Sorry, I do not how to mock this case by an simply way, so I give up.

        StepVerifier.create(result).expectSubscription().verifyComplete();
    }

    @Test
    public void testGetOrder() {
        Assert.assertEquals(debugPlugin.getOrder(), PluginEnum.DEBUG.getCode());
    }

    @Test
    public void testNamed() {
        Assert.assertEquals(debugPlugin.named(), PluginEnum.DEBUG.getName());
    }

    @Test
    public void testSkip() {
        Assert.assertFalse(debugPlugin.skip(exchange));
    }
}
