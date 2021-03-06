package org.testpackage.optimization;

import com.google.common.collect.Sets;
import com.googlecode.javaewah.datastructure.BitSet;

import java.util.List;
import java.util.Set;

/**
 * @author richardnorth
 */
public class TestSubsetOptimizerResult {
    private final List<TestWithCoverage> selections;
    private final BitSet covered;
    private final long numProbePoints;
    private final Set<String> selectedTestNames;
    private Long cost = 0L;

    public TestSubsetOptimizerResult(List<TestWithCoverage> selections, BitSet covered, long numProbePoints) {

        this.selections = selections;
        this.covered = covered;
        this.numProbePoints = numProbePoints;
        this.selectedTestNames = Sets.newHashSet();

        for (TestWithCoverage testWithCoverage : selections) {
            this.selectedTestNames.add(testWithCoverage.getId());
            this.cost += testWithCoverage.getCost();
        }
    }

    public List<TestWithCoverage> getSelections() {
        return selections;
    }

    public BitSet getCovered() {
        return covered;
    }

    public int getCoveredLines() {
        return covered.cardinality();
    }

    public boolean containsTestName(String testName) {
        return this.selectedTestNames.contains(testName);
    }

    public String describe() {
        return String.format("%d tests with %2.1f%% coverage and %dms expected execution time",
                this.selectedTestNames.size(),
                ((double) this.covered.cardinality() / numProbePoints) * 100,
                this.cost);
    }
}
