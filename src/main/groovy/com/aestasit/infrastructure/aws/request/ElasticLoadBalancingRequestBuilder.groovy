package com.aestasit.infrastructure.aws.request

import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.VolumeType
import com.amazonaws.services.ec2.model.InstanceType
import com.amazonaws.services.ec2.model.EbsBlockDevice
import com.amazonaws.services.ec2.model.BlockDeviceMapping
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest


class ElasticLoadBalancingRequestBuilder {
	
	def String loadBalancerName
	def instances = new ArrayList<Instance>();

	ElasticLoadBalancingRequestBuilder withLoadBalancerName(String name){
		loadBalancerName = name
		return this
	}
	
	ElasticLoadBalancingRequestBuilder withInstancesIds(String[] intancesIds){
		for ( id in intancesIds ) { instances.add(new Instance(id)) }
		return this
	}
	
	DeregisterInstancesFromLoadBalancerRequest buildDeregisterInstancesRequest(){
		DeregisterInstancesFromLoadBalancerRequest deregisterRequest = new DeregisterInstancesFromLoadBalancerRequest();
		deregisterRequest.setLoadBalancerName(loadBalancerName)
		deregisterRequest.setInstances(instances);
		return deregisterRequest
	}
	
	RegisterInstancesWithLoadBalancerRequest buildRegisterInstancesRequest(){
		RegisterInstancesWithLoadBalancerRequest registerRequest = new RegisterInstancesWithLoadBalancerRequest();
		registerRequest.setLoadBalancerName(loadBalancerName)
		registerRequest.setInstances(instances);
		return registerRequest
	}
	
	  
}
