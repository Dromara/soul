package org.dromara.soul.client.alibaba.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.spring.ServiceBean;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.soul.client.common.utils.OkHttpTools;
import org.dromara.soul.client.dubbo.common.annotation.SoulDubboClient;
import org.dromara.soul.client.dubbo.common.config.DubboConfig;
import org.dromara.soul.client.dubbo.common.dto.MetaDataDTO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * The Alibaba Dubbo ServiceBean PostProcessor.
 *
 * @author xiaoyu
 */
@Slf4j
public class AlibabaDubboServiceBeanPostProcessor implements BeanPostProcessor {
    
    private DubboConfig dubboConfig;
    
    private ExecutorService executorService;
    
    private final String url;
    
    public AlibabaDubboServiceBeanPostProcessor(final DubboConfig dubboConfig) {
        String contextPath = dubboConfig.getContextPath();
        String adminUrl = dubboConfig.getAdminUrl();
        if (contextPath == null || "".equals(contextPath)
                || adminUrl == null || "".equals(adminUrl)) {
            throw new RuntimeException("Alibaba dubbo client must config the contextPath, adminUrl");
        }
        this.dubboConfig = dubboConfig;
        url = dubboConfig.getAdminUrl() + "/soul-client/dubbo-register";
        executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof ServiceBean) {
            executorService.execute(() -> handler((ServiceBean) bean));
        }
        return bean;
    }
    
    private void handler(final ServiceBean serviceBean) {
        Class<?> clazz = serviceBean.getRef().getClass();
        if (ClassUtils.isCglibProxyClass(clazz)) {
            String superClassName = clazz.getGenericSuperclass().getTypeName();
            try {
                clazz = Class.forName(superClassName);
            } catch (ClassNotFoundException e) {
                log.error(String.format("class not found: %s", superClassName));
                return;
            }
        }
        final Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(clazz);
        for (Method method : methods) {
            SoulDubboClient soulDubboClient = method.getAnnotation(SoulDubboClient.class);
            if (Objects.nonNull(soulDubboClient)) {
                post(buildJsonParams(serviceBean, soulDubboClient, method));
            }
        }
    }
    
    private String buildJsonParams(final ServiceBean serviceBean, final SoulDubboClient soulDubboClient, final Method method) {
        String appName = dubboConfig.getAppName();
        if (appName == null || "".equals(appName)) {
            appName = serviceBean.getApplication().getName();
        }
        String path = dubboConfig.getContextPath() + soulDubboClient.path();
        String desc = soulDubboClient.desc();
        String serviceName = serviceBean.getInterface();
        String configRuleName = soulDubboClient.ruleName();
        String ruleName = ("".equals(configRuleName)) ? path : configRuleName;
        String methodName = method.getName();
        Class<?>[] parameterTypesClazz = method.getParameterTypes();
        String parameterTypes = Arrays.stream(parameterTypesClazz).map(Class::getName)
                .collect(Collectors.joining(","));
        MetaDataDTO metaDataDTO = MetaDataDTO.builder()
                .appName(appName)
                .serviceName(serviceName)
                .methodName(methodName)
                .contextPath(dubboConfig.getContextPath())
                .path(path)
                .ruleName(ruleName)
                .pathDesc(desc)
                .parameterTypes(parameterTypes)
                .rpcExt(buildRpcExt(serviceBean))
                .rpcType("dubbo")
                .enabled(soulDubboClient.enabled())
                .build();
        return OkHttpTools.getInstance().getGosn().toJson(metaDataDTO);
        
    }
    
    private String buildRpcExt(final ServiceBean serviceBean) {
        MetaDataDTO.RpcExt build = MetaDataDTO.RpcExt.builder()
                .group(StringUtils.isNotEmpty(serviceBean.getGroup()) ? serviceBean.getGroup() : "")
                .version(StringUtils.isNotEmpty(serviceBean.getVersion()) ? serviceBean.getVersion() : "")
                .loadbalance(StringUtils.isNotEmpty(serviceBean.getLoadbalance()) ? serviceBean.getLoadbalance() : Constants.DEFAULT_LOADBALANCE)
                .retries(Objects.isNull(serviceBean.getRetries()) ? Constants.DEFAULT_RETRIES : serviceBean.getRetries())
                .timeout(Objects.isNull(serviceBean.getTimeout()) ? Constants.DEFAULT_CONNECT_TIMEOUT : serviceBean.getTimeout())
                .url("")
                .build();
        return OkHttpTools.getInstance().getGosn().toJson(build);
        
    }
    
    private void post(final String json) {
        try {
            String result = OkHttpTools.getInstance().post(url, json);
            if (Objects.equals(result, "success")) {
                log.info("dubbo client register success :{} " + json);
            } else {
                log.error("dubbo client register error :{} " + json);
            }
        } catch (IOException e) {
            log.error("cannot register soul admin param :{}", url + ":" + json);
        }
    }
}
