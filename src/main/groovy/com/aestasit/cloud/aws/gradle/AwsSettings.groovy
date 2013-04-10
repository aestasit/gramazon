package com.aestasit.cloud.aws.gradle

import groovy.transform.Canonical

/**
 * Common Gramazon/EC2 settings data object. 
 * 
 * @author Aestas/IT
 *
 */
@Canonical
class AwsSettings {
  
  String acceesKeyId
  String secretKey
  String region = "eu-west-1"

}
