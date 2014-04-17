package com.aestasit.infrastructure.aws.request

import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

import org.junit.Before;
import org.junit.Test

import static org.junit.Assert.*

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
