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

package com.aestasit.infrastructure.aws.gradle

import static com.aestasit.infrastructure.aws.gradle.InstanceStateUtils.getInstanceState

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
