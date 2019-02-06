java_test(
    name = "benchmark-test",
    test_class = "grakn.benchmark.runner.GraknBenchmarkTest",
    srcs = ["GraknBenchmarkTest.java"],
    deps = [
        "//profiler:benchmark-runner",
        "//dependencies/maven/artifacts/commons-cli",
    ],
    classpath_resources = ["//profiler/test/resources:logback-test"],
    data = [
        "//profiler/test/resources:web-content-config"
    ]
)