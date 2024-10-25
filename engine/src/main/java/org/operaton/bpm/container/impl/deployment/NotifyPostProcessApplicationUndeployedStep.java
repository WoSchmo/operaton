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
package org.operaton.bpm.container.impl.deployment;

import org.operaton.bpm.application.AbstractProcessApplication;
import org.operaton.bpm.container.impl.RuntimeContainerDelegateImpl;
import org.operaton.bpm.container.impl.jmx.services.JmxManagedBpmPlatformPlugins;
import org.operaton.bpm.container.impl.plugin.BpmPlatformPlugin;
import org.operaton.bpm.container.impl.spi.DeploymentOperation;
import org.operaton.bpm.container.impl.spi.DeploymentOperationStep;
import org.operaton.bpm.container.impl.spi.PlatformServiceContainer;
import org.operaton.bpm.container.impl.spi.ServiceTypes;

/**
 * @author Daniel Meyer
 *
 */
public class NotifyPostProcessApplicationUndeployedStep extends DeploymentOperationStep {

  @Override
  public String getName() {
    return "NotifyPostProcessApplicationUndeployedStep";
  }

  @Override
  public void performOperationStep(DeploymentOperation operationContext) {

    final AbstractProcessApplication processApplication = operationContext.getAttachment(Attachments.PROCESS_APPLICATION);

    final PlatformServiceContainer serviceContainer = operationContext.getServiceContainer();
    final JmxManagedBpmPlatformPlugins plugins =
        serviceContainer.getService(ServiceTypes.BPM_PLATFORM, RuntimeContainerDelegateImpl.SERVICE_NAME_PLATFORM_PLUGINS);

    if (plugins != null) {
      for (BpmPlatformPlugin  plugin : plugins.getValue().getPlugins()) {
        plugin.postProcessApplicationUndeploy(processApplication);
      }
    }
  }

}