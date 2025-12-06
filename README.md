# Miao

[RAT-89](https://github.com/anvilventures/RAT/issues/89)

>  Get a quick statistical evaluation of your hunch over a possible time-based information leak. Send the results of an intruder attack sending the same request for a given existing resource a certain amount of times. Do the same for a request with a non-existing resource. The plugin evaluates whether there is statistical evidence that the two series of requests do not belong to the same distribution i.e. there is statistical evidence to say one set of requests is different from the other therefore resource enumeration is feasible.

Build with:

```sh
./gradlew bigJar
```

Then in Burp, go to Extensions tab and load the jar from `build/libs/`.