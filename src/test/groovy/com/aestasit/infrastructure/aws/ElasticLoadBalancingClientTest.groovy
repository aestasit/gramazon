package com.aestasit.infrastructure.aws

import com.aestasit.infrastructure.aws.request.ElasticLoadBalancingRequestBuilder
import com.amazonaws.regions.Region
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

@RunWith(MockitoJUnitRunner.class)
class ElasticLoadBalancingClientTest {

  def elasticLoadBalancing
  @Mock AmazonElasticLoadBalancingClient loadBalancingClient
  @Mock ElasticLoadBalancingRequestBuilder requestBuilder
  @Mock RegisterInstancesWithLoadBalancerRequest registerRequest
  @Mock RegisterInstancesWithLoadBalancerResult registerResponse
  @Mock DeregisterInstancesFromLoadBalancerRequest deregisterRequest
  @Mock DeregisterInstancesFromLoadBalancerResult deregisterResponse
  def ids = ["a", "b"] as String[]
  def loadBalancingName = "elasticloadbalancing"

  @Before
  public void setUp(){
	  initMocks(this);
	  elasticLoadBalancing = new ElasticLoadBalancingClient("loadbalancer_name", "region_europe", loadBalancingClient);
	  when(requestBuilder.withLoadBalancerName(anyString())).thenReturn(requestBuilder)
	  when(requestBuilder.withInstancesIds(ids)).thenReturn(requestBuilder)
	  when(requestBuilder.buildRegisterInstancesRequest()).thenReturn(registerRequest)
	  when(requestBuilder.buildDeregisterInstancesRequest()).thenReturn(deregisterRequest)
	  when(loadBalancingClient.registerInstancesWithLoadBalancer(registerRequest)).thenReturn(registerResponse)
	  when(loadBalancingClient.deregisterInstancesFromLoadBalancer(deregisterRequest)).thenReturn(deregisterResponse)
  }

  @Test
  void testConstructorSetsCorrectRegionForLoadBalancerOnClient() {
	  ArgumentCaptor<Region> argument = ArgumentCaptor.forClass(Region.class)

	  verify(loadBalancingClient).setRegion(argument.capture())

	  Map<String, String> serviceEndpoints = argument.getValue().getServiceEndpoints()

	  assertTrue(serviceEndpoints.containsKey("elasticloadbalancing"))
	  assertEquals("elasticloadbalancing.region_europe.amazonaws.com", serviceEndpoints.get("elasticloadbalancing"))
  }

  @Test
  void testAddInstancesToLoadBalancer_ShouldInvokeAmazonsAddInstanceRequest_AndReturnAResult() {
	  def reponse = elasticLoadBalancing.addInstancesToLoadBalancer(ids, requestBuilder)
	  verify(loadBalancingClient).registerInstancesWithLoadBalancer(registerRequest)
	  assertTrue(reponse instanceof RegisterInstancesWithLoadBalancerResult)
  }

  @Test
  void testRemoveInstancesFromLoadBalancer_ShouldInvokeAmazonsRemoveInstanceRequest_AndReturnAResult() {
	  def reponse = elasticLoadBalancing.removeInstancesFromLoadBalancer(ids, requestBuilder)
	  verify(loadBalancingClient).deregisterInstancesFromLoadBalancer(deregisterRequest)
	  assertTrue(reponse instanceof DeregisterInstancesFromLoadBalancerResult)
  }

}
