/*
 * TryParseCatchingTest.java
 * Copyright 2019 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.tryparse;

import static com.github.robtimus.tryparse.TryParse.tryParse;
import static com.github.robtimus.tryparse.TryParse.tryParseDouble;
import static com.github.robtimus.tryparse.TryParse.tryParseInstant;
import static com.github.robtimus.tryparse.TryParse.tryParseLocalDate;
import static com.github.robtimus.tryparse.TryParse.tryParseLocalDateTime;
import static com.github.robtimus.tryparse.TryParse.tryParseLocalTime;
import static com.github.robtimus.tryparse.TryParse.tryParseOffsetDateTime;
import static com.github.robtimus.tryparse.TryParse.tryParseOffsetTime;
import static com.github.robtimus.tryparse.TryParse.tryParseTemporal;
import static com.github.robtimus.tryparse.TryParse.tryParseURI;
import static com.github.robtimus.tryparse.TryParse.tryParseURL;
import static com.github.robtimus.tryparse.TryParse.tryParseZonedDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class TryParseCatchingTest {

    @Test
    public void testParseDouble() {
        assertEquals(OptionalDouble.empty(), tryParseDouble(null));
        assertEquals(OptionalDouble.empty(), tryParseDouble(""));
        assertEquals(123.456, tryParseDouble("123.456").getAsDouble(), 0.00001);
        assertEquals(OptionalDouble.empty(), tryParseDouble("foo"));
    }

    @Test
    public void testParseURI() {
        assertEquals(Optional.empty(), tryParseURI(null));
        // an empty URI is valid
        assertEquals(Optional.of(URI.create("http://example.org")), tryParseURI("http://example.org"));
        assertEquals(Optional.empty(), tryParseURI("http://example.org##"));
    }

    @Test
    public void testParseURL() throws MalformedURLException {
        assertEquals(Optional.empty(), tryParseURL(null));
        assertEquals(Optional.empty(), tryParseURL(""));
        assertEquals(Optional.of(new URL("http://example.org")), tryParseURL("http://example.org"));
        assertEquals(Optional.empty(), tryParseURL("foo"));
    }

    @Test
    public void testParseInstant() {
        assertEquals(Optional.empty(), tryParseInstant(null));
        assertEquals(Optional.empty(), tryParseInstant(""));
        assertEquals(Optional.of(Instant.parse("2007-12-03T10:15:30.00Z")), tryParseInstant("2007-12-03T10:15:30.00Z"));
        assertEquals(Optional.empty(), tryParseURL("2007-12-03T10:15:30.00ZX"));
    }

    @Test
    public void testParseLocalDate() {
        assertEquals(Optional.empty(), tryParseLocalDate(null));
        assertEquals(Optional.empty(), tryParseLocalDate(""));
        assertEquals(Optional.of(LocalDate.of(2007, 12, 3)), tryParseLocalDate("2007-12-03"));
        assertEquals(Optional.empty(), tryParseLocalDate("2007-12-03T"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertEquals(Optional.empty(), tryParseLocalDate(null, formatter));
        assertEquals(Optional.empty(), tryParseLocalDate("", formatter));
        assertEquals(Optional.of(LocalDate.of(2007, 12, 3)), tryParseLocalDate("03-12-2007", formatter));
        assertEquals(Optional.empty(), tryParseLocalDate("03-12-2007T", formatter));
    }

    @Test
    public void testParseLocalDateTime() {
        assertEquals(Optional.empty(), tryParseLocalDateTime(null));
        assertEquals(Optional.empty(), tryParseLocalDateTime(""));
        assertEquals(Optional.of(LocalDateTime.of(2007, 12, 3, 10, 15, 30)), tryParseLocalDateTime("2007-12-03T10:15:30"));
        assertEquals(Optional.empty(), tryParseLocalDateTime("2007-12-03 10:15:30"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
        assertEquals(Optional.empty(), tryParseLocalDateTime(null, formatter));
        assertEquals(Optional.empty(), tryParseLocalDateTime("", formatter));
        assertEquals(Optional.of(LocalDateTime.of(2007, 12, 3, 10, 15, 30)), tryParseLocalDateTime("03-12-2007, 10:15:30", formatter));
        assertEquals(Optional.empty(), tryParseLocalDateTime("03-12-2007 10:15:30", formatter));
    }

    @Test
    public void testParseLocalTime() {
        assertEquals(Optional.empty(), tryParseLocalTime(null));
        assertEquals(Optional.empty(), tryParseLocalTime(""));
        assertEquals(Optional.of(LocalTime.of(10, 15, 30)), tryParseLocalTime("10:15:30"));
        assertEquals(Optional.empty(), tryParseLocalTime("10.15.30"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS");
        assertEquals(Optional.empty(), tryParseLocalTime(null, formatter));
        assertEquals(Optional.empty(), tryParseLocalTime("", formatter));
        assertEquals(Optional.of(LocalTime.of(10, 15, 30, 123_400_000)), tryParseLocalTime("10:15:30.1234", formatter));
        assertEquals(Optional.empty(), tryParseLocalTime("10:15:30", formatter));
    }

    @Test
    public void testParseOffsetDateTime() {
        assertEquals(Optional.empty(), tryParseOffsetDateTime(null));
        assertEquals(Optional.empty(), tryParseOffsetDateTime(""));
        assertEquals(Optional.of(OffsetDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneOffset.ofHours(1))),
                tryParseOffsetDateTime("2007-12-03T10:15:30+01:00"));
        assertEquals(Optional.empty(), tryParseOffsetDateTime("2007-12-03T10:15:30 GMT"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss, X");
        assertEquals(Optional.empty(), tryParseOffsetDateTime(null, formatter));
        assertEquals(Optional.empty(), tryParseOffsetDateTime("", formatter));
        assertEquals(Optional.of(OffsetDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneOffset.of("Z"))),
                tryParseOffsetDateTime("03-12-2007, 10:15:30, +00", formatter));
        assertEquals(Optional.empty(), tryParseOffsetDateTime("03-12-2007, 10:15:30, GMT", formatter));
    }

    @Test
    public void testParseOffsetTime() {
        assertEquals(Optional.empty(), tryParseOffsetTime(null));
        assertEquals(Optional.empty(), tryParseOffsetTime(""));
        assertEquals(Optional.of(OffsetTime.of(10, 15, 30, 0, ZoneOffset.ofHours(1))), tryParseOffsetTime("10:15:30+01:00"));
        assertEquals(Optional.empty(), tryParseOffsetTime("10:15:30 GMT"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss, X");
        assertEquals(Optional.empty(), tryParseOffsetTime(null, formatter));
        assertEquals(Optional.empty(), tryParseOffsetTime("", formatter));
        assertEquals(Optional.of(OffsetTime.of(10, 15, 30, 0, ZoneOffset.of("Z"))), tryParseOffsetTime("10:15:30, +00", formatter));
        assertEquals(Optional.empty(), tryParseOffsetTime("10:15:30, GMT", formatter));
    }

    @Test
    public void testParseZonedDateTime() {
        assertEquals(Optional.empty(), tryParseZonedDateTime(null));
        assertEquals(Optional.empty(), tryParseZonedDateTime(""));
        assertEquals(Optional.of(ZonedDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneId.of("Europe/Paris"))),
                tryParseZonedDateTime("2007-12-03T10:15:30+01:00[Europe/Paris]"));
        assertEquals(Optional.empty(), tryParseZonedDateTime("2007-12-03T10:15:30+01:00[foo]"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss, VV");
        assertEquals(Optional.empty(), tryParseZonedDateTime(null, formatter));
        assertEquals(Optional.empty(), tryParseZonedDateTime("", formatter));
        assertEquals(Optional.of(ZonedDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneId.of("Europe/Paris"))),
                tryParseZonedDateTime("03-12-2007, 10:15:30, Europe/Paris", formatter));
        assertEquals(Optional.empty(), tryParseZonedDateTime("03-12-2007, 10:15:30", formatter));
    }

    @Test
    public void testParseTemporal() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertEquals(Optional.empty(), tryParseTemporal(null, formatter, LocalDate::from));
        assertEquals(Optional.empty(), tryParseTemporal("", formatter, LocalDate::from));
        assertEquals(Optional.of(LocalDate.of(2007, 12, 3)), tryParseTemporal("03-12-2007", formatter, LocalDate::from));
        assertEquals(Optional.empty(), tryParseTemporal("03-12-2007T", formatter, LocalDate::from));
    }

    @Test
    public void testParse() {
        assertEquals(Optional.empty(), tryParse(null, o -> o));
        assertEquals(Optional.of(""), tryParse("", o -> o));
        assertEquals(Optional.of("foo"), tryParse("foo", o -> o));
        assertEquals(Optional.empty(), tryParse(null, o -> Objects.requireNonNull(o)));

        assertEquals(Optional.empty(), tryParse(null, o -> o, NullPointerException.class));
        assertEquals(Optional.of(""), tryParse("", o -> o, NullPointerException.class));
        assertEquals(Optional.of("foo"), tryParse("foo", o -> o, NullPointerException.class));
        assertEquals(Optional.empty(), tryParse(null, o -> Objects.requireNonNull(o), NullPointerException.class));
        assertThrows(NullPointerException.class, () -> tryParse(null, o -> Objects.requireNonNull(o), IllegalArgumentException.class));
    }
}
