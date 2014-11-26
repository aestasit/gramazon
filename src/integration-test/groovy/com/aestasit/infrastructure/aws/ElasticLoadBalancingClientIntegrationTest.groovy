/*
 * Copyright (C) 2011-2014 Aestas/IT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aestasit.infrastructure.aws

import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

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
