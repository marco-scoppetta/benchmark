java_test(
    name = "grakn-benchmark",
    test_class = "grakn.benchmark.runner.GraknBenchmarkTest",
    srcs = ["GraknBenchmarkTest.java"],
    deps = ["//runner:benchmark-runner"],
    classpath_resources = ["//runner/test/resources:logback-test"],
    data = [
        "//runner/test/resources:web-content-config"
    ]
)