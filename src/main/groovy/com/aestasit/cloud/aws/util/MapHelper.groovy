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

package com.aestasit.cloud.aws.util

import com.aestasit.cloud.aws.Instance

public class MapHelper {

  public static Instance map(ec2, com.amazonaws.services.ec2.model.Instance instance) {
    def tName = instance.tags.find { it.key == "Name" }
    new Instance(ec2,
                    instance.instanceId,
                    instance.launchTime,
                    (tName!=null ? tName.value : ""),
                    instance.publicDnsName,
                    instance.state.name)
  }
  
}
