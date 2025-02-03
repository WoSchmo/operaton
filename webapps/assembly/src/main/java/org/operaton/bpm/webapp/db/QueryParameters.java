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
package org.operaton.bpm.webapp.db;

import org.operaton.bpm.engine.impl.db.ListQueryParameterObject;

/**
 * Typed query parameters for usage in webapp plugins
 */
public abstract class QueryParameters extends ListQueryParameterObject {

  private static final long serialVersionUID = 1L;

  protected boolean historyEnabled = true;
  protected boolean maxResultsLimitEnabled = true;

  protected QueryParameters() { }

  protected QueryParameters(int firstResult, int maxResults) {
    this.firstResult = firstResult;
    this.maxResults = maxResults;
  }

  public boolean isHistoryEnabled() {
    return historyEnabled;
  }

  public void setHistoryEnabled(boolean historyEnabled) {
    this.historyEnabled = historyEnabled;
  }

  public boolean isMaxResultsLimitEnabled() {
    return maxResultsLimitEnabled;
  }

  public void disableMaxResultsLimit() {
    maxResultsLimitEnabled = false;
  }

}
