package com.aestasit.cloud.aws

import org.junit.BeforeClass
import org.apache.commons.io.FileUtils

class GramazonTest {

  protected static Properties props = new Properties()
  protected static DEFAULT_AMI = ''
  protected static DEFAULT_KEYPAIR = ''
  protected static DEFAULT_SECURITY = ''
  protected static DEFAULT_INSTANCETYPE = ''
  protected static DEFAULT_EBSSIZE =  -1

  @BeforeClass
  public static void setUp() {
    def f = FileUtils.toFile(this.getClass().getResource("/test.properties"))
    f.withInputStream {
     stream -> props.load(stream)
    }

    DEFAULT_AMI = props['aws.defaultAmi']
    DEFAULT_KEYPAIR = props['aws.defaultKeypair']
    DEFAULT_SECURITY = props['aws.defaultSecurity']
    DEFAULT_INSTANCETYPE = props['aws.defaultInstanceType']
    DEFAULT_EBSSIZE = props['aws.defaultEBSSize'].toInteger()

  }





}