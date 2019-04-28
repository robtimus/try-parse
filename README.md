# try-parse

Provides functionality to parse to [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) and its primitive versions.

Currently there are five sets of methods:

* `TryParse.tryParseInt`: a copy of `Integer.parseInt` to parse to [OptionalInt](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalInt.html) instead of throwing exceptions.
* `TryParse.tryParseUnsignedInt`: a copy of `Integer.parseUnsignedInt` to parse to [OptionalInt](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalInt.html) instead of throwing exceptions.
* `TryParse.tryParseLong`: a copy of `Long.parseLong` to parse to [OptionalLong](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalLong.html) instead of throwing exceptions.
* `TryParse.tryParseUnsignedLong`: a copy of `Long.parseUnsignedLong` to parse to [OptionalLong](https://docs.oracle.com/javase/8/docs/api/java/util/OptionalLong.html) instead of throwing exceptions.
* `TryParse.tryParseBoolean`: a copy of `Boolean.parseBoolean` to parse to [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) instead of returning `false`.
