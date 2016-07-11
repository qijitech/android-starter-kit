# Overview

android-starter-kit is a fast development kit.

# Dependencies

This library is published to the Maven Central repository, so you can use it through Gradle/Maven.
You can use it in Eclipse, but Android Studio (or Gradle) is recommended.
In Quick start guide, we assume you're using Android Studio.

## build.gradle

Write the following dependency configuration to your `build.gradle`.

```gradle
repositories {
    mavenCentral()
}

dependencies {
    // Other dependencies are omitted
    compile 'com.smartydroid:android-starter-kit:VERSION'
}
```

You should replace `VERSION` to the appropriate version number like `0.1.14`.

Then, click "sync" button to get the library using the configuration above.

To confirm the available versions, search [the Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Candroid-starter-kit).

# Build on Android Studio

This library and samples basically support Android Studio and Gradle.
(Actually, I'm using them to develop this library.)

## Instructions

### Get the source codes

Get the source code of the library and example app, by cloning git repository or downloading archives.

If you use git, execute the following command in your workspace directory.

```
$ git clone https://github.com/qijitech/android-starter-kit.git
```

If you are using Windows, try it on GitBash or Cygwin or something that supports git.

### Import the project to Android Studio

1. Select File &gt; New &gt; Import Project... from the menu.
1. Select the directory that is cloned. If you can't see your cloned directory, click "Refresh" icon and find it.
1. Android Studio will import the project and build it. This might take minutes to complete. Even when the project window is opened, wait until the Gradle tasks are finished and indexed.
1. Click "Run 'app'" button to build and launch the app. Don't forget to connect your devices to your machine.

### Build and install using Gradle

If you just want to install the app to your device, you don't have to import project to Android Studio.

After cloning the project, connect your device to your machine, and execute the following command on the terminal.

Mac / Linux / Git Bash, Cygwin on Windows:

```sh
$ cd /path/to/android-starter-kit
$ ./gradlew installDebug
```

Windows (Command prompt):

```sh
> cd C:\path\to\android-starter-kit
> gradlew installDebug
```

# Environment

## Development

This project is built and tested under the following environment.

* OS: Mac OS X 10.11.4
* IDE: Android Studio 2.1.2
* JDK: 1.7

## Prerequisites for building on Android Studio

* Android Studio (2.0.0+)
* Oracle JDK 7
* Android SDK Tools (Rev.25.1.7)
* Android SDK Build-tools (Rev.24)
* Android N SDK Platform (Rev.1)
* Android Support Repository (Rev.33)
* Android Support Library (Rev.23.2.1)