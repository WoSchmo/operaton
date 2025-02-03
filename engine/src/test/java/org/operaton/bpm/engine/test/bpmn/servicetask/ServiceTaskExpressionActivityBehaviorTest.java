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
package org.operaton.bpm.engine.test.bpmn.servicetask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Map;

import org.operaton.bpm.engine.ProcessEngineException;
import org.operaton.bpm.engine.exception.NullValueException;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.test.util.PluggableProcessEngineTest;
import org.junit.Test;

/**
 * @author Ronny Bräunlich
 *
 */
public class ServiceTaskExpressionActivityBehaviorTest extends PluggableProcessEngineTest {

  @Deployment
  @Test
  public void testExceptionThrownBySecondScopeServiceTaskIsNotHandled(){
    Map<Object, Object> beans = processEngineConfiguration.getBeans();
    beans.put("dummyServiceTask", new DummyServiceTask());
    processEngineConfiguration.setBeans(beans);
    var variables = Collections.<String, Object>singletonMap("count", 0);

    try{
      runtimeService.startProcessInstanceByKey("process", variables);
      fail();
      // the EL resolver will wrap the actual exception inside a process engine exception
    }
    //since the NVE extends the ProcessEngineException we have to handle it separately
    catch(NullValueException nve){
      fail("Shouldn't have received NullValueException");
    }
    catch(ProcessEngineException e){
      assertThat(e.getMessage()).contains("Invalid format");
    }
  }

}
