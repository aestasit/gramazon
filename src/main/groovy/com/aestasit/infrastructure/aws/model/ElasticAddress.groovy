package com.aestasit.infrastructure.aws.model

import com.aestasit.infrastructure.aws.EC2Client

import groovy.transform.Canonical

@Canonical
class ElasticAddress {

  EC2Client ec2
  
  String address
  
}
