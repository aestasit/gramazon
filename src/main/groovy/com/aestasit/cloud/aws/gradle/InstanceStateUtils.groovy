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

package com.aestasit.cloud.aws.gradle

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

/**
 * Utility methods for reading/writing Amazon EC2 instance state data.
 *
 * @author Aestas/IT
 *
 */
class InstanceStateUtils {

  /**
   * Deserialize the InstanceState pojo from JSON.
   *
   * @param f the file where the JSON file is.
   * @return a deserialized InstanceState.
   */
  static InstanceState getInstanceState(File f) {
    def s = new JsonSlurper().parseText(f.text)
    return new InstanceState(s.instanceState.name,
    s.instanceState.host,
    s.instanceState.instanceId)
  }

  /**
   * Save the InstanceState POJO to a JSON file.
   *
   * @param f the file where to save serialize the POJO.
   * @param state the POJO to serialize.
   */
  static void setInstanceState(File f, InstanceState state) {
    JsonBuilder builder = new JsonBuilder()
    builder.instanceState(
        name: state.name,
        host: state.host,
        instanceId: state.instanceId
        )
    f.withWriter { it << builder.toPrettyString() }
  }
}
