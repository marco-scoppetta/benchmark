java_library(
    name = "metrics-core",
    exports = [
        "//external:jar/com/codahale/metrics/metrics-core"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)



java_library(
    name = "metrics-ganglia",
    exports = [
        "//external:jar/com/codahale/metrics/metrics-ganglia"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/info/ganglia/gmetric4j:gmetric4j",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
        ":metrics-core"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)



java_library(
    name = "metrics-graphite",
    exports = [
        "//external:jar/com/codahale/metrics/metrics-graphite"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
        ":metrics-core"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)


