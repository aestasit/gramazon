package com.aestasit.cloud.aws.gradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import static com.aestasit.cloud.aws.gradle.InstanceStateUtils.getInstanceState

/**
 * Task that is capable of creatim Amazon AMI from EC2 instance.
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
