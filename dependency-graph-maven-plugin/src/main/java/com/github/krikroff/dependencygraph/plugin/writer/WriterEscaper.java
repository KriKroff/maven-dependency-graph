/*
 * Copyright (c) 2014 by Stefan Ferstl <st.ferstl@gmail.com>
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
package com.github.krikroff.dependencygraph.plugin.writer;

import java.util.regex.Pattern;


public final class WriterEscaper {

  private static final String QUOTE_REPLACEMENT = "\\\\\"";
  private static final Pattern REPLACE_NEWLINE_PATTERN = Pattern.compile("(\\r\\n)|[\\r\\n]", Pattern.DOTALL);
  private static final Pattern REPLACE_QUOTE_PATTERN = Pattern.compile("\"");

  private String value;

  private WriterEscaper(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static String escape(Object value) {
    return new WriterEscaper(value.toString())
      .escapeNewLines()
      .escapeQuotes()
      .quoteIfRequired()
      .getValue();
  }

  private WriterEscaper escapeNewLines() {
    this.value = REPLACE_NEWLINE_PATTERN.matcher(this.value).replaceAll("\\\\n");
    return this;
  }

  private WriterEscaper escapeQuotes() {
    if (isQuoted()) {
      String valueToQuote = this.value.substring(1, this.value.length() - 1);
      this.value = "\"" + REPLACE_QUOTE_PATTERN.matcher(valueToQuote).replaceAll(QUOTE_REPLACEMENT) + "\"";
    } else {
      this.value = REPLACE_QUOTE_PATTERN.matcher(this.value).replaceAll(QUOTE_REPLACEMENT);
    }

    return this;
  }

  private WriterEscaper quoteIfRequired() {
    if (requiresQuoting()) {
      this.value = "\"" + this.value + "\"";
    }

    return this;
  }

  private boolean requiresQuoting() {
    return !isQuoted();
  }

  private boolean isQuoted() {
    return this.value.startsWith("\"") && this.value.endsWith("\"");
  }
}
