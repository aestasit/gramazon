/*
 * Copyright (C) 2011-2014 Aestas/IT
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

import com.aestasit.infrastructure.aws.gradle.InstanceState
import com.aestasit.infrastructure.aws.model.Instance
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import static com.aestasit.infrastructure.aws.gradle.InstanceStateUtils.setInstanceState

/**
 * Task that is capable of starting Amazon EC2 instance. 
 *
 * @author Aestas/IT
 *
 */
class StartInstance extends AbstractEc2Task {

  @Input
  String keyName

  @Input
  String ami

  @Input
  String securityGroup

  @Input
  String instanceType

  @Input
  String stateFileName

  @Input
  boolean waitForStart = true

  @Input
  @Optional
  int ebsSize = -1

  @Input
  @Optional
  int portToProbe = 22

  @Input
  @Optional
  String statePath = project.buildDir.path

  @Input
  @Optional
  String instanceName

  @Input
  @Optional
  String reuseInstanceId


  @TaskAction
  def start() {

    Instance ec2Instance
    boolean doStart = true
    if (reuseInstanceId) {
      if (client.getInstanceState(reuseInstanceId) == 'running') {
        ec2Instance = client.getInstance(reuseInstanceId)
        doStart = false
        project.logger.quiet("Reusing instance id: " + ec2Instance.instanceId + " - no instance will be started!")
      }
    }
    
    if (doStart) {
      
      // Start instance.
      ec2Instance = client.startInstance(
          keyName,
          ami,
          securityGroup,
          instanceType,
          waitForStart,
          portToProbe,
          ebsSize,
          instanceName)

      // Log success.
      if (waitForStart) {
        project.logger.quiet("Instance started. Instance id: " + ec2Instance.instanceId)
      } else {
        project.logger.quiet("Sent instance start request. Instance id: " + ec2Instance.instanceId)
      }
      
    }

    // Save state.
    project.file(statePath).mkdirs()
    setInstanceState(new File(statePath, stateFileName),
                     new InstanceState(ec2Instance.name, 
                                       ec2Instance.host, 
                                       ec2Instance.instanceId))

  }

}
