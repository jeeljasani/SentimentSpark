package org.example;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class ReutersWordCount{
    public static void main(String[] args) {

        String filePath = "./StopWords.csv";
        List<String> stopWords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into words
                String[] lineWords = line.split(",");

                for (String word : lineWords) {
                    stopWords.add(word.toLowerCase().trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            StringBuilder allArticles = new StringBuilder();

            // Read the SGM file using Jsoup
            File inputFile = new File("./reut2-009.sgm");
            org.jsoup.nodes.Document doc = Jsoup.parse(inputFile,"UTF-8", "", Parser.xmlParser());
            Elements reuters = doc.select("REUTERS");
            System.out.println("Processing " + reuters.size() + " articles...");

            // Process each Reuters article
            for (Element article : reuters) {
                Element titleEle = article.selectFirst("TITLE");
                String title = titleEle != null ? titleEle.text() : "";
                Element bodyEle = article.selectFirst("BODY");
                String body = bodyEle != null ? bodyEle.text() : "";

                if (!body.isEmpty() && !title.isEmpty()) {
                    String formattedArticle = String.format("%s %s", title, body);
                    allArticles.append(formattedArticle);

                }
            }
            String[] finalWords = allArticles.toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");

            List<String> filteredWords = new ArrayList<>();
            for (String word : finalWords) {
                if (!stopWords.contains(word)) {
                    filteredWords.add(word);
                }
            }
            // Print the filtered result
            System.out.println("Filtered words: " + String.join(" ", filteredWords));
            String finalInput = String.join(" ", filteredWords);

            // Configure Spark
            SparkConf conf = new SparkConf()
                    .setAppName("WordFrequencyCount")
                    .setMaster("local[*]");
            JavaSparkContext sc = new JavaSparkContext(conf);

            JavaRDD<String> wordsRDD = sc.parallelize(Arrays.asList(finalInput.toLowerCase().split("\\W+")));

            // Count word frequencies
            Map<String, Long> wordCounts = wordsRDD
                    .filter(word -> !word.isEmpty())
                    .countByValue();

            List<Map.Entry<String, Long>> sortedWordCounts = wordCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            System.out.println("Word Frequencies:");
            for (Map.Entry<String, Long> entry : sortedWordCounts) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Error processing news data: " + e.getMessage());
            e.printStackTrace();
        }


    }
}