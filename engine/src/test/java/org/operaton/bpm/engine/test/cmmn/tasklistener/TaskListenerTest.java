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
package org.operaton.bpm.engine.test.cmmn.tasklistener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.operaton.bpm.engine.delegate.TaskListener;
import org.operaton.bpm.engine.impl.cmmn.execution.CmmnExecution;
import org.operaton.bpm.engine.runtime.VariableInstanceQuery;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.test.cmmn.tasklistener.util.FieldInjectionTaskListener;
import org.operaton.bpm.engine.test.cmmn.tasklistener.util.MySpecialTaskListener;
import org.operaton.bpm.engine.test.cmmn.tasklistener.util.MyTaskListener;
import org.operaton.bpm.engine.test.cmmn.tasklistener.util.NotTaskListener;
import org.operaton.bpm.engine.test.cmmn.tasklistener.util.TaskDeleteListener;
import org.operaton.bpm.engine.test.util.PluggableProcessEngineTest;
import org.junit.Test;

/**
 * @author Roman Smirnov
 *
 */
public class TaskListenerTest extends PluggableProcessEngineTest {

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCreateListenerByClass.cmmn"})
  @Test
  public void testCreateListenerByClass() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(3);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCreateListenerByExpression.cmmn"})
  @Test
  public void testCreateListenerByExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MyTaskListener())
      .create()
      .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCreateListenerByDelegateExpression.cmmn"})
  @Test
  public void testCreateListenerByDelegateExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MySpecialTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCreateListenerByScript.cmmn"})
  @Test
  public void testCreateListenerByScript() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(2);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCompleteListenerByClass.cmmn"})
  @Test
  public void testCompleteListenerByClass() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(3);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCompleteListenerByExpression.cmmn"})
  @Test
  public void testCompleteListenerByExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MyTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCompleteListenerByDelegateExpression.cmmn"})
  @Test
  public void testCompleteListenerByDelegateExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MySpecialTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testCompleteListenerByScript.cmmn"})
  @Test
  public void testCompleteListenerByScript() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(2);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDeleteListenerByClass.cmmn"})
  @Test
  public void testDeleteListenerByClass() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(3);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDeleteListenerByExpression.cmmn"})
  @Test
  public void testDeleteListenerByExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MyTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDeleteListenerByDelegateExpression.cmmn"})
  @Test
  public void testDeleteListenerByDelegateExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MySpecialTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDeleteListenerByScript.cmmn"})
  @Test
  public void testDeleteListenerByScript() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(2);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDeleteListenerByCaseInstanceDeletion.cmmn"})
  @Test
  public void testDeleteListenerByCaseInstanceDeletion() {
    TaskDeleteListener.clear();

    // given
    final String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    processEngineConfiguration
      .getCommandExecutorTxRequired()
      .execute(commandContext -> {
      commandContext
          .getCaseExecutionManager()
          .deleteCaseInstance(caseInstanceId, null);
      return null;
    });

    // then
    assertThat(TaskDeleteListener.eventCounter).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAssignmentListenerByClass.cmmn"})
  @Test
  public void testAssignmentListenerByClass() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(3);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAssignmentListenerByExpression.cmmn"})
  @Test
  public void testAssignmentListenerByExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MyTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAssignmentListenerByDelegateExpression.cmmn"})
  @Test
  public void testAssignmentListenerByDelegateExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MySpecialTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAssignmentListenerByScript.cmmn"})
  @Test
  public void testAssignmentListenerByScript() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(2);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAssignmentListenerByInitialInstantiation.cmmn"})
  @Test
  public void testAssignmentListenerByInitialInstantiation() {
    // given
    String caseInstanceId = caseService
        .withCaseDefinitionByKey("case")
        .create()
        .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(3);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testUpdateListenerByClass.cmmn"})
  @Test
  public void testUpdateListenerByClass() {
    // given
    String caseInstanceId = caseService
        .withCaseDefinitionByKey("case")
        .create()
        .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(3);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testUpdateListenerByExpression.cmmn"})
  @Test
  public void testUpdateListenerByExpression() {
    // given
    String caseInstanceId = caseService
        .withCaseDefinitionByKey("case")
        .setVariable("myTaskListener", new MyTaskListener())
        .create()
        .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testUpdateListenerByDelegateExpression.cmmn"})
  @Test
  public void testUpdateListenerByDelegateExpression() {
    // given
    String caseInstanceId = caseService
        .withCaseDefinitionByKey("case")
        .setVariable("myTaskListener", new MySpecialTaskListener())
        .create()
        .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);
    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(1);

  }
  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testUpdateListenerByScript.cmmn"})
  @Test
  public void testUpdateListenerByScript() {
    // given
    String caseInstanceId = caseService
        .withCaseDefinitionByKey("case")
        .create()
        .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    // when
    taskService.setAssignee(taskId, "jonny");

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(2);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testUpdateListenerByInitialInstantiation.cmmn"})
  @Test
  public void testUpdateListenerNotInvokedByInitialInstantiation() {
    // given
    String caseInstanceId = caseService
        .withCaseDefinitionByKey("case")
        .create()
        .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isZero();
  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByClass.cmmn"})
  @Test
  public void testAllListenerByClassExcludingDeletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(9);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByClass.cmmn"})
  @Test
  public void testAllListenerByClassExcludingCompletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(9);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByExpression.cmmn"})
  @Test
  public void testAllListenerByExpressionExcludingDeletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MyTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(10);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByExpression.cmmn"})
  @Test
  public void testAllListenerByExpressionExcludingCompletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MyTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(10);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByDelegateExpression.cmmn"})
  @Test
  public void testAllListenerByDelegateExpressionExcludingDeletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MySpecialTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when

    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(10);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByDelegateExpression.cmmn"})
  @Test
  public void testAllListenerByDelegateExpressionExcludingCompletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new MySpecialTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(10);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByScript.cmmn"})
  @Test
  public void testAllListenerByScriptExcludingDeletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    //when
    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(9);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testAllListenerByScript.cmmn"})
  @Test
  public void testAllListenerByScriptExcludingCompletion() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    terminate(humanTaskId);

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(9);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("update").singleResult().getValue()).isTrue();
    assertThat(query.variableName("updateEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("delete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("deleteEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(4);

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testFieldInjectionByClass.cmmn"})
  @Test
  public void testFieldInjectionByClass() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(4);

    assertThat(query.variableName("greeting").singleResult().getValue()).isEqualTo("Hello from The Case");
    assertThat(query.variableName("helloWorld").singleResult().getValue()).isEqualTo("Hello World");
    assertThat(query.variableName("prefix").singleResult().getValue()).isEqualTo("ope");
    assertThat(query.variableName("suffix").singleResult().getValue()).isEqualTo("rato");

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testFieldInjectionByDelegateExpression.cmmn"})
  @Test
  public void testFieldInjectionByDelegateExpression() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .setVariable("myTaskListener", new FieldInjectionTaskListener())
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(5);

    assertThat(query.variableName("greeting").singleResult().getValue()).isEqualTo("Hello from The Case");
    assertThat(query.variableName("helloWorld").singleResult().getValue()).isEqualTo("Hello World");
    assertThat(query.variableName("prefix").singleResult().getValue()).isEqualTo("ope");
    assertThat(query.variableName("suffix").singleResult().getValue()).isEqualTo("rato");

  }

  @Deployment(resources = {
      "org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testListenerByScriptResource.cmmn",
      "org/operaton/bpm/engine/test/cmmn/tasklistener/taskListener.groovy"
      })
  @Test
  public void testListenerByScriptResource() {
    // given
    String caseInstanceId = caseService
      .withCaseDefinitionByKey("case")
      .create()
      .getId();

    String humanTaskId = caseService
        .createCaseExecutionQuery()
        .activityId("PI_HumanTask_1")
        .singleResult()
        .getId();

    // when
    String taskId = taskService
        .createTaskQuery()
        .caseExecutionId(humanTaskId)
        .singleResult()
        .getId();

    taskService.setAssignee(taskId, "jonny");

    caseService
      .withCaseExecution(humanTaskId)
      .complete();

    // then
    VariableInstanceQuery query = runtimeService
        .createVariableInstanceQuery()
        .caseInstanceIdIn(caseInstanceId);

    assertThat(query.count()).isEqualTo(7);

    assertThat((Boolean) query.variableName("create").singleResult().getValue()).isTrue();
    assertThat(query.variableName("createEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("assignment").singleResult().getValue()).isTrue();
    assertThat(query.variableName("assignmentEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat((Boolean) query.variableName("complete").singleResult().getValue()).isTrue();
    assertThat(query.variableName("completeEventCounter").singleResult().getValue()).isEqualTo(1);

    assertThat(query.variableName("eventCounter").singleResult().getValue()).isEqualTo(3);
  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDoesNotImplementTaskListenerInterfaceByClass.cmmn"})
  @Test
  public void testDoesNotImplementTaskListenerInterfaceByClass() {
    var caseInstanceBuilder = caseService.withCaseDefinitionByKey("case");
    try {
      caseInstanceBuilder.create();
      fail("exception expected");
    } catch (Exception e) {
      // then
      Throwable cause = e.getCause();
      String message = cause.getMessage();
      testRule.assertTextPresent("NotTaskListener doesn't implement "+TaskListener.class, message);
    }

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testDoesNotImplementTaskListenerInterfaceByDelegateExpression.cmmn"})
  @Test
  public void testDoesNotImplementTaskListenerInterfaceByDelegateExpression() {
    var caseInstanceBuilder = caseService
          .withCaseDefinitionByKey("case")
          .setVariable("myTaskListener", new NotTaskListener());
    try {
      caseInstanceBuilder.create();
      fail("exception expected");
    } catch (Exception e) {
      // then
      Throwable cause = e.getCause();
      String message = cause.getMessage();
      testRule.assertTextPresent("Delegate expression ${myTaskListener} did not resolve to an implementation of interface "+TaskListener.class.getName(), message);
    }

  }

  @Deployment(resources = {"org/operaton/bpm/engine/test/cmmn/tasklistener/TaskListenerTest.testTaskListenerDoesNotExist.cmmn"})
  @Test
  public void testTaskListenerDoesNotExist() {
    var caseInstanceBuilder = caseService.withCaseDefinitionByKey("case");

    try {
      caseInstanceBuilder.create();
      fail("exception expected");
    } catch (Exception e) {
      // then
      Throwable cause = e.getCause();
      String message = cause.getMessage();
      testRule.assertTextPresent("Exception while instantiating class 'org.operaton.bpm.engine.test.cmmn.tasklistener.util.NotExistingTaskListener'", message);
    }

  }

  protected void terminate(final String caseExecutionId) {
    processEngineConfiguration
      .getCommandExecutorTxRequired()
      .execute(commandContext -> {
      CmmnExecution caseTask = (CmmnExecution) caseService
          .createCaseExecutionQuery()
          .caseExecutionId(caseExecutionId)
          .singleResult();
      caseTask.terminate();
      return null;
    });
  }

}
