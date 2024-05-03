/*
 * SonarScanner CLI
 * Copyright (C) 2011-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.scanner.cli;

import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonarsource.scanner.lib.LogOutput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class LogsTest {
  @Mock
  private PrintStream stdOut;

  @Mock
  private PrintStream stdErr;

  private Logs logs;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    logs = new Logs(stdOut, stdErr);
  }

  @Test
  void testInfo() {
    logs.info("info");
    verify(stdOut).println("INFO: info");
    verifyNoMoreInteractions(stdOut, stdErr);
  }

  @Test
  void testWarn() {
    logs.warn("warn");
    verify(stdOut).println("WARN: warn");
    verifyNoMoreInteractions(stdOut, stdErr);
  }

  @Test
  void testWarnWithTimestamp() {
    logs.setDebugEnabled(true);
    logs.warn("warn");
    verify(stdOut).println(ArgumentMatchers.matches("\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d WARN: warn"));
    verifyNoMoreInteractions(stdOut, stdErr);
  }

  @Test
  void testError() {
    Exception e = new NullPointerException("exception");
    logs.error("error1");
    verify(stdErr).println("ERROR: error1");

    logs.error("error2", e);
    verify(stdErr).println("ERROR: error2");
    verify(stdErr).println(e);
    // other interactions to print the exception..
  }

  @Test
  void testDebug() {
    logs.setDebugEnabled(true);

    logs.debug("debug");
    verify(stdOut).println(ArgumentMatchers.matches("\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d DEBUG: debug$"));

    logs.setDebugEnabled(false);
    logs.debug("debug");
    verifyNoMoreInteractions(stdOut, stdErr);
  }

  @Test
  void should_forward_logs() {
    var mockedLogs = mock(Logs.class);
    var logOutput = new Logs.LogOutputAdapter(mockedLogs);

    String msg = "test";

    logOutput.log(msg, LogOutput.Level.DEBUG);
    verify(mockedLogs).debug(msg);
    verifyNoMoreInteractions(mockedLogs);
    reset(mockedLogs);

    logOutput.log(msg, LogOutput.Level.INFO);
    verify(mockedLogs).info(msg);
    verifyNoMoreInteractions(mockedLogs);
    reset(mockedLogs);

    logOutput.log(msg, LogOutput.Level.ERROR);
    verify(mockedLogs).error(msg);
    verifyNoMoreInteractions(mockedLogs);
    reset(mockedLogs);

    logOutput.log(msg, LogOutput.Level.WARN);
    verify(mockedLogs).warn(msg);
    verifyNoMoreInteractions(mockedLogs);
    reset(mockedLogs);

    logOutput.log(msg, LogOutput.Level.TRACE);
    verify(mockedLogs).debug(msg);
    verifyNoMoreInteractions(mockedLogs);
    reset(mockedLogs);
  }
}
