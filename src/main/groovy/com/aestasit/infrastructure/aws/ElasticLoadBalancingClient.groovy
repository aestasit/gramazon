package com.aestasit.infrastructure.aws

import com.aestasit.infrastructure.aws.request.ElasticLoadBalancingRequestBuilder
import com.amazonaws.auth.SystemPropertiesCredentialsProvider 
import com.amazonaws.regions.Region
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest

class ElasticLoadBalancingClient {
	
	final AmazonElasticLoadBalancingClient loadBalancingClient
	final String loadBalancerName
	
	ElasticLoadBalancingClient(String loadBalancerName, String region = 'eu-west-1',
		AmazonElasticLoadBalancingClient amazonLoadBalancingClient = new AmazonElasticLoadBalancingClient(new SystemPropertiesCredentialsProvider())) {
		this.loadBalancingClient = amazonLoadBalancingClient
		this.loadBalancerName = loadBalancerName
		
		Region loadBalancerRegion = new Region(region)
		loadBalancerRegion.serviceEndpoints.put('elasticloadbalancing', "elasticloadbalancing."+region+".amazonaws.com")
		this.loadBalancingClient.setRegion(loadBalancerRegion)
	}
	
	RegisterInstancesWithLoadBalancerResult addInstancesToLoadBalancer(String[] intancesIds, ElasticLoadBalancingRequestBuilder requestBuilder = new ElasticLoadBalancingRequestBuilder()) {
		def registerRequest = requestBuilder.withLoadBalancerName(loadBalancerName).withInstancesIds(intancesIds).buildRegisterInstancesRequest()
		loadBalancingClient.registerInstancesWithLoadBalancer(registerRequest)
	}
	
	DeregisterInstancesFromLoadBalancerResult removeInstancesFromLoadBalancer(String[] intancesIds, ElasticLoadBalancingRequestBuilder requestBuilder = new ElasticLoadBalancingRequestBuilder()) {
		def deregisterRequest = requestBuilder.withLoadBalancerName(loadBalancerName).withInstancesIds(intancesIds).buildDeregisterInstancesRequest();
		loadBalancingClient.deregisterInstancesFromLoadBalancer(deregisterRequest)
	}
	
}