package com.aestasit.cloud.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import groovy.transform.Canonical

@Canonical
class Instance {

  AmazonEC2 ec2
  String instanceId
  Date started
  String name
  String host
  String state
  // TODO add tags

  public int terminate() {
    return ec2.terminateInstances(new TerminateInstancesRequest([this.instanceId])).terminatingInstances.size()
  } 

}