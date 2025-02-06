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
package org.operaton.bpm.engine.test.api.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.operaton.bpm.engine.ProcessEngineException;
import org.operaton.bpm.engine.exception.NullValueException;
import org.operaton.bpm.engine.impl.calendar.DateTimeUtil;
import org.operaton.bpm.engine.repository.Deployment;
import org.operaton.bpm.engine.repository.DeploymentQuery;
import org.operaton.bpm.engine.repository.ProcessApplicationDeployment;
import org.operaton.bpm.engine.test.util.PluggableProcessEngineTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Tom Baeyens
 * @author Ingo Richtsmeier
 */
public class DeploymentQueryTest extends PluggableProcessEngineTest {

  private String deploymentOneId;
  private String deploymentTwoId;

  @Before
  public void setUp() {
    deploymentOneId = repositoryService
      .createDeployment()
      .name("org/operaton/bpm/engine/test/repository/one.bpmn20.xml")
      .addClasspathResource("org/operaton/bpm/engine/test/repository/one.bpmn20.xml")
      .source(ProcessApplicationDeployment.PROCESS_APPLICATION_DEPLOYMENT_SOURCE)
      .deploy()
      .getId();

    deploymentTwoId = repositoryService
      .createDeployment()
      .name("org/operaton/bpm/engine/test/repository/two_.bpmn20.xml")
      .addClasspathResource("org/operaton/bpm/engine/test/repository/two.bpmn20.xml")
      .deploy()
      .getId();


  }

  @After
  public void tearDown() {

    repositoryService.deleteDeployment(deploymentOneId, true);
    repositoryService.deleteDeployment(deploymentTwoId, true);
  }

  @Test
  public void testQueryNoCriteria() {
    DeploymentQuery query = repositoryService.createDeploymentQuery();
    assertEquals(2, query.list().size());
    assertEquals(2, query.count());

    try {
      query.singleResult();
      fail();
    } catch (ProcessEngineException e) {
      assertEquals("Query return 2 results instead of max 1", e.getMessage());
    }
  }

  @Test
  public void testQueryByDeploymentId() {
    DeploymentQuery query = repositoryService.createDeploymentQuery().deploymentId(deploymentOneId);
    assertNotNull(query.singleResult());
    assertEquals(1, query.list().size());
    assertEquals(1, query.count());
  }

  @Test
  public void testQueryByInvalidDeploymentId() {
    DeploymentQuery query = repositoryService.createDeploymentQuery().deploymentId("invalid");
    assertNull(query.singleResult());
    assertEquals(0, query.list().size());
    assertEquals(0, query.count());
    var deploymentQuery = repositoryService.createDeploymentQuery();

    try {
      deploymentQuery.deploymentId(null);
      fail();
    } catch (ProcessEngineException e) {
      assertEquals("Deployment id is null", e.getMessage());
    }
  }

  @Test
  public void testQueryByName() {
    DeploymentQuery query = repositoryService.createDeploymentQuery().deploymentName("org/operaton/bpm/engine/test/repository/two_.bpmn20.xml");
    assertNotNull(query.singleResult());
    assertEquals(1, query.list().size());
    assertEquals(1, query.count());
  }

  @Test
  public void testQueryByInvalidName() {
    DeploymentQuery query = repositoryService.createDeploymentQuery().deploymentName("invalid");
    assertNull(query.singleResult());
    assertEquals(0, query.list().size());
    assertEquals(0, query.count());
    var deploymentQuery = repositoryService.createDeploymentQuery();

    try {
      deploymentQuery.deploymentName(null);
      fail();
    } catch (ProcessEngineException e) {
      assertEquals("deploymentName is null", e.getMessage());
    }
  }

  @Test
  public void testQueryByNameLike() {
    DeploymentQuery query = repositoryService.createDeploymentQuery().deploymentNameLike("%operaton%");
    assertEquals(2, query.list().size());
    assertEquals(2, query.count());

    query = repositoryService.createDeploymentQuery().deploymentNameLike("%two\\_%");
    assertEquals(1, query.list().size());
    assertEquals(1, query.count());
    assertEquals("org/operaton/bpm/engine/test/repository/two_.bpmn20.xml", query.singleResult().getName());
  }

  @Test
  public void testQueryByInvalidNameLike() {
    DeploymentQuery query = repositoryService.createDeploymentQuery().deploymentNameLike("invalid");
    assertNull(query.singleResult());
    assertEquals(0, query.list().size());
    assertEquals(0, query.count());
    var deploymentQuery = repositoryService.createDeploymentQuery();

    try {
      deploymentQuery.deploymentNameLike(null);
      fail();
    } catch (ProcessEngineException e) {
      assertEquals("deploymentNameLike is null", e.getMessage());
    }
  }

  @Test
  public void testQueryByDeploymentBefore() {
    Date later = DateTimeUtil.now().plus(10 * 3600).toDate();
    Date earlier = DateTimeUtil.now().minus(10 * 3600).toDate();

    long count = repositoryService.createDeploymentQuery().deploymentBefore(later).count();
    assertEquals(2, count);

    count = repositoryService.createDeploymentQuery().deploymentBefore(earlier).count();
    assertEquals(0, count);
    var deploymentQuery = repositoryService.createDeploymentQuery();

    try {
      deploymentQuery.deploymentBefore(null);
      fail("Exception expected");
    } catch (NullValueException e) {
      // expected
    }
  }

  @Test
  public void testQueryDeploymentAfter() {
    Date later = DateTimeUtil.now().plus(10 * 3600).toDate();
    Date earlier = DateTimeUtil.now().minus(10 * 3600).toDate();

    long count = repositoryService.createDeploymentQuery().deploymentAfter(later).count();
    assertEquals(0, count);

    count = repositoryService.createDeploymentQuery().deploymentAfter(earlier).count();
    assertEquals(2, count);
    var deploymentQuery = repositoryService.createDeploymentQuery();

    try {
      deploymentQuery.deploymentAfter(null);
      fail("Exception expected");
    } catch (NullValueException e) {
      // expected
    }
  }

  @Test
  public void testQueryBySource() {
    DeploymentQuery query = repositoryService
        .createDeploymentQuery()
        .deploymentSource(ProcessApplicationDeployment.PROCESS_APPLICATION_DEPLOYMENT_SOURCE);

    assertEquals(1, query.list().size());
    assertEquals(1, query.count());
  }

  @Test
  public void testQueryByNullSource() {
    DeploymentQuery query = repositoryService
        .createDeploymentQuery()
        .deploymentSource(null);

    assertEquals(1, query.list().size());
    assertEquals(1, query.count());
  }

  @Test
  public void testQueryByInvalidSource() {
    DeploymentQuery query = repositoryService
        .createDeploymentQuery()
        .deploymentSource("invalid");

    assertEquals(0, query.list().size());
    assertEquals(0, query.count());
  }

  @Test
  public void testQueryDeploymentBetween() {
    Date later = DateTimeUtil.now().plus(10 * 3600).toDate();
    Date earlier = DateTimeUtil.now().minus(10 * 3600).toDate();

    long count = repositoryService
        .createDeploymentQuery()
        .deploymentAfter(earlier)
        .deploymentBefore(later).count();
    assertEquals(2, count);

    count = repositoryService
      .createDeploymentQuery()
      .deploymentAfter(later)
      .deploymentBefore(later)
      .count();
    assertEquals(0, count);

    count = repositoryService
      .createDeploymentQuery()
      .deploymentAfter(earlier)
      .deploymentBefore(earlier)
      .count();
    assertEquals(0, count);

    count = repositoryService
        .createDeploymentQuery()
        .deploymentAfter(later)
        .deploymentBefore(earlier)
        .count();
    assertEquals(0, count);
  }

  @Test
  public void testVerifyDeploymentProperties() {
    List<Deployment> deployments = repositoryService.createDeploymentQuery()
      .orderByDeploymentName()
      .asc()
      .list();

    Deployment deploymentOne = deployments.get(0);
    assertEquals("org/operaton/bpm/engine/test/repository/one.bpmn20.xml", deploymentOne.getName());
    assertEquals(deploymentOneId, deploymentOne.getId());
    assertEquals(ProcessApplicationDeployment.PROCESS_APPLICATION_DEPLOYMENT_SOURCE, deploymentOne.getSource());
    assertNull(deploymentOne.getTenantId());

    Deployment deploymentTwo = deployments.get(1);
    assertEquals("org/operaton/bpm/engine/test/repository/two_.bpmn20.xml", deploymentTwo.getName());
    assertEquals(deploymentTwoId, deploymentTwo.getId());
    assertNull(deploymentTwo.getSource());
    assertNull(deploymentTwo.getTenantId());
  }

  @Test
  public void testQuerySorting() {
    assertEquals(2, repositoryService.createDeploymentQuery()
        .orderByDeploymentName()
        .asc()
        .list()
        .size());

    assertEquals(2, repositoryService.createDeploymentQuery()
      .orderByDeploymentId()
      .asc()
      .list()
      .size());

    assertEquals(2, repositoryService.createDeploymentQuery()
      .orderByDeploymenTime()
      .asc()
      .list()
      .size());

    assertEquals(2, repositoryService.createDeploymentQuery()
      .orderByDeploymentTime()
      .asc()
      .list()
      .size());
  }

}
