package com.aestasit.cloud.aws

import com.aestasit.cloud.aws.gradle.tasks.CreateImage
import com.aestasit.cloud.aws.gradle.tasks.StartInstance
import com.aestasit.cloud.aws.gradle.tasks.TerminateInstance
import groovy.time.TimeCategory
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class TestGramazonPlugin extends GramazonTest {

  Project project

  @Before
  def void buildProject() {
    project = ProjectBuilder.builder().build()
    project.logging.level = LogLevel.INFO
    project.with {

      apply plugin: 'gramazon'

      aws {
        acceesKeyId = props['aws.accessKeyId']
        secretKey = props['aws.secretKey']
        region = props['aws.defaultRegion']
      }

      task('start', type: StartInstance) {
        keyName DEFAULT_KEYPAIR
        ami DEFAULT_AMI
        securityGroup DEFAULT_SECURITY
        instanceType DEFAULT_INSTANCETYPE
        instanceName 'gramazon gradle'
        stateFileName 'mediabox.json'
        ebsSize = 20
      }

      task('reuseMediaLib', type: StartInstance) {
        keyName DEFAULT_KEYPAIR
        ami DEFAULT_AMI
        securityGroup DEFAULT_SECURITY
        instanceType DEFAULT_INSTANCETYPE
        instanceName 'gramazon gradle'
        stateFileName 'mediabox.json'
        reuseInstanceId 'i-xxxxx'
      }

      task('terminateReuse', type: TerminateInstance) {
        stateFileName 'mediabox.json'
        reuseInstanceId 'i-xxxxx'
      }

      task('terminate', type: TerminateInstance) { stateFileName 'mediabox.json' }

      task('terminateAll', type: TerminateInstance) {
        instanceName 'gramazon gradle*'
        filter { instance ->
          use(TimeCategory) {
            instance.started < 2.milliseconds.ago
          }
        }
      }

      task('createImage', type: CreateImage) {
        instanceId 'i-xxxxxx'
        amiName "my ami for test"
        amiDescription 'test ami'
        stopBeforeCreation true
      }

    }
  }

  @Test
  def void testStartTerminate() throws Exception {
    project.tasks.'start'.execute()
    project.tasks.'terminate'.execute()
    project.tasks.'terminateAll'.execute()
  }

  @Test
  @Ignore
  def void reuseMediaLibrary() throws Exception {
    project.tasks.'reuseMediaLib'.execute()
    project.tasks.'terminateReuse'.execute()
  }

  @Test
  @Ignore
  // This test is ignored because currently there is no way to remove an AMI
  def void createImage() throws Exception {
    project.tasks.'createImage'.execute()
  }

}
