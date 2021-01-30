# Java Flight Recorder - Linux Support

Examples: https://github.com/petrbouda/jfr-cgroups-examples

- https://docs.oracle.com/en/java/javase/14/jfapi/
- https://stackoverflow.com/questions/32589840/java-flight-recorder-options-not-working-and-file-is-stored-only-in-the-end-of-t
- `java -XshowSettings -version` check settings
- Reading JFR file https://docs.oracle.com/en/java/javase/14/docs/specs/man/jfr.html
- https://docs.oracle.com/en/java/javase/14/docs/api/jdk.jfr/jdk/jfr/package-summary.html

#### Execution

JFR Logging: `-Xlog:jfr*=debug`

##### From a very beginning 

```
-XX:StartFlightRecording=delay=5s,duration=10s,settings=profile,filename=test.jfr
```

#### Using JCMD tool

```
jcmd 1 JFR.start name=test maxsize=2G settings=profile disk=true path-to-gc-roots=true
jcmd 1 JFR.configure stackdepth=128
jcmd 1 JFR.dump path-to-gc-roots=true name=test
jcmd 1 JFR.stop name=test
jcmd 1 JFR.stop name=test filename=test.jfr
```

##### -XX:+DebugNonSafepoints Option 

```
One nice property of the JFR method profiler is that it does not require 
threads to be at safe points in order for stacks to be sampled. 
However, since the common case is that stacks will only be walked at safe points, 
HotSpot normally does not provide metadata for non-safe point parts of the code, 
which means that such samples will not be properly resolved to the correct line 
number and BCI. 
```

Check whether JVM runs with this option enabled (especially when we start JFR using JCMD tool):
```
java -XX:+UnlockDiagnosticVMOptions -XX:+PrintFlagsFinal -version | grep Debug
```
otherwise, use this to enable it.
```
-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints
```

### How to try it out

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>jfr-linux</id>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                    <url>https://maven.pkg.github.com/petrbouda/jfr-linux</url>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <servers>
        <server>
            <id>jfr-linux</id>
            <username>petrbouda</username>
            <password>{access-token}</password>
        </server>
    </servers>
</settings>
```

```
<dependency>
  <groupId>pbouda.jfr</groupId>
  <artifactId>linux-recorder</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```