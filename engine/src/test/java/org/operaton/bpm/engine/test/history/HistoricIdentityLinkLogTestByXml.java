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
package org.operaton.bpm.engine.test.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.operaton.bpm.engine.ProcessEngineConfiguration;
import org.operaton.bpm.engine.history.HistoricIdentityLinkLog;
import org.operaton.bpm.engine.history.HistoricIdentityLinkLogQuery;
import org.operaton.bpm.engine.repository.ProcessDefinition;
import org.operaton.bpm.engine.runtime.ProcessInstance;
import org.operaton.bpm.engine.task.IdentityLink;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.test.RequiredHistoryLevel;
import org.operaton.bpm.engine.test.util.PluggableProcessEngineTest;
import org.junit.Test;

@RequiredHistoryLevel(ProcessEngineConfiguration.HISTORY_FULL)
public class HistoricIdentityLinkLogTestByXml extends PluggableProcessEngineTest {

  private static String PROCESS_DEFINITION_KEY_CANDIDATE_USER = "oneTaskProcessForHistoricIdentityLinkWithCanidateUser";
  private static String PROCESS_DEFINITION_KEY_CANDIDATE_GROUP = "oneTaskProcessForHistoricIdentityLinkWithCanidateGroup";
  private static String PROCESS_DEFINITION_KEY_ASSIGNEE = "oneTaskProcessForHistoricIdentityLinkWithAssignee";
  private static String PROCESS_DEFINITION_KEY_CANDIDATE_STARTER_USER = "oneTaskProcessForHistoricIdentityLinkWithCanidateStarterUsers";
  private static String PROCESS_DEFINITION_KEY_CANDIDATE_STARTER_GROUP = "oneTaskProcessForHistoricIdentityLinkWithCanidateStarterGroups";
  private static final String XML_USER = "demo";
  private static final String XML_GROUP = "demoGroups";
  private static final String XML_ASSIGNEE = "assignee";

  protected static final String TENANT_ONE = "tenant1";

  protected static final String CANDIDATE_STARTER_USER = "org/operaton/bpm/engine/test/api/repository/ProcessDefinitionCandidateTest.testCandidateStarterUser.bpmn20.xml";
  protected static final String CANDIDATE_STARTER_USERS = "org/operaton/bpm/engine/test/api/repository/ProcessDefinitionCandidateTest.testCandidateStarterUsers.bpmn20.xml";

  protected static final String CANDIDATE_STARTER_GROUP = "org/operaton/bpm/engine/test/api/repository/ProcessDefinitionCandidateTest.testCandidateStarterGroup.bpmn20.xml";
  protected static final String CANDIDATE_STARTER_GROUPS = "org/operaton/bpm/engine/test/api/repository/ProcessDefinitionCandidateTest.testCandidateStarterGroups.bpmn20.xml";

  @Deployment(resources = { "org/operaton/bpm/engine/test/api/runtime/OneTaskProcessWithCandidateUser.bpmn20.xml" })
  @Test
  public void testShouldAddTaskCandidateforAddIdentityLinkUsingXml() {

    // Pre test
    List<HistoricIdentityLinkLog> historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(0, historicIdentityLinks.size());

    // given
    startProcessInstance(PROCESS_DEFINITION_KEY_CANDIDATE_USER);
    historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // query Test
    HistoricIdentityLinkLogQuery query = historyService.createHistoricIdentityLinkLogQuery();
    assertEquals(1, query.userId(XML_USER).count());
  }

  @Deployment(resources = { "org/operaton/bpm/engine/test/api/runtime/OneTaskProcessWithTaskAssignee.bpmn20.xml" })
  @Test
  public void testShouldAddTaskAssigneeforAddIdentityLinkUsingXml() {

    // Pre test
    List<HistoricIdentityLinkLog> historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(0, historicIdentityLinks.size());

    // given
    startProcessInstance(PROCESS_DEFINITION_KEY_ASSIGNEE);
    historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // query Test
    HistoricIdentityLinkLogQuery query = historyService.createHistoricIdentityLinkLogQuery();
    assertEquals(1, query.userId(XML_ASSIGNEE).count());


  }

  @Deployment(resources = { "org/operaton/bpm/engine/test/api/runtime/OneTaskProcessWithCandidateGroups.bpmn20.xml" })
  @Test
  public void testShouldAddTaskCandidateGroupforAddIdentityLinkUsingXml() {

    // Pre test
    List<HistoricIdentityLinkLog> historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(0, historicIdentityLinks.size());

    // given
    startProcessInstance(PROCESS_DEFINITION_KEY_CANDIDATE_GROUP);
    historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // query Test
    HistoricIdentityLinkLogQuery query = historyService.createHistoricIdentityLinkLogQuery();
    assertEquals(1, query.groupId(XML_GROUP).count());
  }

  @Deployment(resources = { "org/operaton/bpm/engine/test/api/runtime/OneTaskProcessWithCandidateStarterUsers.bpmn20.xml" })
  @Test
  public void testShouldAddProcessCandidateStarterUserforAddIdentityLinkUsingXml() {

    // Pre test - Historical identity link is added as part of deployment
    List<HistoricIdentityLinkLog> historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // given
    ProcessDefinition latestProcessDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(PROCESS_DEFINITION_KEY_CANDIDATE_STARTER_USER)
        .singleResult();
    assertNotNull(latestProcessDef);

    List<IdentityLink> links = repositoryService.getIdentityLinksForProcessDefinition(latestProcessDef.getId());
    assertEquals(1, links.size());

    historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // query Test
    HistoricIdentityLinkLogQuery query = historyService.createHistoricIdentityLinkLogQuery();
    assertEquals(1, query.userId(XML_USER).count());
  }
  @Deployment(resources = { "org/operaton/bpm/engine/test/api/runtime/OneTaskProcessWithCandidateStarterGroups.bpmn20.xml" })
  public void testShouldAddProcessCandidateStarterGroupforAddIdentityLinkUsingXml() {

    // Pre test - Historical identity link is added as part of deployment
    List<HistoricIdentityLinkLog> historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // given
    ProcessDefinition latestProcessDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(PROCESS_DEFINITION_KEY_CANDIDATE_STARTER_GROUP)
        .singleResult();
    assertNotNull(latestProcessDef);

    List<IdentityLink> links = repositoryService.getIdentityLinksForProcessDefinition(latestProcessDef.getId());
    assertEquals(1, links.size());

    historicIdentityLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicIdentityLinks.size());

    // query Test
    HistoricIdentityLinkLogQuery query = historyService.createHistoricIdentityLinkLogQuery();
    assertEquals(1, query.groupId(XML_GROUP).count());
  }

  @Test
  public void testPropagateTenantIdToCandidateStarterUser() {
    // when
    org.operaton.bpm.engine.repository.Deployment deployment = repositoryService.createDeployment()
      .addClasspathResource(CANDIDATE_STARTER_USER)
      .tenantId(TENANT_ONE)
      .deploy();

    // then
    List<HistoricIdentityLinkLog> historicLinks = historyService.createHistoricIdentityLinkLogQuery().list();
    assertEquals(1, historicLinks.size());

    HistoricIdentityLinkLog historicLink = historicLinks.get(0);
    assertNotNull(historicLink.getTenantId());
    assertEquals(TENANT_ONE, historicLink.getTenantId());

    repositoryService.deleteDeployment(deployment.getId(), true);
  }

  @Test
  public void testPropagateTenantIdToCandidateStarterUsers() {
    // when
    org.operaton.bpm.engine.repository.Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource(CANDIDATE_STARTER_USERS)
        .tenantId(TENANT_ONE)
        .deploy();

      // then
      List<HistoricIdentityLinkLog> historicLinks = historyService.createHistoricIdentityLinkLogQuery().list();
      assertEquals(3, historicLinks.size());

    for (HistoricIdentityLinkLog historicLink : historicLinks) {
      assertNotNull(historicLink.getTenantId());
      assertEquals(TENANT_ONE, historicLink.getTenantId());
    }

    repositoryService.deleteDeployment(deployment.getId(), true);
  }

  @Test
  public void testPropagateTenantIdToCandidateStarterGroup() {
    // when
    org.operaton.bpm.engine.repository.Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource(CANDIDATE_STARTER_GROUP)
        .tenantId(TENANT_ONE)
        .deploy();

      // then
      List<HistoricIdentityLinkLog> historicLinks = historyService.createHistoricIdentityLinkLogQuery().list();
      assertEquals(1, historicLinks.size());

      HistoricIdentityLinkLog historicLink = historicLinks.get(0);
      assertNotNull(historicLink.getTenantId());
      assertEquals(TENANT_ONE, historicLink.getTenantId());

      repositoryService.deleteDeployment(deployment.getId(), true);
  }

  @Test
  public void testPropagateTenantIdToCandidateStarterGroups() {
    // when
    org.operaton.bpm.engine.repository.Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource(CANDIDATE_STARTER_GROUPS)
        .tenantId(TENANT_ONE)
        .deploy();

      // then
      List<HistoricIdentityLinkLog> historicLinks = historyService.createHistoricIdentityLinkLogQuery().list();
      assertEquals(3, historicLinks.size());

    for (HistoricIdentityLinkLog historicLink : historicLinks) {
      assertNotNull(historicLink.getTenantId());
      assertEquals(TENANT_ONE, historicLink.getTenantId());
    }

    repositoryService.deleteDeployment(deployment.getId(), true);
  }

  protected ProcessInstance startProcessInstance(String key) {
    return runtimeService.startProcessInstanceByKey(key);
  }
}
