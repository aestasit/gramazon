package com.aestasit.infrastructure.aws.model

import com.aestasit.infrastructure.aws.EC2Client;

import groovy.transform.Canonical

/**
 * Model object representing EC2 security group.
 *
 * @author Aestas/IT
 *
 */
@Canonical
class SecurityGroup {

  EC2Client ec2
  
  String name
  String id
  
}
