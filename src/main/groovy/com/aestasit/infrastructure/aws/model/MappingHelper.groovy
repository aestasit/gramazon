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

package com.aestasit.infrastructure.aws.model

import com.aestasit.infrastructure.aws.EC2Client
import com.amazonaws.services.ec2.model.Tag

public class MappingHelper {

  static Instance map(EC2Client ec2client, com.amazonaws.services.ec2.model.Instance instance) {
    Tag tName = instance.tags.find { it.key == "Name" }
    Instance mapped = new Instance()
    mapped.with {
      ec2 = ec2client
      started = instance.launchTime ?: new Date()
      instanceId = instance.instanceId ?: ""
      name = tName?.value ?: ""
      host = instance.publicDnsName ?: ""
      state = instance.state?.name ?: "UNKNOWN"    
    }
    mapped
  }

  static KeyPair map(EC2Client ec2client, com.amazonaws.services.ec2.model.KeyPair keyPair) {
    KeyPair mapped = new KeyPair()
    mapped.with {
      ec2 = ec2client
      name = keyPair.keyName
      fingerprint = keyPair.keyFingerprint
      material = keyPair.keyMaterial
    }
    mapped
  }

}
