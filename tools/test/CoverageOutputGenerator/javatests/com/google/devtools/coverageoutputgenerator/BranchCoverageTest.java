// Copyright 2018 The Bazel Authors. All rights reserved.
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

package com.google.devtools.coverageoutputgenerator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.base.VerifyException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@BranchCoverageData}. */
@RunWith(JUnit4.class)
public class BranchCoverageTest {

  @Test
  public void testNoBlockBranchSemantics() {
    BranchCoverage b0 = BranchCoverage.create(3, 0);
    BranchCoverage b1 = BranchCoverage.create(3, 1);
    BranchCoverage b2 = BranchCoverage.create(3, 2);

    assertThat(b0.lineNumber()).isEqualTo(3);
    assertThat(b0.evaluated()).isFalse();
    assertThat(b0.wasExecuted()).isFalse();
    assertThat(b0.nrOfExecutions()).isEqualTo(0);

    assertThat(b1.lineNumber()).isEqualTo(3);
    assertThat(b1.evaluated()).isTrue();
    assertThat(b1.wasExecuted()).isFalse();
    assertThat(b1.nrOfExecutions()).isEqualTo(1);

    assertThat(b2.lineNumber()).isEqualTo(3);
    assertThat(b2.evaluated()).isTrue();
    assertThat(b2.wasExecuted()).isTrue();
    assertThat(b2.nrOfExecutions()).isEqualTo(2);
  }

  @Test
  public void testFirstTransition() {
    int arbitraryLineNumber = 3;
    String arbitraryBlockNumber = "3", arbitraryBranchNumber = "2";
    BranchCoverage b0 = BranchCoverage.create(arbitraryLineNumber, 0);
    // FIRST CLASS REPRESENTATION
    // First node 
    assertThat(b0.lineNumber()).isEqualTo(3);
    assertThat(b0.evaluated()).isFalse();
    assertThat(b0.wasExecuted()).isFalse();
    // First transition
    BranchCoverage b1 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m0 = BranchCoverage.merge(b0, b1); 
    assertThat(m0.lineNumber()).isEqualTo(3);
    assertThat(m0.evaluated()).isTrue();
    assertThat(m0.wasExecuted()).isFalse();
    // SECOND CLASS REPRESENTATION
    // First node
    b0 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, false, 0);
    assertThat(b0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(b0.nrOfExecutions()).isEqualTo(0);
    // First Transition
    b1 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m0 = BranchCoverage.merge(b0, b1);
    assertThat(m0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m0.nrOfExecutions()).isEqualTo(1);
  }

  @Test
  public void testSecondTransition() {
    int arbitraryLineNumber = 3;
    String arbitraryBlockNumber = "3", arbitraryBranchNumber = "2";
    // FIRST CLASS REPRESENTATION
    BranchCoverage b0 = BranchCoverage.create(arbitraryLineNumber, 0);
    // First node 
    assertThat(b0.lineNumber()).isEqualTo(3);
    assertThat(b0.evaluated()).isFalse();
    assertThat(b0.wasExecuted()).isFalse();
    // Branch evaluated 
    BranchCoverage b1 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m0 = BranchCoverage.merge(b0, b1); 
    assertThat(m0.lineNumber()).isEqualTo(3);
    assertThat(m0.evaluated()).isTrue();
    assertThat(m0.wasExecuted()).isFalse();
    // Branch evaluated
    BranchCoverage b2 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m1 = BranchCoverage.merge(m0, b2); 
    assertThat(m1.lineNumber()).isEqualTo(3);
    assertThat(m1.evaluated()).isTrue();
    assertThat(m1.wasExecuted()).isFalse();
    // Branch evaluated
    BranchCoverage b3 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m2 = BranchCoverage.merge(m1, b3); 
    assertThat(m2.lineNumber()).isEqualTo(3);
    assertThat(m2.evaluated()).isTrue();
    assertThat(m2.wasExecuted()).isFalse();
    // SECOND CLASS REPRESENTATION
    // First node
    b0 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, false, 0);
    assertThat(b0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(b0.nrOfExecutions()).isEqualTo(0);
    // Branch evaluated
    b1 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m0 = BranchCoverage.merge(b0, b1);
    assertThat(m0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m0.nrOfExecutions()).isEqualTo(1);
    // Branch evaluated
    b2 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m1 = BranchCoverage.merge(m0, b2);
    assertThat(m1.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m1.nrOfExecutions()).isEqualTo(2);
    // Branch evaluated
    b3 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m2 = BranchCoverage.merge(m1, b3);
    assertThat(m2.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m2.nrOfExecutions()).isEqualTo(3);
  }

  @Test
  public void testThirdTransition() {
    int arbitraryLineNumber = 3;
    String arbitraryBlockNumber = "3", arbitraryBranchNumber = "2";
    BranchCoverage b0 = BranchCoverage.create(arbitraryLineNumber, 0);
    // FIRST CLASS REPRESENTATION
    // First node 
    assertThat(b0.lineNumber()).isEqualTo(3);
    assertThat(b0.evaluated()).isFalse();
    assertThat(b0.wasExecuted()).isFalse();
    // Branch evaluated
    BranchCoverage b1 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m0 = BranchCoverage.merge(b0, b1); 
    assertThat(m0.lineNumber()).isEqualTo(3);
    assertThat(m0.evaluated()).isTrue();
    assertThat(m0.wasExecuted()).isFalse();
    // Branch taken
    BranchCoverage b2 = BranchCoverage.create(arbitraryLineNumber, 2);
    BranchCoverage m1 = BranchCoverage.merge(m0, b2); 
    assertThat(m1.lineNumber()).isEqualTo(3);
    assertThat(m1.evaluated()).isTrue();
    assertThat(m1.wasExecuted()).isTrue();
    // SECOND CLASS REPRESENTATION
    // First node
    b0 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, false, 0);
    assertThat(b0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(b0.nrOfExecutions()).isEqualTo(0);
    // Branch evaluated
    b1 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m0 = BranchCoverage.merge(b0, b1);
    assertThat(m0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m0.nrOfExecutions()).isEqualTo(1);
    // Branch taken
    b2 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m1 = BranchCoverage.merge(m0, b2);
    assertThat(m1.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m1.nrOfExecutions()).isEqualTo(2);
  }

  @Test
  public void testFourthTransition() {
    int arbitraryLineNumber = 3;
    String arbitraryBlockNumber = "3", arbitraryBranchNumber = "2";
    BranchCoverage b0 = BranchCoverage.create(arbitraryLineNumber, 0);
    // FIRST CLASS REPRESENTATION
    // First node 
    assertThat(b0.lineNumber()).isEqualTo(3);
    assertThat(b0.evaluated()).isFalse();
    assertThat(b0.wasExecuted()).isFalse();
    // Branch evaluated
    BranchCoverage b1 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m0 = BranchCoverage.merge(b0, b1); 
    assertThat(m0.lineNumber()).isEqualTo(3);
    assertThat(m0.evaluated()).isTrue();
    assertThat(m0.wasExecuted()).isFalse();
    // Branch taken
    BranchCoverage b2 = BranchCoverage.create(arbitraryLineNumber, 2);
    BranchCoverage m1 = BranchCoverage.merge(m0, b2); 
    assertThat(m1.lineNumber()).isEqualTo(3);
    assertThat(m1.evaluated()).isTrue();
    assertThat(m1.wasExecuted()).isTrue();
    // Branch evaluated
    BranchCoverage b3 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m2 = BranchCoverage.merge(m1, b3); 
    assertThat(m2.lineNumber()).isEqualTo(3);
    assertThat(m2.evaluated()).isTrue();
    assertThat(m2.wasExecuted()).isTrue();
    // SECOND CLASS REPRESENTATION
    // First node
    b0 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, false, 0);
    assertThat(b0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(b0.nrOfExecutions()).isEqualTo(0);
    // Branch evaluated
    b1 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m0 = BranchCoverage.merge(b0, b1);
    assertThat(m0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m0.nrOfExecutions()).isEqualTo(1);
    // Branch taken
    b2 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m1 = BranchCoverage.merge(m0, b2);
    assertThat(m1.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m1.nrOfExecutions()).isEqualTo(2);
    // Branch evaluated
    b3 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m2 = BranchCoverage.merge(m1, b3);
    assertThat(m2.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m2.nrOfExecutions()).isEqualTo(3);
  }

  @Test
  public void testFifthTransition() {
    int arbitraryLineNumber = 3;
    String arbitraryBlockNumber = "3", arbitraryBranchNumber = "2";
    BranchCoverage b0 = BranchCoverage.create(arbitraryLineNumber, 0);
    // FIRST CLASS REPRESENTATION
    // First node 
    assertThat(b0.lineNumber()).isEqualTo(3);
    assertThat(b0.evaluated()).isFalse();
    assertThat(b0.wasExecuted()).isFalse();
    // Branch evaluated
    BranchCoverage b1 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m0 = BranchCoverage.merge(b0, b1); 
    assertThat(m0.lineNumber()).isEqualTo(3);
    assertThat(m0.evaluated()).isTrue();
    assertThat(m0.wasExecuted()).isFalse();
    // Branch taken
    BranchCoverage b2 = BranchCoverage.create(arbitraryLineNumber, 2);
    BranchCoverage m1 = BranchCoverage.merge(m0, b2); 
    assertThat(m1.lineNumber()).isEqualTo(3);
    assertThat(m1.evaluated()).isTrue();
    assertThat(m1.wasExecuted()).isTrue();
    // Branch evaluated
    BranchCoverage b3 = BranchCoverage.create(arbitraryLineNumber, 1);
    BranchCoverage m2 = BranchCoverage.merge(m1, b3); 
    assertThat(m2.lineNumber()).isEqualTo(3);
    assertThat(m2.evaluated()).isTrue();
    assertThat(m2.wasExecuted()).isTrue();
    // Branch taken
    BranchCoverage b4 = BranchCoverage.create(arbitraryLineNumber, 2);
    BranchCoverage m3 = BranchCoverage.merge(m2, b4); 
    assertThat(m3.lineNumber()).isEqualTo(3);
    assertThat(m3.evaluated()).isTrue();
    assertThat(m3.wasExecuted()).isTrue();
    // SECOND CLASS REPRESENTATION
    // First node
    b0 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, false, 0);
    assertThat(b0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(b0.nrOfExecutions()).isEqualTo(0);
    // Branch evaluated
    b1 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m0 = BranchCoverage.merge(b0, b1);
    assertThat(m0.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m0.nrOfExecutions()).isEqualTo(1);
    // Branch taken
    b2 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m1 = BranchCoverage.merge(m0, b2);
    assertThat(m1.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m1.nrOfExecutions()).isEqualTo(2);
    // Branch evaluated
    b3 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m2 = BranchCoverage.merge(m1, b3);
    assertThat(m2.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m2.nrOfExecutions()).isEqualTo(3);
    // Branch taken
    b4 = BranchCoverage.createWithBlockAndBranch(arbitraryLineNumber, arbitraryBlockNumber, arbitraryBranchNumber, true, 1);
    m3 = BranchCoverage.merge(m2, b4);
    assertThat(m3.lineNumber()).isEqualTo(arbitraryLineNumber);
    assertThat(m3.nrOfExecutions()).isEqualTo(4);
  }

  @Test
  public void testNoBlockBranchInvalidValuesFail() {
    assertThrows(VerifyException.class, () -> BranchCoverage.create(3, -1));
    assertThrows(VerifyException.class, () -> BranchCoverage.create(3, 4));
  }

  @Test
  public void testMergesBranchesWithBlockBranchEvaluated() {
    BranchCoverage b1 = BranchCoverage.createWithBlockAndBranch(1, "3", "2", true, 3);
    BranchCoverage b2 = BranchCoverage.createWithBlockAndBranch(1, "3", "2", true, 0);
    BranchCoverage b3 = BranchCoverage.createWithBlockAndBranch(1, "3", "2", true, 2);

    BranchCoverage m1 = BranchCoverage.merge(b1, b2);
    BranchCoverage m2 = BranchCoverage.merge(m1, b3);

    assertThat(m1.lineNumber()).isEqualTo(1);
    assertThat(m1.blockNumber()).isEqualTo("3");
    assertThat(m1.branchNumber()).isEqualTo("2");
    assertThat(m1.evaluated()).isTrue();
    assertThat(m1.nrOfExecutions()).isEqualTo(3);
    assertThat(m2.evaluated()).isTrue();
    assertThat(m2.nrOfExecutions()).isEqualTo(5);
  }

  @Test
  public void testMergeBranchesWithBlockBranchNotEvaluated() {
    BranchCoverage b1 = BranchCoverage.createWithBlockAndBranch(1, "2", "0", false, 0);
    BranchCoverage b2 = BranchCoverage.createWithBlockAndBranch(1, "2", "0", false, 0);

    BranchCoverage merged = BranchCoverage.merge(b1, b2);

    assertThat(merged.lineNumber()).isEqualTo(1);
    assertThat(merged.blockNumber()).isEqualTo("2");
    assertThat(merged.branchNumber()).isEqualTo("0");
    assertThat(merged.evaluated()).isFalse();
    assertThat(merged.nrOfExecutions()).isEqualTo(0);
  }

  @Test
  public void testMergeBranchesWithBlockBranchMixedEvaluated() {
    BranchCoverage b1 = BranchCoverage.createWithBlockAndBranch(1, "2", "0", false, 0);
    BranchCoverage b2 = BranchCoverage.createWithBlockAndBranch(1, "2", "0", true, 0);

    BranchCoverage merged = BranchCoverage.merge(b1, b2);

    assertThat(merged.lineNumber()).isEqualTo(1);
    assertThat(merged.blockNumber()).isEqualTo("2");
    assertThat(merged.branchNumber()).isEqualTo("0");
    assertThat(merged.evaluated()).isTrue();
    assertThat(merged.nrOfExecutions()).isEqualTo(0);
  }

  @Test
  public void testMergesWithNoBlockBranch() {
    BranchCoverage b1 = BranchCoverage.create(3, 1);
    BranchCoverage b2 = BranchCoverage.create(3, 0);
    BranchCoverage b3 = BranchCoverage.create(3, 2);

    BranchCoverage m1 = BranchCoverage.merge(b1, b2);
    BranchCoverage m2 = BranchCoverage.merge(m1, b3);

    assertThat(m1.nrOfExecutions()).isEqualTo(1);
    assertThat(m1.wasExecuted()).isFalse();
    assertThat(m1.evaluated()).isTrue();
    assertThat(m2.lineNumber()).isEqualTo(3);
    assertThat(m2.blockNumber()).isEmpty();
    assertThat(m2.branchNumber()).isEmpty();
    assertThat(m2.nrOfExecutions()).isEqualTo(2);
    assertThat(m2.wasExecuted()).isTrue();
    assertThat(m2.evaluated()).isTrue();
  }

  @Test
  public void testDifferentLineNumbersFail() {
    BranchCoverage b1 = BranchCoverage.create(2, 1);
    BranchCoverage b2 = BranchCoverage.create(3, 2);
    assertThrows(VerifyException.class, () -> BranchCoverage.merge(b1, b2));
  }

  @Test
  public void testDifferentBlockNumbersFail() {
    BranchCoverage b1 = BranchCoverage.createWithBlockAndBranch(1, "3", "2", true, 1);
    BranchCoverage b2 = BranchCoverage.createWithBlockAndBranch(1, "2", "2", true, 1);
    assertThrows(VerifyException.class, () -> BranchCoverage.merge(b1, b2));
  }

  @Test
  public void testDifferentBranchNumbersFail() {
    BranchCoverage b1 = BranchCoverage.createWithBlockAndBranch(1, "3", "2", true, 1);
    BranchCoverage b2 = BranchCoverage.createWithBlockAndBranch(1, "3", "3", true, 1);
    assertThrows(VerifyException.class, () -> BranchCoverage.merge(b1, b2));
  }
}
