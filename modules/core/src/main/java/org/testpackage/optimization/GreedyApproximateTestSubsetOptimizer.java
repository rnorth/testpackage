package org.testpackage.optimization;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author richardnorth
 */
public class GreedyApproximateTestSubsetOptimizer {

    private Integer targetTestCount;
    private Double targetCoverage;

    public TestSubsetOptimizerResult solve(Set<TestWithCoverage> coverageSets) {

        List<TestWithCoverage> remainingCandidates = newArrayList(coverageSets);
        List<TestWithCoverage> selections = newArrayList();
        BitSet covered = new BitSet();

        if (targetTestCount != null) {
            solveForTargetTestCount(remainingCandidates, selections, covered);
        } else if (targetCoverage != null) {
            solveForTargetCoverage(remainingCandidates, selections, covered);
        } else {
            throw new IllegalStateException("A target test count or coverage must be set");
        }


        return new TestSubsetOptimizerResult(selections, covered);
    }

    private void solveForTargetCoverage(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        double coverage = 0;
        while (coverage < targetCoverage && !remainingCandidates.isEmpty()) {

            search(remainingCandidates, selections, covered);
            coverage = ((double) covered.cardinality()) / covered.size();
        }
    }

    private void solveForTargetTestCount(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        for (int i = 0; i < targetTestCount; i++) {
            search(remainingCandidates, selections, covered);
        }
    }

    private void search(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        double bestScore = 0;
        TestWithCoverage bestCandidate = null;

        // Score each candidate
        for (int j = 0; j < remainingCandidates.size(); j++) {
            final TestWithCoverage candidate = remainingCandidates.get(j);
            BitSet candidateCoverage = candidate.getCoverage();

            // work out which lines of code get freshly covered
            covered.xor(candidateCoverage);

            // work out the score, the number of newly covered lines
            double score = ((double) covered.cardinality()) / candidate.getCost();

            // revert covered back to its previous state
            covered.xor(candidateCoverage);

            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
            }
        }

        if (bestCandidate != null) {
            // Remove the best candidate from future evaluation and add it to the coverage achieved
            remainingCandidates.remove(bestCandidate);
            covered.or(bestCandidate.getCoverage());
            selections.add(bestCandidate);
        }
    }

    public GreedyApproximateTestSubsetOptimizer withTargetTestCount(int targetTestCount) {
        this.targetTestCount = targetTestCount;
        return this;
    }

    public GreedyApproximateTestSubsetOptimizer withTargetTestCoverage(double targetCoverage) {
        this.targetCoverage = targetCoverage;
        return this;
    }
}
