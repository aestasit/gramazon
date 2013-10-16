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

package com.aestasit.cloud.aws

import static org.junit.Assert.*
import groovy.time.TimeCategory

import org.junit.*

/**
 * Groovy API (EC2Client) integration test.
 *
 * @author Aestas/IT
 *
 */
class KeyPairTest extends GramazonTest {


  EC2Client ec2

  @Before
  public void prepare() {
    System.setProperty("aws.accessKeyId", AWS_ACCESS_KEY_ID)
    System.setProperty("aws.secretKey", AWS_SECRET_KEY)
    ec2 = new EC2Client(DEFAULT_REGION)
  }

  @Ignore
  void createKeypairWithName() {
    ec2.createKeyPair('test-groovy')
  }

  @Ignore
  void createKeypairWithRandomName() {
    ec2.createKeyPair()
  }

}