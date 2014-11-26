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

package com.aestasit.infrastructure.aws

import com.aestasit.infrastructure.aws.gradle.tasks.CreateImage
import com.aestasit.infrastructure.aws.gradle.tasks.StartInstance
import com.aestasit.infrastructure.aws.gradle.tasks.TerminateInstance
import groovy.time.TimeCategory
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Integration test for Gradle API (StartInstance, TerminateInstance, CreateImage etc.) of Gramazon.
 *
 * @author Aestas/IT
 *
 */
class GramazonPluginTest extends GramazonTest {

  Project project

  @Before
  def void buildProject() {
    project = ProjectBuilder.builder().build()
    project.logging.level = LogLevel.INFO
    project.with {

      apply plugin: 'gramazon'

      aws {
        acceesKeyId = AWS_ACCESS_KEY_ID
        secretKey = AWS_SECRET_KEY
        region = DEFAULT_REGION
      }

      task('start', type: StartInstance) {
        keyName DEFAULT_KEY_NAME
        ami DEFAULT_AMI_ID
        securityGroup DEFAULT_SECURITY_GROUP
        instanceType DEFAULT_INSTANCETYPE
        instanceName 'gramazon gradle'
        stateFileName 'mediabox.json'
        ebsSize = 20
      }

      task('reuseMediaLib', type: StartInstance) {
        keyName DEFAULT_KEY_NAME
        ami DEFAULT_AMI_ID
        securityGroup DEFAULT_SECURITY_GROUP
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
    // TODO: Start instance to reuse before the test, get its instance id and then verify that it is actually reused
    project.tasks.'reuseMediaLib'.execute()
    project.tasks.'terminateReuse'.execute()
  }

  @Test
  @Ignore
  def void createImage() throws Exception {
    // TODO: This test is ignored because currently there is no way to remove an AMI
    project.tasks.'createImage'.execute()
  }
}
