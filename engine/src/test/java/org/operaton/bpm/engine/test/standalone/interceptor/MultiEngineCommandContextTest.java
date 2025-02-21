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
package org.operaton.bpm.engine.test.standalone.interceptor;

import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.operaton.bpm.model.bpmn.Bpmn;
import org.operaton.bpm.model.bpmn.BpmnModelInstance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel Meyer
 *
 */
public class MultiEngineCommandContextTest {

  protected ProcessEngine engine1;
  protected ProcessEngine engine2;

  @Before
  public void startEngines() {
    engine1 = createProcessEngine("engine1");
    engine2 = createProcessEngine("engine2");
    StartProcessInstanceOnEngineDelegate.ENGINES.put("engine1", engine1);
    StartProcessInstanceOnEngineDelegate.ENGINES.put("engine2", engine2);
  }

  @After
  public void closeEngine1() {
    try {
      engine1.close();
    }
    finally {
      engine1 = null;
    }
  }

  @After
  public void closeEngine2() {
    try {
      engine2.close();
    }
    finally {
      engine2 = null;
    }
  }

  @After
  public void removeEngines() {
    StartProcessInstanceOnEngineDelegate.ENGINES.clear();
  }

  @Test
  public void shouldOpenNewCommandContextWhenInteractingAcrossEngines() {
    BpmnModelInstance process1 = Bpmn.createExecutableProcess("process1")
        .startEvent()
        .serviceTask()
          .operatonInputParameter("engineName", "engine2")
          .operatonInputParameter("processKey", "process2")
          .operatonClass(StartProcessInstanceOnEngineDelegate.class.getName())
        .endEvent()
        .done();

    BpmnModelInstance process2 = Bpmn.createExecutableProcess("process2")
        .startEvent()
        .endEvent()
        .done();

    // given
    engine1.getRepositoryService().createDeployment().addModelInstance("process1.bpmn", process1).deploy();
    engine2.getRepositoryService().createDeployment().addModelInstance("process2.bpmn", process2).deploy();

    // when
    engine1.getRuntimeService().startProcessInstanceByKey("process1");

    // then
    var processInstance1 = engine1.getHistoryService().createHistoricProcessInstanceQuery()
      .processDefinitionKey("process1")
      .singleResult();
    assertThat(processInstance1).isNotNull();
  }

  @Test
  public void shouldOpenNewCommandContextWhenInteractingWithOtherEngineAndBack() {

    BpmnModelInstance process1 = Bpmn.createExecutableProcess("process1")
        .startEvent()
        .serviceTask()
          .operatonInputParameter("engineName", "engine2")
          .operatonInputParameter("processKey", "process2")
          .operatonClass(StartProcessInstanceOnEngineDelegate.class.getName())
        .endEvent()
        .done();

    BpmnModelInstance process2 = Bpmn.createExecutableProcess("process2")
        .startEvent()
        .serviceTask()
          .operatonInputParameter("engineName", "engine1")
          .operatonInputParameter("processKey", "process3")
          .operatonClass(StartProcessInstanceOnEngineDelegate.class.getName())
        .done();

    BpmnModelInstance process3 = Bpmn.createExecutableProcess("process3")
        .startEvent()
        .endEvent()
        .done();

    // given
    engine1.getRepositoryService().createDeployment().addModelInstance("process1.bpmn", process1).deploy();
    engine2.getRepositoryService().createDeployment().addModelInstance("process2.bpmn", process2).deploy();
    engine1.getRepositoryService().createDeployment().addModelInstance("process3.bpmn", process3).deploy();

    // when
    engine1.getRuntimeService().startProcessInstanceByKey("process1");

    // then
    var processInstance1 = engine1.getHistoryService().createHistoricProcessInstanceQuery()
      .processDefinitionKey("process1")
      .singleResult();
    assertThat(processInstance1).isNotNull();
  }

  private ProcessEngine createProcessEngine(String name) {
    StandaloneInMemProcessEngineConfiguration processEngineConfiguration = new StandaloneInMemProcessEngineConfiguration();

    processEngineConfiguration.setProcessEngineName(name);
    processEngineConfiguration.setJdbcUrl(String.format("jdbc:h2:mem:%s", name));
    processEngineConfiguration.setEnforceHistoryTimeToLive(false);

    return processEngineConfiguration.buildProcessEngine();
  }

}
