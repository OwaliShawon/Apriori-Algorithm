import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AprioriAlgorithm {

    // Function to calculate the support count of itemsets
    public static List<Set<String>> calculateSupport(List<Set<String>> transactions, List<Set<String>> candidates, int minSupport, Map<Set<String>, Integer> supportCount) {
        List<Set<String>> frequentItemsets = new ArrayList<>();

        for (Set<String> candidate : candidates) {
            int count = 0;
            for (Set<String> transaction : transactions) {
                if (transaction.containsAll(candidate)) {
                    count++;
                }
            }

            if (count >= minSupport) {
                frequentItemsets.add(candidate);
                supportCount.put(candidate, count);
            }
        }

        return frequentItemsets;
    }

    // Function to generate candidates of the next level from frequent itemsets
    public static List<Set<String>> generateCandidates(List<Set<String>> frequentItemsets) {
        List<Set<String>> candidates = new ArrayList<>();

        for (int i = 0; i < frequentItemsets.size(); i++) {
            for (int j = i + 1; j < frequentItemsets.size(); j++) {
                Set<String> candidate = new HashSet<>(frequentItemsets.get(i));
                candidate.addAll(frequentItemsets.get(j));

                if (candidate.size() == frequentItemsets.get(0).size() + 1) {
                    candidates.add(candidate);
                }
            }
        }

        // Remove duplicate candidates
        candidates = candidates.stream().distinct().collect(Collectors.toList());

        return candidates;
    }

    // Function to read transactions from a text file
    public static List<Set<String>> readTransactions(String filename) {
        List<Set<String>> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                Set<String> transaction = new HashSet<>(Arrays.asList(line.split(",")));
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public static void main(String[] args) {
        String filename = "transactions.txt";
        List<Set<String>> transactions = readTransactions(filename);

        // Minimum support threshold
        int minSupport = 2;

        // Generate frequent itemsets of size 1
        Set<String> items = new HashSet<>();
        for (Set<String> transaction : transactions) {
            items.addAll(transaction);
        }

        List<Set<String>> candidates = new ArrayList<>();
        for (String item : items) {
            candidates.add(new HashSet<>(Collections.singleton(item)));
        }

        Map<Set<String>, Integer> supportCount = new HashMap<>();
        List<Set<String>> frequentItemsets = calculateSupport(transactions, candidates, minSupport, supportCount);

        // Iteratively find frequent itemsets of larger sizes
        while (!frequentItemsets.isEmpty()) {
            System.out.println("Frequent Itemsets of size " + frequentItemsets.get(0).size() + ":");
            for (Set<String> itemset : frequentItemsets) {
                System.out.print("{ ");
                for (String item : itemset) {
                    System.out.print(item + " ");
                }
                System.out.println("}: " + supportCount.get(itemset));
            }

            // Generate candidates for the next level
            candidates = generateCandidates(frequentItemsets);

            // Calculate support for the new candidates
            frequentItemsets = calculateSupport(transactions, candidates, minSupport, supportCount);
        }
    }
}
