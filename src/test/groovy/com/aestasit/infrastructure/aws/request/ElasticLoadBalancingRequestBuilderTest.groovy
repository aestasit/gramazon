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

package com.aestasit.infrastructure.aws.request

import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class ElasticLoadBalancingRequestBuilderTest {

	private static final String EXPECTED_NAME = 'myloadbalancer'
	
	def ElasticLoadBalancingRequestBuilder requestBuilder;
	
	@Before
	public void setUp() {
		requestBuilder = new ElasticLoadBalancingRequestBuilder();
	}
	
	@Test 
	void testBuildsRegisterInstancesRequestsWithProvidedParameters() {
	    requestBuilder.withLoadBalancerName(EXPECTED_NAME)
		def ids =  [ "a", "b" ] as String[]
		requestBuilder.withInstancesIds(ids)
		
		def registerInstancesRequest = requestBuilder.buildRegisterInstancesRequest()
		
		assertTrue(registerInstancesRequest instanceof RegisterInstancesWithLoadBalancerRequest)
		assertEquals(EXPECTED_NAME, registerInstancesRequest.loadBalancerName)
		assertTrue(registerInstancesRequest.getInstances().contains(new Instance("a")))
		assertTrue(registerInstancesRequest.getInstances().contains(new Instance("a")))
	}
	
	@Test 
	void testBuildsDeRegisterInstancesRequestsWithProvidedParameters() {
		requestBuilder.withLoadBalancerName(EXPECTED_NAME)
		def ids =  [ "a", "b" ] as String[]
		requestBuilder.withInstancesIds(ids)
		
		def deRegisterInstancesRequest = requestBuilder.buildDeregisterInstancesRequest()
		
		assertTrue(deRegisterInstancesRequest instanceof DeregisterInstancesFromLoadBalancerRequest)
		assertEquals(EXPECTED_NAME, deRegisterInstancesRequest.loadBalancerName)
		assertTrue(deRegisterInstancesRequest.getInstances().contains(new Instance("a")))
		assertTrue(deRegisterInstancesRequest.getInstances().contains(new Instance("a")))
	}
  
}
