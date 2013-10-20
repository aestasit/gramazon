/*
 * Copyright (C) 2011-2013 Aestas/IT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aestasit.infrastructure.aws.gradle.tasks

import static com.aestasit.infrastructure.aws.gradle.InstanceStateUtils.getInstanceState

import com.aestasit.infrastructure.aws.model.Instance;

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction


/**
 * Task that is capable of terminating Amazon EC2 instance.
 * 
 * @author Aestas/IT
 *
 */
class TerminateInstance extends AbstractEc2Task {

  @Input
  @Optional
  String statePath = project.buildDir.path
  
  @Input
  @Optional
  String stateFileName

  @Input
  @Optional
  String instanceName = ''

  @Input
  @Optional
  String reuseInstanceId

  @Input
  @Optional
  Closure filter

  @TaskAction
  def terminate() {
    sanitize()
    if (stateFileName) {
      def state = getInstanceState(new File(statePath, stateFileName))
      if (state) {
        if (reuseInstanceId != state.instanceId) {
          client.terminateInstances([state.instanceId])
          project.logger.quiet("Sent instance terminate request. Instance id: " + state.instanceId)
        } else {
          project.logger.quiet("Instance id: " + state.instanceId + " will not be terminated, marked for reuse.")
        }
      }
    } else {
      List<Instance> instances = client.listInstances(instanceName)
      instances.each { Instance instance ->
        if ((filter == null) || (filter != null && filter(instance))) {
          instance.terminate()
          project.logger.quiet("Sent instance terminate request. Instance id: " + instance.instanceId)
        }
      }
    }
  }
  
  private void sanitize() {
    statePath = statePath ?: '.'
    if (stateFileName && (instanceName || filter)) {
      throw new GradleException("You can't specify stateFileName together with instanceName or filter at the same time!")
    }
    if (!stateFileName && !instanceName && !filter) {
      throw new GradleException("You must specify either stateFileName or instanceName or filter properties!")
    }
  }
  
}
