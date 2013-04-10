package com.aestasit.cloud.aws.gradle

import groovy.transform.Canonical

/**
 * Data object representing Amazon EC2 instance state.
 *
 * @author Aestas/IT
 *
 */
@Canonical
class InstanceState {

  String name
  String host
  String instanceId

}
