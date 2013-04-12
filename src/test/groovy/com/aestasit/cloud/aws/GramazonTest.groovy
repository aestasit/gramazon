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

  protected static String DEFAULT_AMI = ''
  protected static String DEFAULT_KEYPAIR = ''
  protected static String DEFAULT_SECURITY = ''
  protected static String DEFAULT_INSTANCETYPE = ''
  protected static String DEFAULT_EBSSIZE = -1
  protected static String DEFAULT_REGION = ''

  @BeforeClass
  public static void setUp() {
    AWS_ACCESS_KEY_ID = readProperty('awsAccessKeyId')
    AWS_SECRET_KEY = readProperty('awsSecretKey')
    DEFAULT_AMI = readProperty('awsDefaultAmi')
    DEFAULT_KEYPAIR = readProperty('awsDefaultKeypair')
    DEFAULT_SECURITY = readProperty('awsDefaultSecurity')
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