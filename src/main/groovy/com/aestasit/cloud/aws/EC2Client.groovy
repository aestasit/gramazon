package com.aestasit.cloud.aws

import com.aestasit.cloud.aws.Instance
import com.aestasit.cloud.aws.util.MapHelper
import com.amazonaws.auth.SystemPropertiesCredentialsProvider
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.*
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import groovy.time.TimeCategory

/**
 *
 * Client class to interact with Amazon EC2 Instances.
 * The only constructor allows to set the EC2 Region (e.g. eu-west-1).
 *
 * The class provides credentials by looking at the <code>aws.accessKeyId</code>
 * and <code>aws.secretKey</code> Java system properties.
 *
 * @author Aestas IT
 *
 */
class EC2Client {

  private final static String RUNNING_STATE = "running"

  static final int EC2_API_REQUEST_DELAY = 5000
  static final int SSH_CONNECTION_RETRY_DELAY = 5000
  static final int DEFAULT_RETRY_COUNT = 30

  private final AmazonEC2 ec2

  EC2Client(region) {
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
   * @param waitForStart if true then method is wating for the instance to become available.
   * @return an Instance object containing the data of the started instance.
   */
  Instance startInstance(String keyName,
      String ami,
      String securityGroup,
      String instanceType,
      boolean waitForStart,
      int esbVolumeSize = -1,
      String instanceName = null,
      Map<String, String> additionalTags = [:]) {

    def req = new RunInstancesRequest()
    req.keyName = keyName
    req.imageId = ami
    req.securityGroups = [securityGroup]
    req.instanceType = InstanceType.fromValue(instanceType)
    req.minCount = 1
    req.maxCount = 1
    if (esbVolumeSize != -1) {
      req.setBlockDeviceMappings([
        getEsbInfo(esbVolumeSize, false)
      ])
    }

    def result = ec2.runInstances(req)
    def instanceId = result.reservation.instances[0].instanceId

    // Sleep for a while, to give time to the instance to properly initialize.
    sleep(EC2_API_REQUEST_DELAY)

    // Set instance name.
    addTagsToInstance(result.reservation.instances[0].instanceId, {
      if (instanceName) {
        additionalTags << ["Name": instanceName]
      }
      additionalTags ?: null
    })

    // Sleep for a while, to give time to the instance to properly initialize.
    sleep(EC2_API_REQUEST_DELAY)

    // Wait for instace to have 'running' state.
    repeat("creating instance...", EC2_API_REQUEST_DELAY, DEFAULT_RETRY_COUNT) {
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
      repeat("initiating ssh connection...", SSH_CONNECTION_RETRY_DELAY, DEFAULT_RETRY_COUNT) {
        available(instance.host, 22)
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
   * @param instanceName the instance name pattern, supports wildcards
   * @param tagFilter the map of additional tag values.
   * @return collection of instance data.
   */
  List<Instance> listInstances(String instanceName, Map<String, String> tagFilter = [:]) {
    listInstancesWithRequest(new DescribeInstancesRequest()
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
   */
  void stopInstance(String instanceId) {
    ec2.stopInstances(new StopInstancesRequest().withInstanceIds([instanceId]))
  }

  /**
   * Terminate an EC2 instance.
   *
   * @param instanceIds the list of instance id to terminate.
   * @return number of terminated instances.
   */
  int terminateInstances(List<String> instanceIds) {
    def response = ec2.terminateInstances(new TerminateInstancesRequest(instanceIds))
    response.terminatingInstances.size()
  }

  /**
   *
   * Create an AMI (image) out of an existing instance.
   *
   * @param instanceId the instance id of the instance to create an image from.
   * @param name the name of the AMI.
   * @param description a description of the AMI.
   * @param stopBeforeCreation if true, stop the instance before creating the AMI (reccomended).
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

    def imageRequest = new CreateImageRequest()
        .withInstanceId(instanceId)
        .withName(name)
        .withDescription(description)

    ec2.createImage(imageRequest).imageId

  }

  /*
   * PRIVATE METHODS
   */

  private List<Instance> listInstancesWithRequest(DescribeInstancesRequest req) {
    DescribeInstancesResult result = ec2.describeInstances(req)
    def instances = []
    result.getReservations().each {
      instances << MapHelper.map(ec2, it.getInstances()[0])
    }
    instances
  }

  private void addTagsToInstance(String instance, Closure tags) {
    def tagMap = tags.call()
    if (tagMap) {
      ec2.createTags(new CreateTagsRequest()
          .withResources([instance])
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

  private void withTimeout(Integer x, Integer y, Closure block)  {

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
          timedOut = startDate > x.seconds.ago
        }
        sleep(y.seconds.millis.longValue())
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

  private boolean available(String host, int port, String keyFilePath = null) {
    JSch jsch = new JSch()
    Properties config = new Properties()
    config.put("StrictHostKeyChecking", "no")
    config.put("HashKnownHosts",  "yes")
    jsch.config = config
    Session session = jsch.getSession("root", host, port)
    if (keyFilePath != null) {
      jsch.addIdentity(keyFilePath)
    }
    // TODO: We should provide key file for proper connection checking.
    try {
      session.connect()
      session.disconnect()
      return true
    } catch (com.jcraft.jsch.JSchException e) {
      // TODO: This code should not be here when key file is added to the list of parameters.
      if (e.message.contains("Auth fail")) {
        return true
      }
    } catch (Exception e1) {
      e1.printStackTrace()
    }
    false
  }

}