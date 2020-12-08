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

package org.dromara.soul.admin.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.soul.admin.dto.RuleConditionDTO;
import org.dromara.soul.common.utils.UUIDUtils;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * RuleConditionDO.
 *
 * @author jiangxiaofeng(Nicholas)
 * @author nuo-promise
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class RuleConditionDO extends BaseDO {

    /**
     * rule id.
     */
    private String ruleId;

    /**
     * parameter type.
     */
    private String paramType;

    /**
     * match operator.
     */
    private String operator;

    /**
     * parameter name.
     */
    private String paramName;

    /**
     * parameter value.
     */
    private String paramValue;

    @Builder
    private RuleConditionDO(final String id, final Timestamp dateCreated, final Timestamp dateUpdated, final String ruleId,
                            final String paramType, final String operator, final String paramName, final String paramValue) {
        super(id, dateCreated, dateUpdated);
        this.ruleId = ruleId;
        this.paramType = paramType;
        this.operator = operator;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    /**
     * build ruleConditionDO.
     *
     * @param ruleConditionDTO {@linkplain RuleConditionDTO}
     * @return {@linkplain RuleConditionDO}
     */
    public static RuleConditionDO buildRuleConditionDO(final RuleConditionDTO ruleConditionDTO) {
        return Optional.ofNullable(ruleConditionDTO).map(item -> {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            RuleConditionDO ruleConditionDO = RuleConditionDO.builder()
                    .paramType(item.getParamType())
                    .ruleId(item.getRuleId())
                    .operator(item.getOperator())
                    .paramName(item.getParamName())
                    .paramValue(item.getParamValue())
                    .dateUpdated(currentTime)
                    .build();
            if (StringUtils.isEmpty(item.getId())) {
                ruleConditionDO.setId(UUIDUtils.getInstance().generateShortUuid());
                ruleConditionDO.setDateCreated(currentTime);
            } else {
                ruleConditionDO.setId(item.getId());
            }
            return ruleConditionDO;
        }).orElse(null);
    }
}
