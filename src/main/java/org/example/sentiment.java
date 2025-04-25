package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.util.HashSet;
import java.util.Set;

public class sentiment {
    public static void main(String[] args) {
        String positiveFilePath = "D:\\Data Assignment 2\\NewsRead\\src\\main\\java\\org\\example\\positive.csv";
        String negativeFilePath = "D:\\Data Assignment 2\\NewsRead\\src\\main\\java\\org\\example\\negative.csv";

        List<String> positiveWords = readWordsFromFile(positiveFilePath);
        List<String> negativeWords = readWordsFromFile(negativeFilePath);

        try {
            File inputFile = new File("D:\\Data Assignment 2\\NewsRead\\src\\main\\java\\org\\example\\reut2-009.sgm");
            org.jsoup.nodes.Document doc = Jsoup.parse(inputFile, "UTF-8", "", Parser.xmlParser());

            Elements reuters = doc.select("REUTERS");
            System.out.println("Processing " + reuters.size() + " articles...");

            List<SentimentResult> results = new ArrayList<>();
            int positiveCount = 0;
            int negativeCount = 0;
            int neutralCount = 0;

            for (int i = 0; i < reuters.size(); i++) {
                Element article = reuters.get(i);
                Element titleEle = article.selectFirst("TITLE");
                String title = titleEle != null ? titleEle.text() : "";

                if (!title.isEmpty()) {
                    String[] titleWords = title.split(" ");
                    long score = 0;
                    Set<String> matches = new HashSet<>();

                    for (String word : titleWords) {
                        String lowerWord = word.toLowerCase();
                        if (positiveWords.contains(lowerWord)) {
                            score++;
                            matches.add(word);
                        } else if (negativeWords.contains(lowerWord)) {
                            score--;
                            matches.add(word);
                        }
                    }

                    String sentiment = "Neutral";
                    if (score > 0) {
                        sentiment = "Positive";
                        positiveCount++;
                    } else if (score < 0) {
                        sentiment = "Negative";
                        negativeCount++;
                    } else {
                        neutralCount++;
                    }

                    results.add(new SentimentResult(title, i + 1, score, sentiment, matches));
                }
            }

            for (SentimentResult result : results) {
                System.out.println(result);
            }

            // Print counts
            System.out.println("\nTotal articles analyzed: " + results.size());
            System.out.println("Positive: " + positiveCount);
            System.out.println("Negative: " + negativeCount);
            System.out.println("Neutral: " + neutralCount);

            // Save results to CSV in the new format
            saveResultsToCSVWithMatches(results, "D:\\Data Assignment 2\\NewsRead\\sentiment_results_extended 1.csv");

        } catch (Exception e) {
            System.err.println("Error processing news data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> readWordsFromFile(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineWords = line.split(",");
                for (String word : lineWords) {
                    words.add(word.toLowerCase().trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private static void saveResultsToCSVWithMatches(List<SentimentResult> results, String outputFilePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFilePath))) {
            pw.println("News#,Title Content,match,score,Polarity");
            for (SentimentResult result : results) {
                String matches = String.join(", ", result.getMatches());
                pw.printf("%d,\"%s\",\"%s\",%d,%s%n",
                        result.getId(),
                        result.getTitle().replace("\"", "\"\""), // Escape quotes for CSV
                        matches,
                        result.getScore(),
                        result.getSentiment());
            }
            System.out.println("Extended results saved to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error saving extended results to CSV: " + e.getMessage());
        }
    }
}

class SentimentResult {
    private final String title;
    private final long id;
    private final long score;
    private final String sentiment;
    private final Set<String> matches;

    public SentimentResult(String title, long id, long score, String sentiment, Set<String> matches) {
        this.title = title;
        this.id = id;
        this.score = score;
        this.sentiment = sentiment;
        this.matches = matches;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public long getScore() {
        return score;
    }

    public String getSentiment() {
        return sentiment;
    }

    public Set<String> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Title: \"%s\", Matches: [%s], Score: %d, Sentiment: %s",
                id, title, String.join(", ", matches), score, sentiment);
    }
}
