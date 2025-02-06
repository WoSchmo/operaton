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
package org.operaton.bpm.engine.test.bpmn.event.timer;

import org.operaton.bpm.engine.ProcessEngineException;
import org.operaton.bpm.engine.impl.interceptor.CommandExecutor;
import org.operaton.bpm.engine.impl.jobexecutor.AsyncContinuationJobHandler;
import org.operaton.bpm.engine.impl.jobexecutor.historycleanup.HistoryCleanupJobHandler;
import org.operaton.bpm.engine.impl.persistence.entity.JobEntity;
import org.operaton.bpm.engine.runtime.Job;
import org.operaton.bpm.engine.runtime.JobQuery;
import org.operaton.bpm.engine.runtime.ProcessInstance;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.test.util.PluggableProcessEngineTest;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Test timer recalculation
 * 
 * @author Tobias Metzke
 */

public class TimerRecalculationTest extends PluggableProcessEngineTest {
	
  private Set<String> jobIds = new HashSet<>();
  
  @After
  public void tearDown() {
    clearMeterLog();

    for (String jobId : jobIds) {
      clearJobLog(jobId);
      clearJob(jobId);
    }
    
    jobIds = new HashSet<>();
  }
	  
  @Test
  public void testUnknownId() {
    try {
      // when
      managementService.recalculateJobDuedate("unknownID", false);
      fail("The recalculation with an unknown job ID should not be possible");
    } catch (ProcessEngineException pe) {
      // then
      testRule.assertTextPresent("No job found with id '" + "unknownID", pe.getMessage());
    }
  }
  
  @Test
  public void testEmptyId() {
    try {
      // when
      managementService.recalculateJobDuedate("", false);
      fail("The recalculation with an unknown job ID should not be possible");
    } catch (ProcessEngineException pe) {
      // then
      testRule.assertTextPresent("The job id is mandatory: jobId is empty", pe.getMessage());
    }
  }
  
  @Test
  public void testNullId() {
    try {
      // when
      managementService.recalculateJobDuedate(null, false);
      fail("The recalculation with an unknown job ID should not be possible");
    } catch (ProcessEngineException pe) {
      // then
      testRule.assertTextPresent("The job id is mandatory: jobId is null", pe.getMessage());
    }
  }

  @Deployment
  @Test
  public void testFinishedJob() {
    // given
    HashMap<String, Object> variables1 = new HashMap<>();
    variables1.put("dueDate", new Date());

    ProcessInstance pi1 = runtimeService.startProcessInstanceByKey("intermediateTimerEventExample", variables1);
    assertEquals(1, managementService.createJobQuery().processInstanceId(pi1.getId()).count());

    JobQuery jobQuery = managementService.createJobQuery().executable();
    assertEquals(1L, jobQuery.count());

    // job duedate can be recalculated, job still exists in runtime
    String jobId = jobQuery.singleResult().getId();
    managementService.recalculateJobDuedate(jobId, false);
    // run the job, finish the process
    managementService.executeJob(jobId);
    assertEquals(0L, managementService.createJobQuery().processInstanceId(pi1.getId()).count());
    testRule.assertProcessEnded(pi1.getProcessInstanceId());
    
    try {
      // when
      managementService.recalculateJobDuedate(jobId, false);
      fail("The recalculation of a finished job should not be possible");
    } catch (ProcessEngineException pe) {
      // then
      testRule.assertTextPresent("No job found with id '" + jobId, pe.getMessage());
    }
  }
  
  @Test
  public void testEverLivingJob() {
    // given
    Job job = historyService.cleanUpHistoryAsync(true);
    jobIds.add(job.getId());
    
    // when & then
    tryRecalculateUnsupported(job, HistoryCleanupJobHandler.TYPE);
  }
  
  @Deployment
  @Test
  public void testMessageJob() {
    // given
    runtimeService.startProcessInstanceByKey("asyncService");
    Job job = managementService.createJobQuery().singleResult();
    jobIds.add(job.getId());
    
    // when & then
    tryRecalculateUnsupported(job, AsyncContinuationJobHandler.TYPE);
  }
  

  // helper /////////////////////////////////////////////////////////////////
  
  protected void tryRecalculateUnsupported(Job job, String type) {
    // given
    String jobId = job.getId();
    try {
      // when
      managementService.recalculateJobDuedate(jobId, false);
      fail("The recalculation with an unsupported type should not be possible");
    } catch (ProcessEngineException pe) {
      // then
      testRule.assertTextPresent("Only timer jobs can be recalculated, but the job with id '" + jobId + "' is of type '" + type, pe.getMessage());
    }
  }

  
  protected void clearMeterLog() {
    processEngineConfiguration.getCommandExecutorTxRequired()
      .execute(commandContext -> {
      commandContext.getMeterLogManager().deleteAll();

      return null;
    });
  }
  
  protected void clearJobLog(final String jobId) {
    CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
    commandExecutor.execute(commandContext -> {
      commandContext.getHistoricJobLogManager().deleteHistoricJobLogByJobId(jobId);
      return null;
    });
  }
  
  protected void clearJob(final String jobId) {
    processEngineConfiguration.getCommandExecutorTxRequired()
      .execute(commandContext -> {
      JobEntity job = commandContext.getJobManager().findJobById(jobId);
      if (job != null) {
        commandContext.getJobManager().delete(job);
      }
      return null;
    });
  }
}
