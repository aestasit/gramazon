package com.aestasit.cloud.aws

import groovy.time.TimeCategory
import org.junit.*
import org.junit.Assert

class TestEC2Client extends GramazonTest {

  EC2Client ec2


  @Before
  public void setUp() {
    System.setProperty("aws.accessKeyId", props['aws.accessKeyId'])
    System.setProperty("aws.secretKey", props['aws.secretKey'])

    ec2 = new EC2Client(props['aws.defaultRegion'])
  }

  @Test
  public void startInstanceWithNoName() throws Exception {
    def instance
    try {
      instance = ec2.startInstance(DEFAULT_KEYPAIR, DEFAULT_AMI, DEFAULT_SECURITY, DEFAULT_INSTANCETYPE, true)
      Assert.assertEquals("running", ec2.getInstanceState(instance.instanceId))
    } finally {
      ec2.terminateInstances([instance.instanceId])
    }
  }

  @Test
  public void startInstanceWithName() throws Exception {
    Instance instance
    try {
      if (ec2==null)fail("ec2 is null")
      instance = ec2.startInstance(DEFAULT_KEYPAIR, DEFAULT_AMI, DEFAULT_SECURITY, DEFAULT_INSTANCETYPE, true, DEFAULT_EBSSIZE, "gramazon/integration/test")
      Assert.assertEquals("gramazon/integration/test", ec2.getInstance(instance.instanceId).name)
    } finally {
      ec2.terminateInstances([instance.instanceId])
    }
  }

  // No way to test if an instance has tags (yet)
  public void startInstanceWithNameAndTags() throws Exception {
    ec2.startInstance("aestas-ci", "ami-2a98905e", "aestas-default", "t1.micro", "BELIN!", ["type": "groovy", "env": "prod"])
    Instance instance
    try {
      instance = ec2.startInstance(DEFAULT_KEYPAIR, DEFAULT_AMI, DEFAULT_SECURITY, DEFAULT_INSTANCETYPE, true, DEFAULT_EBSSIZE, "gramazon/integration/test", ["type": "groovy", "env": "prod"])
      Assert.assertEquals("gramazon/integration/test", ec2.getInstance(instance.instanceId).name)
    } finally {
      ec2.terminateInstances([instance.instanceId])
    }
  }

  public void getAllInstances() throws Exception {
    ec2.listAllInstances().each {
      println it.getClass().getName()
    }
  }

  public void getInstancesHavingName() throws Exception {
    use(TimeCategory) {
      ec2.listInstances("jenkins*").each {
        if (it.started < 2.hours.ago) {
          println it.name
        }
      }
    }
  }

  public void startInstanceWithEsbSize() throws Exception {
    ec2.startInstance(DEFAULT_KEYPAIR, DEFAULT_AMI, DEFAULT_SECURITY, DEFAULT_INSTANCETYPE, true, 15, "gramazon/integration/test")
  }

  //public void createImage() throws Exception {
  //  ec2.createImage("i-xxxxxxx", "test image", "test image", true)
  //}

  //public void stopInstance() throws Exception {
  //  ec2.stopInstance("i-xxxxx")
  //}

}
