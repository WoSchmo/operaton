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
package org.operaton.bpm.engine.test.assertions.bpmn;

import static org.operaton.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.operaton.bpm.engine.test.assertions.bpmn.BpmnAwareTests.jobQuery;
import static org.operaton.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.operaton.bpm.engine.runtime.ProcessInstance;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.test.ProcessEngineRule;
import org.operaton.bpm.engine.test.assertions.helpers.ProcessAssertTestCase;
import org.junit.Rule;
import org.junit.Test;

public class JobAssertHasProcessInstanceIdTest extends ProcessAssertTestCase {

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Test
  @Deployment(resources = {"bpmn/JobAssert-hasProcessInstanceId.bpmn"
  })
  public void testHasProcessInstanceId_Success() {
    // When
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(
      "JobAssert-hasProcessInstanceId"
    );
    // Then
    assertThat(jobQuery().singleResult()).isNotNull();
    // And
    assertThat(jobQuery().singleResult()).hasProcessInstanceId(processInstance.getProcessInstanceId());
  }

  @Test
  @Deployment(resources = {"bpmn/JobAssert-hasProcessInstanceId.bpmn"
  })
  public void testHasProcessInstanceId_Failure() {
    // When
    runtimeService().startProcessInstanceByKey(
      "JobAssert-hasProcessInstanceId"
    );
    // Then
    assertThat(jobQuery().singleResult()).isNotNull();
    // And
    expect(() -> assertThat(jobQuery().singleResult()).hasProcessInstanceId("someOtherId"));
  }

  @Test
  @Deployment(resources = {"bpmn/JobAssert-hasProcessInstanceId.bpmn"
  })
  public void testHasProcessInstanceId_Error_Null() {
    // When
    runtimeService().startProcessInstanceByKey(
      "JobAssert-hasProcessInstanceId"
    );
    // Then
    assertThat(jobQuery().singleResult()).isNotNull();
    // And
    expect(() -> assertThat(jobQuery().singleResult()).hasProcessInstanceId(null));
  }

}
