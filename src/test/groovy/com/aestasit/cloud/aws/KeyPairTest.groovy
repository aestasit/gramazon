package com.aestasit.cloud.aws

import static org.junit.Assert.*
import groovy.time.TimeCategory

import org.junit.*

/**
 * Groovy API (EC2Client) integration test.
 *
 * @author Aestas/IT
 *
 */
class KeyPairTest extends GramazonTest {


  EC2Client ec2

  @Before
  public void prepare() {
    System.setProperty("aws.accessKeyId", AWS_ACCESS_KEY_ID)
    System.setProperty("aws.secretKey", AWS_SECRET_KEY)
    ec2 = new EC2Client(DEFAULT_REGION)
  }

  @Test
  void createKeypairWithName() {
    ec2.createKeyPair('test-groovy')
  }

  @Test
  void createKeypairWithRandomName() {
    ec2.createKeyPair()
  }

}