package com.aestasit.cloud.aws.gradle

import static com.aestasit.cloud.aws.gradle.InstanceStateUtils.*

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin that adds common setup shared by Gramazon task types (StartInstance, TerminateInstance etc.). 
 * 
 * @author Aestas/IT
 *
 */
class GramazonPlugin implements Plugin<Project> {

  def void apply(Project project) {
    project.extensions.create("aws", AwsSettings)
    project.metaClass.with {
      getInstanceState << { String fileName ->
        return getInstanceState(new File(project.buildDir.path, fileName))
      }
      getInstanceState << { String path, String fileName ->
        return getInstanceState(new File(path, fileName))
      }
    }
  }
  
}
