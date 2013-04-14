package com.aestasit.cloud.aws.gradle.tasks

import com.aestasit.cloud.aws.EC2Client
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
