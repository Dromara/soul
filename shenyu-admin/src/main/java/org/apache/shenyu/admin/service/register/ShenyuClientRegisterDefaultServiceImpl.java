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

package org.apache.shenyu.admin.service.register;

import org.apache.shenyu.admin.listener.DataChangedEvent;
import org.apache.shenyu.admin.mapper.SelectorMapper;
import org.apache.shenyu.admin.model.entity.MetaDataDO;
import org.apache.shenyu.admin.model.entity.SelectorDO;
import org.apache.shenyu.admin.service.SelectorService;
import org.apache.shenyu.admin.utils.ShenyuResultMessage;
import org.apache.shenyu.common.dto.SelectorData;
import org.apache.shenyu.common.dto.convert.DivideUpstream;
import org.apache.shenyu.common.enums.ConfigGroupEnum;
import org.apache.shenyu.common.enums.DataEventTypeEnum;
import org.apache.shenyu.common.utils.GsonUtils;
import org.apache.shenyu.register.common.dto.MetaDataRegisterDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * default service register.
 *
 * @author KevinClair
 **/
@Service("default")
public class ShenyuClientRegisterDefaultServiceImpl extends AbstractShenyuClientRegisterService {

    private final ApplicationEventPublisher eventPublisher;

    private final SelectorService selectorService;

    private final SelectorMapper selectorMapper;

    public ShenyuClientRegisterDefaultServiceImpl(final ApplicationEventPublisher eventPublisher,
                                                  final SelectorService selectorService,
                                                  final SelectorMapper selectorMapper) {
        this.eventPublisher = eventPublisher;
        this.selectorService = selectorService;
        this.selectorMapper = selectorMapper;
    }

    @Override
    public String register(final MetaDataRegisterDTO metaDataRegisterDTO) {
        return null;
    }

    @Override
    public String registerURI(final String contextPath, final List<String> uriList) {
        SelectorDO selector = selectorService.findByName(contextPath);
        SelectorData selectorData = selectorService.buildByName(contextPath);
        String handler = GsonUtils.getInstance().toJson(buildDivideUpstreamList(uriList));
        selector.setHandle(handler);
        selectorData.setHandle(handler);
        selectorMapper.updateSelective(selector);
        // publish change event.
        eventPublisher.publishEvent(new DataChangedEvent(ConfigGroupEnum.SELECTOR, DataEventTypeEnum.UPDATE,
                Collections.singletonList(selectorData)));
        return ShenyuResultMessage.SUCCESS;
    }

    @Override
    protected void saveOrUpdateMetaData(final MetaDataDO exist, final MetaDataRegisterDTO metaDataDTO) {

    }

    @Override
    protected String handlerSelector(final MetaDataRegisterDTO metaDataDTO) {
        return "";
    }

    @Override
    protected void handlerRule(final String selectorId, final MetaDataRegisterDTO metaDataDTO, final MetaDataDO exist) {

    }

    private List<DivideUpstream> buildDivideUpstreamList(final List<String> uriList) {
        return uriList.stream().map(this::buildDivideUpstream).collect(Collectors.toList());
    }

    @Override
    protected String getPluginId(final String pluginName) {
        return "";
    }
}
