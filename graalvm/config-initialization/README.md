


```xml

# to initialize ConfigExample at buil-time to improve performance
    <configuration>
        <buildArgs>
        <buildArg>--no-fallback</buildArg>
        <buildArg>-H:IncludeResources=account-list.json</buildArg>
        <buildArg>--initialize-at-build-time=org.graalvm.ConfigExample</buildArg>                        
        </buildArgs>

```