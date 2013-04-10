package com.aestasit.cloud.aws.util

import com.amazonaws.services.ec2.model.InstanceType
import com.aestasit.cloud.aws.*

public class MapHelper {

  public static Instance map(ec2, com.amazonaws.services.ec2.model.Instance instance) {
    def tName = instance.tags.find { it.key == "Name" }
    return new Instance(ec2,
                        instance.instanceId, 
                        instance.launchTime,
                        (tName!=null ? tName.value : ""), 
                        instance.publicDnsName,
                        instance.state.name)
  }
  
}
