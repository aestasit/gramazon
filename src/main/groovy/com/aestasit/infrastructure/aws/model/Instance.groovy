/*
 * Copyright (C) 2011-2013 Aestas/IT
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

package com.aestasit.infrastructure.aws.model

import com.aestasit.infrastructure.aws.EC2Client;
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import groovy.transform.Canonical

/**
 * Model object representing EC2 instance.
 * 
 * @author Aestas/IT
 *
 */
@Canonical
class Instance {

  private EC2Client ec2
  
  String instanceId
  Date started
  String name
  String host
  String state
  // TODO add tags

  public boolean stop() {
    ec2.stopInstance(instanceId)
  }

  public boolean terminate() {
    ec2.terminateInstance(instanceId)
  } 

}