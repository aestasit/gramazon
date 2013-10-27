package com.aestasit.infrastructure.aws.model

import com.aestasit.infrastructure.aws.EC2Client;

import groovy.transform.Canonical

/**
 * Model object representing EC2 AMI.
 *
 * @author Aestas/IT
 *
 */
@Canonical
class Image {

  EC2Client ec2
  
  String name
  String id
  
}
