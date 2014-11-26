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

import static com.aestasit.infrastructure.aws.gradle.InstanceStateUtils.getInstanceState

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction


/**
 * Task that is capable of creating Amazon AMI from EC2 instance.
 *
 * @author Aestas/IT
 *
 */
class CreateImage extends AbstractEc2Task {

    @Input
    String amiName

    @Input
    String amiDescription

    @Input
    @Optional
    String instanceId

    @Input
    @Optional
    String stateFileName

    @Input
    @Optional
    String statePath = project.buildDir.path

    @Input
    @Optional
    boolean stopBeforeCreation = false

    @TaskAction
    def createImage() {
        sanitize()
        if (stateFileName) {
            def state = getInstanceState(new File(statePath, stateFileName))
            instanceId = state.instanceId
        }
        def amiId = client.createImage(instanceId, amiName, amiDescription, stopBeforeCreation)
        project.logger.quiet("Sent image creation request. AMI ID: ${amiId}, instance id: ${instanceId}")
    }

    private void sanitize() {

        statePath = statePath ?: '.'
        if (stateFileName && instanceId) {
            throw new GradleException("You can't specify stateFileName together with instanceId at the same time!")
        }
        if (!stateFileName && !instanceId) {
            throw new GradleException("You must specify either stateFileName or instanceId properties!")
        }
    }

}
