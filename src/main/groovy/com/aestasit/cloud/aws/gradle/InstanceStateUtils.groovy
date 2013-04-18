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
