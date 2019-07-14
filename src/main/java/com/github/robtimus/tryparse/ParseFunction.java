/*
 * ParseFunction.java
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

/**
 * A function that parses one object into another.
 *
 * @author Rob Spoor
 * @param <T> The type of object to parse.
 * @param <R> The type of the parse result.
 * @param <X> The type of exception that occurs if parsing fails.
 * @since 1.1
 */
public interface ParseFunction<T, R, X extends Exception> {

    /**
     * Parses an object.
     *
     * @param input The object to parse.
     * @return The parsed object.
     * @throws X If parsing fails.
     */
    R parse(T input) throws X;
}
