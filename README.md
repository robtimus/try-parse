# try-parse
[![Maven Central](https://img.shields.io/maven-central/v/com.github.robtimus/try-parse)](https://search.maven.org/artifact/com.github.robtimus/try-parse)
[![Build Status](https://github.com/robtimus/try-parse/actions/workflows/build.yml/badge.svg)](https://github.com/robtimus/try-parse/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Atry-parse&metric=alert_status)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Atry-parse)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Atry-parse&metric=coverage)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Atry-parse)
[![Known Vulnerabilities](https://snyk.io/test/github/robtimus/try-parse/badge.svg)](https://snyk.io/test/github/robtimus/try-parse)

Provides functionality to parse to [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) and its primitive versions.

Currently there are five sets of methods:

* `TryParse.tryParseInt`: a copy of `Integer.parseInt` to parse to [OptionalInt](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalInt.html) instead of throwing exceptions.
* `TryParse.tryParseUnsignedInt`: a copy of `Integer.parseUnsignedInt` to parse to [OptionalInt](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalInt.html) instead of throwing exceptions.
* `TryParse.tryParseLong`: a copy of `Long.parseLong` to parse to [OptionalLong](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalLong.html) instead of throwing exceptions.
* `TryParse.tryParseUnsignedLong`: a copy of `Long.parseUnsignedLong` to parse to [OptionalLong](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalLong.html) instead of throwing exceptions.
* `TryParse.tryParseBoolean`: a copy of `Boolean.parseBoolean` to parse to [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) instead of returning `false`.

In addition, there are several methods that delegate to an existing method, catching expected exceptions. These are not as fast as the above five sets, but can still be useful.
