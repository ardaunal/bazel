// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.shell;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;
import java.util.Optional;

import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link TerminationStatus}. */
@RunWith(JUnit4.class)
public final class TerminationStatusTest {

  @Test
  public void testCrashed_exitCodesReturnFalse() {
    assertThat(TerminationStatus.crashed(0)).isFalse();
    assertThat(TerminationStatus.crashed(1)).isFalse();
    assertThat(TerminationStatus.crashed(127)).isFalse();
  }

  @Test
  public void testConstructor() {
    TerminationStatus terminationStatus = new TerminationStatus(1, false);
    assertThat(terminationStatus.getRawExitCode()).isEqualTo(1);
    assertThat(terminationStatus.timedOut()).isFalse();
  }

  @Test
  public void testHashCode() {
    TerminationStatus terminationStatus = new TerminationStatus(1, false);
    assertThat(terminationStatus.hashCode()).isEqualTo(1);
  }

  @Test
  public void testGetExitCode() {
    TerminationStatus terminationStatus =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    Exception exception = assertThrows(
        IllegalStateException.class, () -> terminationStatus.getExitCode());
  }

  @Test
  public void testGetTerminatingSignal() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    Exception exception = assertThrows(
        IllegalStateException.class, () -> terminationStatus1.getTerminatingSignal());

    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    exception = assertThrows(
        IllegalStateException.class, () -> terminationStatus2.getTerminatingSignal());

  }

  @Test
  public void testSuccess() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus1.success()).isTrue();

    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus2.success()).isFalse();

    TerminationStatus terminationStatus3 = new TerminationStatus(1, false);
    assertThat(terminationStatus3.success()).isFalse();
  }

  @Test
  public void testExited() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus1.exited()).isTrue();

    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus2.exited()).isFalse();

    TerminationStatus terminationStatus3 =
        TerminationStatus.builder()
            .setWaitResponse(193)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus3.exited()).isTrue();

    TerminationStatus terminationStatus4 =
        TerminationStatus.builder()
            .setWaitResponse(129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus4.exited()).isFalse();
  }

  @Test
  public void testTimedOut() {
    TerminationStatus terminationStatus =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus.timedOut()).isTrue();
  }

  //@Test
  //public void testCrashed_exitCodesReturnFalse() {
  //  assertThat(TerminationStatus.crashed(0)).isFalse();
  //  assertThat(TerminationStatus.crashed(1)).isFalse();
  //  assertThat(TerminationStatus.crashed(127)).isFalse();
  //}

  @Test
  public void testCrashed_terminationSignalsReturnFalse() {
    assertThat(TerminationStatus.crashed(TerminationStatus.SIGNAL_1)).isFalse();
    assertThat(TerminationStatus.crashed(TerminationStatus.SIGNAL_63)).isFalse();
    assertThat(TerminationStatus.crashed(TerminationStatus.SIGNAL_SIGKILL)).isFalse();
    assertThat(TerminationStatus.crashed(TerminationStatus.SIGNAL_SIGTERM)).isFalse();
  }

  @Test
  public void testCrashed_abruptSignalsReturnTrue() {
    assertThat(TerminationStatus.crashed(TerminationStatus.SIGNAL_SIGABRT)).isTrue();
    assertThat(TerminationStatus.crashed(TerminationStatus.SIGNAL_SIGBUS)).isTrue();
  }

  @Test
  public void testBuilder_withNoWaitResponse() {
    assertThrows(
        IllegalStateException.class, () -> TerminationStatus.builder().setTimedOut(false).build());
  }

  @Test
  public void testBuilder_withNoTimedOut() {
    assertThrows(
        IllegalStateException.class, () -> TerminationStatus.builder().setWaitResponse(0).build());
  }

  @Test
  public void testBuilder_withNoExecutionTime() {
    TerminationStatus terminationStatus =
        TerminationStatus.builder().setWaitResponse(0).setTimedOut(false).build();
    assertThat(terminationStatus.getWallExecutionTime()).isEmpty();
    assertThat(terminationStatus.getUserExecutionTime()).isEmpty();
    assertThat(terminationStatus.getSystemExecutionTime()).isEmpty();
  }

  @Test
  public void testBuilder_withExecutionTime() {
    TerminationStatus terminationStatus =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    assertThat(terminationStatus.getWallExecutionTime()).isPresent();
    assertThat(terminationStatus.getWallExecutionTime()).hasValue(Duration.ofMillis(1929));
    assertThat(terminationStatus.getUserExecutionTime()).isPresent();
    assertThat(terminationStatus.getUserExecutionTime()).hasValue(Duration.ofMillis(1492));
    assertThat(terminationStatus.getSystemExecutionTime()).isPresent();
    assertThat(terminationStatus.getSystemExecutionTime()).hasValue(Duration.ofMillis(1787));
  }

  @Test
  public void testBuilder_withExecutionTime2() {
    Optional<Duration> wallExecutionTime = null;
    Optional<Duration> userExecutionTime = null;
    Optional<Duration> systemExecutionTime = null;

    TerminationStatus terminationStatus =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(wallExecutionTime)
            .setUserExecutionTime(userExecutionTime)
            .setSystemExecutionTime(systemExecutionTime)
            .build();
    assertThat(terminationStatus.getWallExecutionTime()).isNull();
    assertThat(terminationStatus.getUserExecutionTime()).isNull();
    assertThat(terminationStatus.getSystemExecutionTime()).isNull();
  }

  @Test
  public void testEquals() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    TerminationStatus terminationStatus3 =
        TerminationStatus.builder()
            .setWaitResponse(1)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.equals(3)).isFalse();
    assertThat(terminationStatus1.equals(terminationStatus2)).isTrue();
    assertThat(terminationStatus1.equals(terminationStatus3)).isFalse();
  }

  @Test
  public void testToStringExited() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.toString().equals(terminationStatus2.toString())).isTrue();
  }

  @Test
  public void testToStringTimedOut() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.toString().equals(terminationStatus2.toString())).isTrue();
  }

  @Test
  public void testToStringTerminated() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.toString().equals(terminationStatus2.toString())).isTrue();
  }

  @Test
  public void testToShortStringExited() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.toShortString().equals(terminationStatus2.toShortString())).isTrue();
  }

  @Test
  public void testToShortStringTimedOut() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(0)
            .setTimedOut(true)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.toShortString().equals(terminationStatus2.toShortString())).isTrue();
  }

  @Test
  public void testToShortStringTerminated() {
    TerminationStatus terminationStatus1 =
        TerminationStatus.builder()
            .setWaitResponse(129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus2 =
        TerminationStatus.builder()
            .setWaitResponse(129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus1.toShortString().equals(terminationStatus2.toShortString())).isTrue();

    TerminationStatus terminationStatus3 =
        TerminationStatus.builder()
            .setWaitResponse(11129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus4 =
        TerminationStatus.builder()
            .setWaitResponse(11129)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus3.toShortString().equals(terminationStatus4.toShortString())).isTrue();

    TerminationStatus terminationStatus5 =
        TerminationStatus.builder()
            .setWaitResponse(191)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();
    
    TerminationStatus terminationStatus6 =
        TerminationStatus.builder()
            .setWaitResponse(191)
            .setTimedOut(false)
            .setWallExecutionTime(Duration.ofMillis(1929))
            .setUserExecutionTime(Duration.ofMillis(1492))
            .setSystemExecutionTime(Duration.ofMillis(1787))
            .build();

    assertThat(terminationStatus5.toShortString().equals(terminationStatus6.toShortString())).isTrue();

  }
}
