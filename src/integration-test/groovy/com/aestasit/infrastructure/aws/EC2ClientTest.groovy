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

package com.aestasit.infrastructure.aws

import static org.junit.Assert.*
import groovy.time.TimeCategory

import org.junit.*

import com.aestasit.infrastructure.aws.EC2Client;
import com.aestasit.infrastructure.aws.model.Instance;

/**
 * Groovy API (EC2Client) integration test.
 *
 * @author Aestas/IT
 *
 */
class EC2ClientTest extends GramazonTest {

  EC2Client ec2

  @Before
  public void prepare() {
    System.setProperty("aws.accessKeyId", AWS_ACCESS_KEY_ID)
    System.setProperty("aws.secretKey", AWS_SECRET_KEY)
    ec2 = new EC2Client(DEFAULT_REGION)
  }

  @Test
  public void startInstanceWithNoName() throws Exception {
    terminateAfterTest { instance ->
      instance = ec2.startInstance(
          DEFAULT_KEY_NAME,
          DEFAULT_AMI_ID,
          DEFAULT_SECURITY_GROUP,
          DEFAULT_INSTANCETYPE,
          true)
      assertEquals("running", ec2.getInstanceState(instance.instanceId))
    }
  }

  @Test
  public void startInstanceWithName() throws Exception {
    terminateAfterTest { instance ->
      instance = ec2.startInstance(
          DEFAULT_KEY_NAME,
          DEFAULT_AMI_ID,
          DEFAULT_SECURITY_GROUP,
          DEFAULT_INSTANCETYPE,
          true,
          DEFAULT_EBSSIZE,
          "gramazon/integration/test")
      assertEquals("running", ec2.getInstanceState(instance.instanceId))
      assertEquals("gramazon/integration/test", ec2.getInstance(instance.instanceId).name)
    }
  }

  @Test
  @Ignore
  public void startInstanceWithEsbSize() throws Exception {
    terminateAfterTest { instance ->
      instance = ec2.startInstance(
          DEFAULT_KEY_NAME,
          DEFAULT_AMI_ID,
          DEFAULT_SECURITY_GROUP,
          DEFAULT_INSTANCETYPE,
          true,
          15,
          "gramazon/integration/test")
      assertEquals("running", ec2.getInstanceState(instance.instanceId))
      assertEquals("gramazon/integration/test", ec2.getInstance(instance.instanceId).name)
    }
  }

  @Test
  @Ignore
  public void startInstanceWithNameAndTags() throws Exception {
    // TODO: No way to test if an instance has tags (yet)
    terminateAfterTest { instance ->
      instance = ec2.startInstance(
          DEFAULT_KEY_NAME,
          DEFAULT_AMI_ID,
          DEFAULT_SECURITY_GROUP,
          DEFAULT_INSTANCETYPE,
          true,
          DEFAULT_EBSSIZE,
          "gramazon/integration/test",
          ["type": "groovy", "env": "prod"])
      assertEquals("running", ec2.getInstanceState(instance.instanceId))
      assertEquals("gramazon/integration/test", ec2.getInstance(instance.instanceId).name)
    }
  }

  @Test
  @Ignore
  public void getAllInstances() throws Exception {
    // TODO: Create an instance before and verify it is listed
    ec2.listAllInstances().each {
      println it.getClass().getName()
    }
  }

  @Test
  @Ignore
  public void getInstancesHavingName() throws Exception {
    // TODO: Create an instance before and verify it is listed
    use(TimeCategory) {
      ec2.listInstances("gramazon*").each {
        if (it.started < 2.hours.ago) {
          println it.name
        }
      }
    }
  }

  /*
   * UTILITIES
   */

  protected void terminateAfterTest(Closure cl) {
    Instance instance
    try {
      cl(instance)
    } finally {
      if (instance) {
        ec2.terminateInstances([instance.instanceId])
      }
    }
  }

}
