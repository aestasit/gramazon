# Gramazon #

## A Groovy-Based API for Amazon EC2 ##

<br>

## About Gramazon ##


Amazon Web Services offers a compute power on demand capability known as the Elastic Compute Cloud (EC2). The server resources in the cloud can be provisioned on demand by making HTTP Query API calls to EC2.

**Gramazon** is an interface library that can be used to interact with the Amazon EC2 system and control server resources on demand from your **Groovy** scripts or from **Gradle**, using a plug-in.

## Installation ###


### Get an AWS Account ###

Before you can make use of this code you will need an Amazon Web Services developer account which you can sign up for at <http://aws-portal.amazon.com/gp/aws/developer/registration/index.html>. This account must also be specifically enabled for Amazon EC2 usage. AWS will provide you with an ‘AWS Access Key ID’ and a ‘Secret Access Key’ which will allow you to authenticate any API calls you make and ensure correct billing to you for usage of the service. Take note of these (and keep them secret!).

## Using Gramazon ##


TODO

## Credits ##


This code is massively dependant on the Amazon SDK for Java. 

## Contact ##


Comments, patches, Git pull requests and bug reports are welcome. Send an email to luciano@aestasit.com.

## Patches & Pull Requests ##


Please follow these steps if you want to send a patch or a GitHub pull request:

- Fork lfiandesio/gramazon

- Create a topic branch: `git checkout -b my_fix`

- Make sure you add tests for your changes and that they all pass with `gradle test` 

- Commit your changes, one change/fix per commit

- Push your fixes branch: `git push origin my_fix`

- Open an Issue on GitHub referencing your branch and send a pull request.

- Please do not push to `master` on your fork. Using a feature/bugfix branch will make everyone’s life easier.

Enjoy!

The Aestas Team
