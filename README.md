# 📰 NewsSense

**NewsSense** is a big data analytics pipeline focused on processing and analyzing Reuters news articles using MongoDB and Apache Spark. This project demonstrates efficient data ingestion, transformation, word frequency analysis, and sentiment classification through scalable, cloud-compatible technologies.

---

## 🚀 Project Overview

The goal of NewsSense is to extract, clean, and analyze textual data from Reuters SGML news articles and derive insights such as frequent keywords and sentiment orientation. The project includes three major components:

1. **Data Ingestion** – Parse and clean Reuters `.sgm` files, storing relevant fields (title, body) in MongoDB.
2. **Spark Processing** – Use Apache Spark to perform word count and frequency analysis on news content.
3. **Sentiment Analysis** – Implement a Bag-of-Words (BoW) approach using positive and negative word lists to classify news titles as Positive, Negative, or Neutral.

---

## ⚙️ Tech Stack

| Component            | Technology Used        |
|----------------------|------------------------|
| Language             | Java                   |
| Text Parsing         | JSoup (HTML/XML parser)|
| NoSQL Database       | MongoDB Atlas          |
| Big Data Processing  | Apache Spark (Standalone & GCP) |
| Cloud Storage        | Google Cloud Platform (GCP) |
| Sentiment Lexicon    | Custom Word Lists      |

---

## 🧱 Features

- 📥 **Reuters File Ingestion**: Efficient parsing of `.sgm` files using JSoup
- 📦 **MongoDB Storage**: Structured storage of cleaned article data in the cloud
- ⚡ **Spark Word Count**: Distributed word frequency computation
- 💬 **BoW Sentiment Analysis**: Classification of article titles using predefined sentiment dictionaries
- 🧹 **Data Cleaning Pipeline**: Stop word removal, special character filtering, case normalization

---
## 🛠️ Setup Instructions

### 1. MongoDB Setup
- Create a MongoDB Atlas cluster
- Add your IP to network access
- Create a database named `RawDb` and a collection named `news`

### 2. Reuters File Parsing
```bash
# Java app using JSoup
java -jar NewsSenseParser.jar

