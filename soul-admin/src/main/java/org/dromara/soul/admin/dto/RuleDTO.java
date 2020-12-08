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

package org.dromara.soul.admin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * this is rule from by web front.
 *
 * @author jiangxiaofeng(Nicholas)
 * @author nuo-promise
 */
@Data
@NoArgsConstructor
public final class RuleDTO implements Serializable {

    /**
     * primary key.
     */
    @NotNull
    private String id;

    /**
     * selector id.
     */
    private String selectorId;

    /**
     * match mode.
     */
    private Integer matchMode;

    /**
     * rule name.
     */
    private String name;

    /**
     * whether enabled.
     */
    private Boolean enabled;

    /**
     * whether loged.
     */
    private Boolean loged;

    /**
     * sort type.
     */
    private Integer sort;

    /**
     * process logic.
     */
    private String handle;

    /**
     * rule conditions.
     */
    private List<RuleConditionDTO> ruleConditions;

    @Builder
    private RuleDTO(final String id, final String selectorId, final Integer matchMode, final String name, final Boolean enabled,
                    final Boolean loged, final Integer sort, final String handle, final List<RuleConditionDTO> ruleConditions) {
        this.id = id;
        this.selectorId = selectorId;
        this.matchMode = matchMode;
        this.name = name;
        this.enabled = enabled;
        this.loged = loged;
        this.sort = sort;
        this.handle = handle;
        this.ruleConditions = ruleConditions;
    }
}
