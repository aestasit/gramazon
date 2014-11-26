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

package com.aestasit.infrastructure.aws.gradle.tasks

import com.aestasit.infrastructure.aws.EC2Client
import org.gradle.api.GradleException
import org.gradle.api.internal.ConventionTask

/**
 * Base class for various Gradle task types that work with Amazon EC2.
 * 
 * @author Aestas/IT
 *
 */
class AbstractEc2Task extends ConventionTask {

  protected void setSystemProperties() {
    if (project.hasProperty("aws")) {

      if (project.aws?.acceesKeyId == null) {
        throw new GradleException("AWS accessKeyId has to be specified.")
      }
      if (project.aws?.secretKey == null) {
        throw new GradleException("AWS secretKey has to be specified.")
      }

      System.setProperty("aws.accessKeyId", project.aws.acceesKeyId)
      System.setProperty("aws.secretKey", project.aws.secretKey)
      
    } else {
      throw new GradleException("Gramazon plugin needs to be applied before you can use " + getClass().getSimpleName() + " task type")
    }
  }
  
  protected EC2Client getClient() {
    setSystemProperties()
    return new EC2Client(project.aws.region)
  }
    
}
