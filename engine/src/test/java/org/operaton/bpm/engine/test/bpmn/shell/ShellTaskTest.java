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
package org.operaton.bpm.engine.test.bpmn.shell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotSame;

import org.operaton.bpm.engine.runtime.ProcessInstance;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.test.util.PluggableProcessEngineTest;
import org.junit.Before;
import org.junit.Test;

public class ShellTaskTest extends PluggableProcessEngineTest {

  enum OsType {
    LINUX, WINDOWS, MAC, SOLARIS, UNKNOWN
  }

  OsType osType;

  OsType getSystemOsType() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win"))
      return OsType.WINDOWS;
    else if (osName.contains("mac"))
      return OsType.MAC;
    else if ((osName.contains("nix")) || (osName.contains("nux")))
      return OsType.LINUX;
    else if (osName.contains("sunos"))
      return OsType.SOLARIS;
    else
      return OsType.UNKNOWN;
  }

  @Before
  public void setUp() {
    osType = getSystemOsType();
  }

  @Test
  public void testOsDetection() {
    assertNotSame(OsType.UNKNOWN, osType);
  }

  @Deployment
  @Test
  public void testEchoShellWindows() {
    if (osType == OsType.WINDOWS) {

      ProcessInstance pi = runtimeService.startProcessInstanceByKey("echoShellWindows");
  
      String st = (String) runtimeService.getVariable(pi.getId(), "resultVar");
      assertThat(st).isNotNull();
      assertThat(st).startsWith("EchoTest");
    }
  }

  @Deployment
  @Test
  public void testEchoShellLinux() {
    if (osType == OsType.LINUX) {

      ProcessInstance pi = runtimeService.startProcessInstanceByKey("echoShellLinux");
  
      String st = (String) runtimeService.getVariable(pi.getId(), "resultVar");
      assertThat(st).isNotNull();
      assertThat(st).startsWith("EchoTest");
    }
  }
  
  @Deployment
  @Test
  public void testEchoShellMac() {
    if (osType == OsType.MAC) {

      ProcessInstance pi = runtimeService.startProcessInstanceByKey("echoShellMac");
  
      String st = (String) runtimeService.getVariable(pi.getId(), "resultVar");
      assertThat(st).isNotNull();
      assertThat(st).startsWith("EchoTest");
    }
  }
}
