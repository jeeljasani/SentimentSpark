package org.example;

import com.mongodb.client.*;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class NewsRead {
    private static final String MONGODB_URI = "mongodb+srv://jasanijeel2772:root@assignment2.mskxx.mongodb.net/";
    private static final String DATABASE_NAME = "RawDb";
    private static final String COLLECTION_NAME = "news";

    public static void main(String[] args) {
        try {

            MongoClient mongoClient = MongoClients.create(MONGODB_URI);
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            // Read the SGM file using Jsoup
            File inputFile = new File("D:\\Data Assignment 2\\NewsRead\\src\\main\\java\\org\\example\\reut2-009.sgm");
            org.jsoup.nodes.Document doc = Jsoup.parse(inputFile,"UTF-8", "", Parser.xmlParser());
            Elements reuters = doc.select("REUTERS");

            System.out.println("Processing " + reuters.size() + " articles...");

            // Process each Reuters article
            for (Element article : reuters) {

                Element titleEle = article.selectFirst("TITLE");
                String title = titleEle != null ? titleEle.text() : "";
                Element bodyEle = article.selectFirst("BODY");
                String body = bodyEle != null ? bodyEle.text() : "";
                // Only insert if body is not empty
                if (!body.isEmpty() && !title.isEmpty()) {
                    Document document = new Document()
                            .append("title", title)
                            .append("body", body)
                            .append("metadata", new Document()
                                    .append("newid", article.attr("NEWID"))
                                    .append("date", article.attr("DATE"))
                                    .append("topics", article.attr("TOPICS")));

                    // Insert into MongoDB
                    collection.insertOne(document);
                }
            }

            System.out.println("Data import completed successfully!");
            mongoClient.close();

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing news data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}