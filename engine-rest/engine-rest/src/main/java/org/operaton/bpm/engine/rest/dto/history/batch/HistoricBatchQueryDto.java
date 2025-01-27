/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.operaton.bpm.engine.rest.dto.history.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.MultivaluedMap;

import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.batch.history.HistoricBatchQuery;
import org.operaton.bpm.engine.rest.dto.AbstractQueryDto;
import org.operaton.bpm.engine.rest.dto.OperatonQueryParam;
import org.operaton.bpm.engine.rest.dto.converter.BooleanConverter;
import org.operaton.bpm.engine.rest.dto.converter.StringListConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HistoricBatchQueryDto extends AbstractQueryDto<HistoricBatchQuery> {

  private static final String SORT_BY_BATCH_ID_VALUE = "batchId";
  private static final String SORT_BY_BATCH_START_TIME_VALUE = "startTime";
  private static final String SORT_BY_BATCH_END_TIME_VALUE = "endTime";
  private static final String SORT_BY_TENANT_ID_VALUE = "tenantId";

  protected String batchId;
  protected String type;
  protected Boolean completed;
  protected List<String> tenantIds;
  protected Boolean withoutTenantId;

  private static final List<String> VALID_SORT_BY_VALUES;
  static {
    VALID_SORT_BY_VALUES = new ArrayList<>();
    VALID_SORT_BY_VALUES.add(SORT_BY_BATCH_ID_VALUE);
    VALID_SORT_BY_VALUES.add(SORT_BY_BATCH_START_TIME_VALUE);
    VALID_SORT_BY_VALUES.add(SORT_BY_BATCH_END_TIME_VALUE);
    VALID_SORT_BY_VALUES.add(SORT_BY_TENANT_ID_VALUE);
  }

  public HistoricBatchQueryDto() {
  }

  public HistoricBatchQueryDto(ObjectMapper objectMapper, MultivaluedMap<String, String> queryParameters) {
    super(objectMapper, queryParameters);
  }

  @OperatonQueryParam("batchId")
  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  @OperatonQueryParam("type")
  public void setType(String type) {
    this.type = type;
  }

  @OperatonQueryParam(value = "completed", converter = BooleanConverter.class)
  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  @OperatonQueryParam(value = "tenantIdIn", converter = StringListConverter.class)
  public void setTenantIdIn(List<String> tenantIds) {
    this.tenantIds = tenantIds;
  }

  @OperatonQueryParam(value = "withoutTenantId", converter = BooleanConverter.class)
  public void setWithoutTenantId(Boolean withoutTenantId) {
    this.withoutTenantId = withoutTenantId;
  }

  protected boolean isValidSortByValue(String value) {
    return VALID_SORT_BY_VALUES.contains(value);
  }

  protected HistoricBatchQuery createNewQuery(ProcessEngine engine) {
    return engine.getHistoryService().createHistoricBatchQuery();
  }

  protected void applyFilters(HistoricBatchQuery query) {
    if (batchId != null) {
      query.batchId(batchId);
    }
    if (type != null) {
      query.type(type);
    }
    if (completed != null) {
      query.completed(completed);
    }
    if (Boolean.TRUE.equals(withoutTenantId)) {
      query.withoutTenantId();
    }
    if (tenantIds != null && !tenantIds.isEmpty()) {
      query.tenantIdIn(tenantIds.toArray(new String[tenantIds.size()]));
    }
  }

  protected void applySortBy(HistoricBatchQuery query, String sortBy, Map<String, Object> parameters, ProcessEngine engine) {
    if (sortBy.equals(SORT_BY_BATCH_ID_VALUE)) {
      query.orderById();
    }
    if (sortBy.equals(SORT_BY_BATCH_START_TIME_VALUE)) {
      query.orderByStartTime();
    }
    if (sortBy.equals(SORT_BY_BATCH_END_TIME_VALUE)) {
      query.orderByEndTime();
    }
    if (sortBy.equals(SORT_BY_TENANT_ID_VALUE)) {
      query.orderByTenantId();
    }
  }

}
