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

import com.aestasit.infrastructure.aws.model.Image
import com.aestasit.infrastructure.aws.model.Instance
import com.aestasit.infrastructure.aws.model.KeyPair
import com.amazonaws.auth.SystemPropertiesCredentialsProvider
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.*
import groovy.time.TimeCategory

import static com.aestasit.infrastructure.aws.model.MappingHelper.map
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

/**
 *
 * Client class to interact with Amazon EC2 instances.
 * The only constructor allows to set the EC2 region (e.g. eu-west-1).
 *
 * The class provides credentials by looking at the <code>aws.accessKeyId</code>
 * and <code>aws.secretKey</code> system properties.
 *
 * @author Aestas/IT
 *
 */
class EC2Client {

  private final static String RUNNING_STATE = "running"

  static final int EC2_API_REQUEST_DELAY = 5000
  static final int CONNECTION_RETRY_DELAY = 5000
  static final int DEFAULT_RETRY_COUNT = 50

  private final AmazonEC2 ec2

  EC2Client(String region = 'eu-west-1') {
    ec2 = new AmazonEC2Client(new SystemPropertiesCredentialsProvider())
    ec2.endpoint = "ec2." + region + ".amazonaws.com"
  }

  /**
   * Starts EC2 instance.
   *
   * @param keyName the security key name.
   * @param ami the image id.
   * @param securityGroup the security group.
   * @param instanceType the instance type.
   * @param waitForStart if true then method is waiting for the instance to become available.
   * @return created instance model object.
   */
  Instance startInstance(
      String keyName,
      String ami,
      String securityGroup,
      String instanceType,
      boolean waitForStart,
      int portToProbe = 22,
      int esbVolumeSize = -1,
      String instanceName = null,
      Map<String, String> additionalTags = [:]) {

    def req = new RunInstancesRequest() 
    req.keyName = keyName
    req.imageId = ami
    req.securityGroups = [ securityGroup ]
    req.instanceType = InstanceType.fromValue(instanceType)
    req.minCount = 1
    req.maxCount = 1
    if (esbVolumeSize != -1) {
      req.setBlockDeviceMappings([
        getEsbInfo(esbVolumeSize, false)
      ])
    }

    def result = ec2.runInstances(req)        
    
    // Sleep for a while, to give time to the instance to properly initialize.
    sleep(EC2_API_REQUEST_DELAY)
    
    // Set instance name.
    def instanceId = result.reservation.instances[0].instanceId
    addTagsToResource(instanceId) {
      instanceName ? additionalTags << ["Name": instanceName] : additionalTags
    }

    // Sleep for a while, to give time to the instance to properly initialize.
    sleep(EC2_API_REQUEST_DELAY)

    // Wait for instance to have 'running' state.
    repeat("> Creating instance...", EC2_API_REQUEST_DELAY, DEFAULT_RETRY_COUNT) {
      try {
        return getInstanceState(instanceId) == RUNNING_STATE
      } catch (Exception e) {
        println "recieved: " + e.message
        return false
      }
    }

    // Sleep for a while, to give time to the instance to properly initialize.
    sleep(EC2_API_REQUEST_DELAY)

    // Get fresh instance information.
    Instance instance = getInstance(instanceId)

    if (!instance) {
      throw new RuntimeException("Failed to parse instance information for instance id: $instanceId!")
    }

    if (waitForStart) {

      if (!instance?.host) {
        throw new RuntimeException("Instance host cannot be retrieved!")
      }

      // Now try to connect to the instance on the specified port.
      repeat("> Testing connection on port ${portToProbe}...", CONNECTION_RETRY_DELAY, DEFAULT_RETRY_COUNT) {
        available(instance.host, portToProbe)
      }
      sleep(EC2_API_REQUEST_DELAY)

    }

    instance

  }

  /**
   * Get an instance state status.
   *
   * @param instanceId the instance id to search for.
   * @return state string.
   */
  String getInstanceState(String instanceId) {
    def response = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds([instanceId]))
    response.getReservations()[0].getInstances()[0].state.name
  }

  /**
   * Get instance data by instance id.
   *
   * @param instanceId the instance id to search for.
   * @return instance data structure.
   */
  Instance getInstance(String instanceId) {
    List<Instance> instances = listInstancesWithRequest(new DescribeInstancesRequest().withInstanceIds(instanceId))
    instances.size() == 1 ? instances[0] : null
  }

  /**
   * List all instances with a given name pattern and tags.
   *
   * An example:
   * <pre>
   * {@code
   * ec2.listInstances("gramazon*").each {
   * ...
   *  }
   *}
   * </pre>
   * 
   * @param instanceName the instance name pattern (supports wild cards).
   * @param tagFilter the map of additional tag values.
   * @return collection of instance data.
   */
  List<Instance> listInstances(String instanceName, Map<String, String> tagFilter = [:]) {
    listInstancesWithRequest(
      new DescribeInstancesRequest()
        .withFilters(tagFilter.collect { k, v -> new Filter("tag:" + k, [v]) } << new Filter("tag:Name", [instanceName]))
    )
  }

  /**
   * List all available EC2 instances.
   *
   * @return collection of instance data.
   */
  List<Instance> listAllInstances() {
    listInstancesWithRequest(new DescribeInstancesRequest())
  }  
  
  /**
   * Stop an EC2 instance.
   *
   * @param instanceId the instance id to stop.
   * @return true if instance stopping was successfully started, false otherwise.
   */
  boolean stopInstance(String instanceId) {
    stopInstances([instanceId]) == 1
  }

  /**
   * Stop one or more EC2 instances.
   *  
   * @param instanceIds the list of instance id to stop.
   * @return number of instances which stopping has successfully started.
   */
  int stopInstances(List<String> instanceIds) {
    def response = ec2.stopInstances(new StopInstancesRequest().withInstanceIds(instanceIds))
    response.stoppingInstances.size()
  }
  
  /**
   * Terminate an EC2 instance.
   *
   * @param instanceId the instance id to terminate.
   * @return true if instance termination was successfully started, false otherwise.
   */
  boolean terminateInstance(String instanceId) {
    terminateInstances([instanceId]) == 1
  }

  /**
   * Terminate one or more EC2 instances.
   *
   * @param instanceIds the list of instance id to terminate.
   * @return number of instances which termination has successfully started.
   */
  int terminateInstances(List<String> instanceIds) {
    def response = ec2.terminateInstances(new TerminateInstancesRequest(instanceIds))
    response.terminatingInstances.size()
  }

  /**
   * Lists all available images.
   * 
   * @return list of image data.
   */
  List<Image> listAllImages() {
    def response = ec2.describeImages(new DescribeImagesRequest())
    response.images.collect { map(ec2, it) }
  }

  /**
   * Lists all images owned by current user.
   *
   * @return list of image data.
   */
  List<Image> listSelfImages() {
    def response = ec2.describeImages(new DescribeImagesRequest().withOwners('self'))
    response.images.collect { map(ec2, it) }
  }

  /**
   * Lists all images owned by Amazon.
   *
   * @return list of image data.
   */
  List<Image> listAmazonImages() {
    def response = ec2.describeImages(new DescribeImagesRequest().withOwners('amazon'))
    response.images.collect { map(ec2, it) }
  }

  /**
   * Lists all images available on marketplace.
   *
   * @return list of image data.
   */
  List<Image> listMarketplaceImages() {
    def response = ec2.describeImages(new DescribeImagesRequest().withOwners('aws-marketplace'))
    response.images.collect { map(ec2, it) }
  }

  /**
   * Lists images available using provided tag filter.
   *
   * @return list of image data.
   */
  List<Image> listImages(Map<String, String> tagFilter = [:]) {
    def response = ec2.describeImages(
      new DescribeImagesRequest().
        withFilters(
          tagFilter.collect { tag, value -> new Filter("tag:" + tag, [ value ]) }
        )
    )
    response.images.collect { map(ec2, it) }
  }

  /**
   *
   * Create an AMI (image) out of an existing instance.
   *
   * @param instanceId the instance id of the instance to create an image from.
   * @param name the name of the AMI.
   * @param description a description of the AMI.
   * @param stopBeforeCreation if true, stop the instance before creating the AMI (recommended).
   * @param stopTimeout optional, set the number of seconds to wait for the instance to stop.
   *                    fails if the waiting time exceeds the timeout.
   * @return the id of the AMI that was created.
   */
  String createImage(String instanceId,
      String name,
      String description,
      boolean stopBeforeCreation = false,
      Integer stopTimeout = 60,
      Integer retryDelay = 5) {

    if (stopBeforeCreation) {
      stopInstance(instanceId)
      withTimeout(stopTimeout, retryDelay) { getInstanceState(instanceId) == "stopped" }
    }

    // Create image.
    def imageRequest = new CreateImageRequest()
        .withInstanceId(instanceId)
        .withName(name)
        .withDescription(description)            
    def imageId = ec2.createImage(imageRequest).imageId
    
    // Add name tag to the image resource.
    addTagsToResource(imageId) {
      ["Name": name]
    }
        
    imageId    
        
  }

  /**
   * Create key pair with random name.
   * 
   * @return created key pair model object. 
   */
  KeyPair createKeyPair() {
    def randomKey = InetAddress.localHost.hostName + '_' + randomAlphanumeric(12)
    createKeyPair(randomKey)
  }

  /**
   * Create key pair with defined name.
   * 
   * @param keyPairName name of key pair to create.
   * @return created key pair model object.
   */
  KeyPair createKeyPair(String keyPairName) {
    def newKeyPair = new CreateKeyPairRequest(keyPairName)
    def response = ec2.createKeyPair(newKeyPair)
    map(this, response.keyPair)
  }
    
  /**
   * Destroy key pair.
   * 
   * @param keyPairName key pair name to destroy.
   */
  void destroyKeyPair(String keyPairName) {
    ec2.deleteKeyPair(new DeleteKeyPairRequest(keyPairName))
  }

  /**
   * Add security group.
   * 
   * @param groupName security group name.
   * @param description group description.
   */
  void createSecurityGroup(String groupName, String description) {
    def result = ec2.createSecurityGroup(
      new CreateSecurityGroupRequest()
        .withGroupName(groupName)
        .withDescription(description)
      )    
    def permissions = [
      new IpPermission()
        .withFromPort(22)
        .withIpProtocol('tcp')
    ]    
    ec2.authorizeSecurityGroupIngress(
      new AuthorizeSecurityGroupIngressRequest()
        .withGroupId(result.groupId)
        .withIpPermissions(permissions)
      )
  }
  
  /**
   * Delete security group.
   * 
   * @param groupName security group name.
   */
  void destroySecurityGroup(String groupName) {
    ec2.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupName(groupName))
  }
      
  /*
   * PRIVATE METHODS
   */

  private List<Instance> listInstancesWithRequest(DescribeInstancesRequest req) {
    DescribeInstancesResult result = ec2.describeInstances(req)
    def instances = []
    result.getReservations().each {
      instances << map(this, it.getInstances()[0])
    }
    instances
  }

  private void addTagsToResource(String resourceId, Closure tags) {
    def tagMap = tags.call()
    if (tagMap) {
      ec2.createTags(
        new CreateTagsRequest()
          .withResources([resourceId])
          .withTags(tagMap.collect { k, v -> new Tag(k, v) })
      )
    }
  }

  private BlockDeviceMapping getEsbInfo(int size, boolean ioOptimized) {

    BlockDeviceMapping bdc = new BlockDeviceMapping()
    bdc.setDeviceName("/dev/sda1")

    // TODO: Implement ephemeral storage usage

    EbsBlockDevice ebs = new EbsBlockDevice()
    ebs.setVolumeType(ioOptimized ? VolumeType.Io1 : VolumeType.Standard)
    ebs.setVolumeSize(size)
    bdc.setEbs(ebs)

    bdc

  }

  private void withTimeout(Integer timeout, Integer retry, Closure block)  {

    boolean timedOut = false
    boolean pass = false
    Throwable thrown = null

    use (TimeCategory) {
      Date startDate = new Date()
      while (!pass && !timedOut && !thrown) {
        try {
          pass = block()
          thrown = null
        } catch (Throwable e) {
          thrown = e
        } finally {
          timedOut = timeout.seconds.ago > startDate
        }
        sleep(retry.seconds.millis.longValue())
      }
    }

    if (thrown) {
      throw new RuntimeException("Execution failed with exception!", thrown)
    }

    if (!pass && timedOut) {
      throw new RuntimeException("Timeout reached!")
    }

    if (!pass && !timedOut) {
      throw new RuntimeException("Execution failed!")
    }

  }

  private void repeat(String message, int pause, int times, Closure block) {
    def count = 0
    while (!(block()) && (count < times)) {
      println message
      sleep(pause)
      count++
    }
    if (count >= times) {
      throw new RuntimeException("Tried $times times without success!")
    }
  }

  private boolean available(String host, int port) {
    try {
      new Socket(host, port).close()
      return true
    } catch (IOException ex) {
      return false
    }
  }

}