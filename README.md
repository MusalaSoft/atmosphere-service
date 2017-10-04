[![Build Status](https://travis-ci.org/MusalaSoft/atmosphere-service.svg?branch=master)](https://travis-ci.org/MusalaSoft/atmosphere-service) [ ![Download](https://api.bintray.com/packages/musala/atmosphere/atmosphere-service/images/download.svg) ](https://bintray.com/musala/atmosphere/atmosphere-service/_latestVersion)  
See our site for better context of this readme. [Click here](http://atmosphereframework.com/)

# atmosphere-service
This is the basic on-device component of the ATMOSPHERE framework. It provides a socket connection between the Agent and the Device and some additional classes and services for obtaining and manipulating information about the device state, e.g. free storage space, sensor information and location.

## Project setup

### Setup Android SDK
[Here](https://github.com/MusalaSoft/atmosphere-docs/blob/master/setup/android_sdk.md) you may read how to setup Android SDK. To build this project you will need:
* `SDK Build-tools` `25.0.0`
* `SDK Platform` for `Android 7.1.1 (API 25)`

### Build the project
You can build the project using the included Gradle wrapper by running:
* `./gradlew build` on Linux/macOS
* `gradlew build` on Windows

### Making changes
If you make changes to this project and would like to use your new version in another ATMOSPHERE framework project that depends on this one, after a successful build also run:
* `./gradlew publishToMavenLocal` (Linux/macOS)
* `gradlew publishToMavenLocal` (Windows)

to publish the jar to your local Maven repository. The ATMOSPHERE framework projects are configured to use the artifact published in the local Maven repository first.
