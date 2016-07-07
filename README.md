# atmosphere-service
This is the basic on-device component of the ATMOSPHERE framework. It provides a socket connection between the Agent and the Device and some additional classes and services for obtaining and manipulating information about the device state, e.g. free storage space, sensor information and location.

## Project setup
This project depends on the [atmosphere-agent-device-lib](https://github.com/MusalaSoft/atmosphere-agent-device-lib), so make sure you publish `atmosphere-agent-device-lib` to your local Maven repository first.

### Setup Android SDK
[Here](https://github.com/MusalaSoft/atmosphere-docs/blob/master/setup/android_sdk.md) you may read how to setup Android SDK.

### Build the project
You can build the project using the included Gradle wrapper by running:
* `./gradlew build` on Linux/macOS<br/>
* `gradlew build` on Windows

### Publish to Maven Local
If the build is successful, also run:
* `./gradlew publishToMavenLocal` on Linux/macOS
* `gradlew publishToMavenLocal` on Windows

to publish the jar to the local Maven repository, so other projects that depend on it can use it.
