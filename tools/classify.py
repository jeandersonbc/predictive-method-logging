import os
import re


def classify(source_path: str) -> str:
    test = lambda e, patterns: any(p in e for p in patterns)
    if test(source_path, ["Test", "/test", "test/", "mock/", "/mock"]):
        return "test-related"
    if test(source_path, ["buildSrc/"]):
        return "build-related"
    if test(source_path, ["/docs/", "/examples/", "/sample", "sample/"]):
        return "doc-related"
    # Leaving this unclassified to be ignored on later stages
    if test(
        source_path,
        [
            "giraph-core/templates",
            "/src/main/resources/archetype-resources/",
            "zookeeper-server/src/main/java-filtered",
            "org/apache/tez/mapreduce/hadoop/DeprecatedKeys.java",
        ],
    ):
        return "others"
    return "production-related"


def main(argv):
    input_file = argv[1]

    fpath = os.path.abspath(input_file)
    outdir = os.path.dirname(fpath)
    with open(fpath) as f:
        for line in f:
            line = line.strip()
            print(line, classify(line))


if __name__ == "__main__":
    from sys import argv

    main(argv)
