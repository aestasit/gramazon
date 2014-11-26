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

import com.aestasit.infrastructure.aws.request.ElasticLoadBalancingRequestBuilder
import com.amazonaws.auth.SystemPropertiesCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult

class ElasticLoadBalancingClient {
	
	final AmazonElasticLoadBalancingClient loadBalancingClient
	final String loadBalancerName
	
	ElasticLoadBalancingClient(String loadBalancerName, String region = 'eu-west-1',
		AmazonElasticLoadBalancingClient amazonLoadBalancingClient = new AmazonElasticLoadBalancingClient(new SystemPropertiesCredentialsProvider())) {
		this.loadBalancingClient = amazonLoadBalancingClient
		this.loadBalancerName = loadBalancerName
		
		Region loadBalancerRegion = new Region(region, null)
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