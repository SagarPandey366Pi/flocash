### Welcome to floj

The floj library is a Java implementation of the FLO protocol, which allows it to maintain a wallet and send/receive transactions without needing a local copy of FLO Core. It comes with full documentation and some example apps showing how to use it.

### Technologies

* Java 6 for the core modules, Java 8 for everything else
* [Maven 3+](http://maven.apache.org) - for building the project
* [Orchid](https://github.com/subgraph/Orchid) - for secure communications over [TOR](https://www.torproject.org)
* [Google Protocol Buffers](https://github.com/google/protobuf) - for use with serialization and hardware communications

### Getting started

To get started, it is best to have the latest JDK and Maven installed. The HEAD of the `master` branch contains the latest development code and various production releases are provided on feature branches.

#### Building from an IDE

Import the project using your IDE. [IntelliJ](http://www.jetbrains.com/idea/download/) has Maven integration built-in and has a free Community Edition. Simply use `File | Import Project` and locate the `pom.xml` in the root of the cloned project source tree.

If you open the pom.xml file in IntelliJ as "Import Project from Maven", be sure to check "Import Maven projects automatically" checkbox in the dialog that appears.

Then be sure to select Java JDK 1.8 as the SDK.

Once the projects load, right-click on the Main file in the flowallet project and click "Run".

As an alternative, Eclipse Neon or [Oxygen](https://eclipse.org/oxygen/) or other version, with the [Maven plugin](https://stackoverflow.com/questions/8620127/maven-in-eclipse-step-by-step-installation), also allows importing using the 'pom.xml' file.

### Where next?

Now you are ready to [follow the tutorial](https://bitcoinj.github.io/getting-started).

### Basic Android App

A basic Android app would have something like this in it:
```
// Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
kit = new WalletAppKit(params, new File("."), filePrefix) {
    @Override
    protected void onSetupCompleted() {
        // This is called in a background thread after startAndWait is called, as setting up various objects
        // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
        // on the main thread.
        if (wallet().getKeyChainGroupSize() < 1)
            wallet().importKey(new ECKey());
    }
};

if (params == RegTestParams.get()) {
    // Regression test mode is designed for testing and development only, so there's no public network for it.
    // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
    kit.connectToLocalHost();
}

// Download the block chain and wait until it's done.
kit.startAsync();
kit.awaitRunning();
```

This is the [basics of getting the wallet app kit working.](https://bitcoinj.github.io/getting-started-java)


#### Building from the command line

To perform a full build use
```
mvn clean package
```
You can also run
```
mvn site:site
```
to generate a website with useful information like JavaDocs.

The outputs are under the `target` directory.