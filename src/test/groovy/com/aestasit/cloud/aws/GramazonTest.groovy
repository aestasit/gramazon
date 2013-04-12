package com.aestasit.cloud.aws

import org.junit.BeforeClass

/**
 * Base Gramazon integration test class.
 *
 * @author Aestas/IT
 *
 */
class GramazonTest {

  protected static String AWS_ACCESS_KEY_ID = ''
  protected static String AWS_SECRET_KEY = ''

  protected static String DEFAULT_REGION = ''
  protected static String DEFAULT_AMI_ID = ''
  protected static String DEFAULT_KEY_NAME = ''
  protected static String DEFAULT_SECURITY_GROUP = ''
  protected static String DEFAULT_INSTANCETYPE = ''
  protected static int DEFAULT_EBSSIZE = -1

  @BeforeClass
  public static void setUp() {
    AWS_ACCESS_KEY_ID = readProperty('awsAccessKeyId')
    AWS_SECRET_KEY = readProperty('awsSecretKey')
    DEFAULT_AMI_ID = readProperty('awsDefaultAmiId')
    DEFAULT_KEY_NAME = readProperty('awsDefaultKeyName')
    DEFAULT_SECURITY_GROUP = readProperty('awsDefaultSecurityGroup')
    DEFAULT_INSTANCETYPE = readProperty('awsDefaultInstanceType', 't1.micro')
    DEFAULT_EBSSIZE = readProperty('awsDefaultEbsSize', '-1').toInteger()
    DEFAULT_REGION = readProperty('awsDefaultRegion', 'eu-west-1')
  }

  private static String readProperty(String key) {
    String value = System.getProperty(key)
    if (!value) {
      throw new RuntimeException("Missing property: '$key'. Please, pass it through system properties!")
    }
    return value
  }

  private static String readProperty(String key, String defaultValue) {
    String value = System.getProperty(key)
    if (!value) {
      value = defaultValue
    }
    return value
  }
}