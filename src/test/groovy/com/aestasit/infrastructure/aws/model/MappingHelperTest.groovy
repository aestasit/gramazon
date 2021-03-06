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

package com.aestasit.infrastructure.aws.model

import org.junit.Test

import static com.aestasit.infrastructure.aws.model.MappingHelper.map

/**
 * Data mapping test.
 * 
 * @author Aestas/IT
 *
 */
class MappingHelperTest {

  @Test 
  void testMapping() {    
    map(null, new com.amazonaws.services.ec2.model.Instance())
    map(null, new com.amazonaws.services.ec2.model.KeyPair())
  }
  
}
