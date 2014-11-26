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

import groovy.transform.Canonical

/**
 * Common Gramazon/EC2 settings data object. 
 * 
 * @author Aestas/IT
 *
 */
@Canonical
class AwsSettings {
  
  String acceesKeyId
  String secretKey
  String region = "eu-west-1"

}
