package io.github.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import io.github.model.Factor;

public class PairwiseGenerator {
    //複数のテストケースをLiskとして返す
    //1件のテストケースは、各因子から選ばれた水準をList<String>で保持する
    public List<List<String>> generate(List<Factor> factors) {
        //最終的に排出するテストケース
        List<List<String>> testCases = new ArrayList<>();

// ================================
// ① 全組合せ候補を作る
// ================================

        List<List<String>>candidates = new ArrayList<>();
        createCandidates(factors, 0, new ArrayList<>(), candidates);

// ================================
// ② 網羅すべき全ペアを作る
// ================================
        Set<List<String>> uncoveredPairs = new HashSet<>();

        for (int i = 0;i <factors.size(); i++){
            for (int j = i + 1 ;j < factors.size(); j++){
                for (String firstLevel : factors.get(i).getLevels()){
                    for (String secondLevel : factors.get(j).getLevels()){                        
                        uncoveredPairs.add(
                            List.of(
                                String.valueOf(i),
                                firstLevel,
                                String.valueOf(j),
                                secondLevel
                            )
                        );
                    }
                }
            }
        }

// ================================
// ③ 全ペアを網羅するまで候補選択を繰り返す
// ================================

        while (!uncoveredPairs.isEmpty()){
            List<String> bestCandidate = null;
            int bestScore = 0;

    // ================================
    // ④ 各候補を採点し、一番よい候補を選ぶ
    // ================================
            for (List<String> candidate : candidates){
                int score = countUncoveredPairs(candidate, uncoveredPairs);

                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                }
            }

            if (bestCandidate == null){
                break;
            }
    // ================================
    // ⑤ 一番よい候補を正式なテストケースに採用
    // ================================

            testCases.add(new ArrayList<>(bestCandidate));
    // ================================
    // ⑥ 採用したケースで網羅したペアを削除
    // ================================
            removeCoveredPairs(bestCandidate, uncoveredPairs);
            candidates.remove(bestCandidate);       
        }
        return testCases;
    }

// ================================
// 補助処理A：全組合せ候補を作る
// ================================
    private void createCandidates(
            List<Factor> factors,
            int factorIndex,
            List<String> current,
            List<List<String>> candidates) {

        if (factorIndex == factors.size()) {
            candidates.add(new ArrayList<>(current));
            return;
        }

        for (String level : factors.get(factorIndex).getLevels()) {

            current.add(level);

            createCandidates(
                factors,
                factorIndex + 1,
                current,
                candidates
            );

            current.remove(current.size() - 1);
        }
    }

// ================================
// 補助処理B：候補1件が未網羅ペアを何個持つか数える
// ================================
    private int countUncoveredPairs(
            List<String> candidate,
            Set<List<String>> uncoveredPairs) {

        int count = 0;

        for (int i = 0; i < candidate.size(); i++) {

            for (int j = i + 1; j < candidate.size(); j++) {

                List<String> pair = List.of(
                    String.valueOf(i),
                    candidate.get(i),
                    String.valueOf(j),
                    candidate.get(j)
                );

                if (uncoveredPairs.contains(pair)) {
                    count++;
                }
            }
        }

        return count;
    }

// ================================
// 補助処理C：採用済みペアを未網羅一覧から削除する
// ================================
    private void removeCoveredPairs(List<String> candidate,Set<List<String>> uncoveredPairs) {

        for (int i = 0; i < candidate.size(); i++) {
            for (int j = i + 1; j < candidate.size(); j++) {

                List<String> pair = List.of(
                    String.valueOf(i),
                    candidate.get(i),
                    String.valueOf(j),
                    candidate.get(j)
                );

                uncoveredPairs.remove(pair);
            }
        }
    }
}
