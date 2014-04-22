package com.aestasit.infrastructure.aws

import static org.junit.Assert.*  
import groovy.time.TimeCategory

import org.junit.*

import com.aestasit.infrastructure.aws.EC2Client
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult
import com.amazonaws.internal.ListWithAutoConstructFlag
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException

class ElasticLoadBalancingClientIntegrationTest {

  ElasticLoadBalancingClient loadBalancingClient
  private static final String INSTANCE_ID   //Assign your instance id
  private static final String LOAD_BALANCER_NAME //Assign your instance id 
  Instance instance = new Instance(INSTANCE_ID) 

  @Before
  public void prepare() {
    System.setProperty("aws.accessKeyId", AWS_ACCESS_KEY_ID)
    System.setProperty("aws.secretKey", AWS_SECRET_KEY)
	loadBalancingClient = new ElasticLoadBalancingClient(LOAD_BALANCER_NAME)
  }
  
  @Test
  @Ignore
  public void removeInstanceFromLoadBalancer() throws Exception {
	  def ids = [INSTANCE_ID] as String[]
	  DeregisterInstancesFromLoadBalancerResult removeInstancesResult = loadBalancingClient.removeInstancesFromLoadBalancer(ids)
	  assertFalse(removeInstancesResult.getInstances().contains(instance))
  }
  
  @Test
  @Ignore
  public void addInstanceToLoadBalancer() throws Exception {
	  def ids =  [INSTANCE_ID] as String[]
	  RegisterInstancesWithLoadBalancerResult registerInstancesResult = loadBalancingClient.addInstancesToLoadBalancer(ids)
	  assertTrue(registerInstancesResult.getInstances().contains(instance))
  }

}
