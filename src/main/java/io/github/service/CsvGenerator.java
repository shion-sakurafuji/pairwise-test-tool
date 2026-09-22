package io.github.service;

import java.util.List;

import io.github.model.Factor;

public class CsvGenerator {
        public String generate(
            List<Factor> factors,
            List<List<String>> testCases) {

                StringBuilder csv = new StringBuilder();

                for (int i = 0; i < factors.size(); i++) {

                    csv.append(factors.get(i).getName());

                    if (i < factors.size() - 1) {
                        csv.append(",");
                    }
                }
                
                csv.append("\n"); 
                
                for (List<String> testCase : testCases) {

                    for (int i = 0; i < testCase.size(); i++) {

                        csv.append(testCase.get(i));

                        if (i < testCase.size() - 1) {
                            csv.append(",");
                        }
                    }

                    csv.append("\n");
                }                
            
                return csv.toString();
            }
}
