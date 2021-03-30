
# <a target="_blank" href="https://www.sciencedirect.com/science/article/abs/pii/S0167739X18320867"> Relevance-based approach for Big Data Exploration</a>

The collection, organisation and analysis of large amount of data (Big Data) in different application domains still require the involvement of experts for the identification of relevant data only, without being overwhelmed by volume, velocity and variety of collected data. According to the “Human-In-the-Loop Data Analysis” vision, experts explore data to take decisions in unexpected situations, based on their long-term experience. In this paper, the IDEAaS (Interactive Data Exploration As-a-Service) approach is presented, apt to enable Big Data Exploration (BDE) according to data relevance. In the approach, novel techniques have been developed: (i) an incremental clustering algorithm, to provide summarised representation of collected data streams; (ii) multi-dimensional organisation of summarised data, for data exploration according to different analysis dimensions; (iii) data relevance evaluation techniques, to attract the experts’ attention on relevant data only during exploration. The approach has been experimented to apply BDE for state detection in the Industry 4.0 domain, given the strategic importance of Big Data management as enabling technology in this field. In particular, a stream of numeric features is collected from a Cyber Physical System and is explored to monitor the system health status, supporting the identification of unknown anomalous conditions. Results of an extensive experimentation in the Industry 4.0 domain are presented in the paper and demonstrated the effectiveness of developed techniques to attract the attention of experts on relevant data, also beyond the considered domain, in presence of disruptive characteristics of Big Data, namely volume (millions of collected records), velocity (measured in milliseconds) and variety (number and heterogeneity of analysis dimensions).

# Build

```
cd ideaas-root
mvn clean install
```

