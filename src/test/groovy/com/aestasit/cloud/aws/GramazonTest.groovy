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

    AWS_ACCESS_KEY_ID = System.getProperty('awsAccessKeyId')
    AWS_SECRET_KEY = System.getProperty('awsSecretKey')

    DEFAULT_AMI = System.getProperty('awsDefaultAmi')
    DEFAULT_KEYPAIR = System.getProperty('awsDefaultKeypair')
    DEFAULT_SECURITY = System.getProperty('awsDefaultSecurity')
    DEFAULT_INSTANCETYPE = System.getProperty('awsDefaultInstanceType')
    DEFAULT_EBSSIZE = System.getProperty('awsDefaultEbsSize').toInteger()
    DEFAULT_REGION = System.getProperty('awsDefaultRegion')
  }
}