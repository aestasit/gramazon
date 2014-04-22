# Gramazon #

## A Groovy-Based API for Amazon EC2 ##

<br>

## About Gramazon ##

Amazon Web Services offers a compute power on demand capability known as the Elastic Compute Cloud (EC2). The server resources in the cloud can be provisioned on demand by making **HTTP Query API** calls to EC2.

**Gramazon** is an interface library that can be used to interact with the Amazon EC2 system and control server resources on demand from your **Groovy** scripts or from **Gradle**, using a plug-in.

## Installation ###

### Get an AWS Account ###

Before you can make use of this code you will need an Amazon Web Services developer account which you can sign up for at <http://aws-portal.amazon.com/gp/aws/developer/registration/index.html>. This account must also be specifically enabled for Amazon EC2 usage. AWS will provide you with an ‘AWS Access Key ID’ and a ‘Secret Access Key’ which will allow you to authenticate any API calls you make and ensure correct billing to you for usage of the service. Take note of these (and keep them secret!).

### Build Gramazon ###

**Gramazon** is built using [Gradle](http://www.gradle.org/). Install **Gradle** and type:

	gradle clean build
Please note that the build also executes some tests against Amazon EC2. In order for the tests to pass, a 'test.properties' file has to be created inside `/src/test/resources`. The file must contain the following key/value pairs properties:

	aws.accessKeyId=XXXXXXXXX # the amazon ec2 access key
	aws.secretKey=the amazon ec2 secret key
	aws.defaultRegion=eu-west-1
	aws.defaultAmi=XXXX # the default AMI id to use in the tests
	aws.defaultKeypair=XXXXX # the default key pair
	aws.defaultSecurity=XXXXXXX # the default security group
	aws.defaultInstanceType=t1.micro
	aws.defaultEBSSize=-1 # set to -1 to use the standard AMI ESB size


## Using Gramazon ##

### Groovy API ###

The easiest way to use **Gramazon** in a **Groovy** script is by importing the dependency using [Grape](http://groovy.codehaus.org/Grape).

	@GrabResolver(name='oss', root='https://oss.sonatype.org/content/repositories/snapshots/')
	@Grab('com.aestasit.infrastructure.aws:gramazon:0.1-SNAPSHOT')
	import com.aestasit.infrastructure.aws.*

Alternatively, the library can be imported using your favourite build tool.

**Gramazon** has currently one main class, `EC2Client` which is invoked like so:

	def ec2 = new EC2Client('eu-west-1')

The class requires that the EC2 region is specified in the constructor. For a list of available regions, please refer to this [document](http://docs.aws.amazon.com/general/latest/gr/rande.html#ec2_region).

In order for the class to connect to the Amazon EC2 cloud infrastructure, the _Access Key ID_ and the _Secret Key_ must be specified. If you don't know these values, access this [page](https://portal.aws.amazon.com/gp/aws/securityCredentials) to get them (you need an active Amazon EC2 account). The the _Access Key ID_ and the _Secret Key_ are passed to **Gramazon** as system properties.

	System.setProperty("aws.accessKeyId", '#################')
	System.setProperty("aws.secretKey", '####################################')

For a list of available methods, please refer to the **Groovydoc** documentation available in the **Groovydoc** [pages](http://aestasit.github.io/gramazon/groovydoc/index.html) . 


### Gradle Plugin ###

TODO

## Credits ##

This code is massively dependant on the Amazon SDK for Java. 

## Contact ##

Comments, patches, Git pull requests and bug reports are welcome. Send an email to opensource@aestasit.com.

## Patches & Pull Requests ##


Please follow these steps if you want to send a patch or a GitHub pull request:

- Fork aestasit/gramazon

- Create a topic branch: `git checkout -b my_fix`

- Make sure you add tests for your changes and that they all pass with `gradle test` 

- Commit your changes, one change/fix per commit

- Push your fixes branch: `git push origin my_fix`

- Open an Issue on GitHub referencing your branch and send a pull request.

- Please do not push to `master` on your fork. Using a feature/bugfix branch will make everyone’s life easier.

Enjoy!

The Aestas Team


