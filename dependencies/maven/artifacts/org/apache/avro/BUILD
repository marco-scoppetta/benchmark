java_library(
    name = "avro",
    exports = [
        "//external:jar/org/apache/avro/avro"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/com/thoughtworks/paranamer:paranamer",
        "//dependencies/maven/artifacts/org/apache/commons:commons-compress",
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-core-asl",
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-mapper-asl",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
        "//dependencies/maven/artifacts/org/xerial/snappy:snappy-java"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)



java_library(
    name = "avro-ipc",
    exports = [
        "//external:jar/org/apache/avro/avro-ipc"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-core-asl",
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-mapper-asl",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
        ":avro"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)



java_library(
    name = "avro-ipc-jar-tests",
    exports = [
        "//external:jar/org/apache/avro/avro-ipc-jar-tests"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-core-asl",
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-mapper-asl",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
        ":avro"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)



java_library(
    name = "avro-mapred-jar-hadoop2",
    exports = [
        "//external:jar/org/apache/avro/avro-mapred-jar-hadoop2"
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-core-asl",
        "//dependencies/maven/artifacts/org/codehaus/jackson:jackson-mapper-asl",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
        ":avro-ipc",
        ":avro-ipc-jar-tests"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)


