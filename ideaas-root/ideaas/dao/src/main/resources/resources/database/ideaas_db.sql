-- MySQL dump 10.13  Distrib 8.0.18, for macos10.14 (x86_64)
--
-- Host: 127.0.0.1    Database: ideaas_model_porta
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `contexts`
--

DROP TABLE IF EXISTS `contexts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contexts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `description` text,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contexts`
--

LOCK TABLES `contexts` WRITE;
/*!40000 ALTER TABLE `contexts` DISABLE KEYS */;
INSERT INTO `contexts` VALUES (1,'Part Program - Tool - Mode','Contesto combinazione di 3 parametri di contesto',1,'2019-12-20 15:22:42','2019-12-20 15:22:42',NULL),(2,'Part Program - Tool','Contesto combinazione di 2 parametri di contesto',1,'2019-12-20 15:22:42','2019-12-20 15:22:42',NULL);
/*!40000 ALTER TABLE `contexts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contexts_has_dimensions`
--

DROP TABLE IF EXISTS `contexts_has_dimensions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contexts_has_dimensions` (
  `context_id` int(11) NOT NULL,
  `context_dimension_id` int(11) NOT NULL,
  PRIMARY KEY (`context_id`,`context_dimension_id`),
  KEY `fk_contexts_has_context_parameters_context_parameters1_idx` (`context_dimension_id`),
  KEY `fk_contexts_has_context_parameters_contexts1_idx` (`context_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contexts_has_dimensions`
--

LOCK TABLES `contexts_has_dimensions` WRITE;
/*!40000 ALTER TABLE `contexts_has_dimensions` DISABLE KEYS */;
INSERT INTO `contexts_has_dimensions` VALUES (1,1),(2,1),(1,2),(1,3),(2,3);
/*!40000 ALTER TABLE `contexts_has_dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contexts_has_features`
--

DROP TABLE IF EXISTS `contexts_has_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contexts_has_features` (
  `context_id` int(11) NOT NULL,
  `feature_id` int(11) NOT NULL,
  `warning_lower_bound` double DEFAULT NULL,
  `warning_upper_bound` double DEFAULT NULL,
  `error_lower_bound` double DEFAULT NULL,
  `error_upper_bound` double DEFAULT NULL,
  PRIMARY KEY (`context_id`,`feature_id`),
  KEY `fk_contexts_has_features_features1_idx` (`feature_id`),
  KEY `fk_contexts_has_features_contexts1_idx` (`context_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contexts_has_features`
--

LOCK TABLES `contexts_has_features` WRITE;
/*!40000 ALTER TABLE `contexts_has_features` DISABLE KEYS */;
/*!40000 ALTER TABLE `contexts_has_features` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_acquisition_anomalies`
--

DROP TABLE IF EXISTS `data_acquisition_anomalies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `data_acquisition_anomalies` (
  `date` text,
  `anomaly_perc` text,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_acquisition_anomalies`
--

LOCK TABLES `data_acquisition_anomalies` WRITE;
/*!40000 ALTER TABLE `data_acquisition_anomalies` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_acquisition_anomalies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_summarisation_anomalies_anomaly`
--

DROP TABLE IF EXISTS `data_summarisation_anomalies_anomaly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `data_summarisation_anomalies_anomaly` (
  `start_datetime` text,
  `end_datetime` text,
  `algorithm` text,
  `centroid_dist_stable` text,
  `radius_dist_stable` text,
  `radius_vers_stable` text,
  `centroid_dist_previous` text,
  `radius_dist_previous` text,
  `radius_vers_previous` text,
  `description` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_summarisation_anomalies_anomaly`
--

LOCK TABLES `data_summarisation_anomalies_anomaly` WRITE;
/*!40000 ALTER TABLE `data_summarisation_anomalies_anomaly` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_summarisation_anomalies_anomaly` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_components`
--

DROP TABLE IF EXISTS `dim_components`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_components` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `descriptino` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_components`
--

LOCK TABLES `dim_components` WRITE;
/*!40000 ALTER TABLE `dim_components` DISABLE KEYS */;
INSERT INTO `dim_components` VALUES (1,'x','Unit 1.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,1),(2,'y','Unit 2.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,1),(3,'z','Unit 3.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,1),(5,'k','Unit 1.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,2),(6,'j','Unit 2.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,2),(7,'i','Unit 3.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,2),(8,'l','Unit 1.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,3),(9,'m','Unit 2.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,3),(10,'n','Unit 3.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,3);
/*!40000 ALTER TABLE `dim_components` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_enterprises`
--

DROP TABLE IF EXISTS `dim_enterprises`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_enterprises` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `descriptino` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `value_UNIQUE` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_enterprises`
--

LOCK TABLES `dim_enterprises` WRITE;
/*!40000 ALTER TABLE `dim_enterprises` DISABLE KEYS */;
INSERT INTO `dim_enterprises` VALUES (1,'Azienda 1','Azienda 1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_enterprises` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_machines`
--

DROP TABLE IF EXISTS `dim_machines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_machines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `descriptino` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `value_UNIQUE` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_machines`
--

LOCK TABLES `dim_machines` WRITE;
/*!40000 ALTER TABLE `dim_machines` DISABLE KEYS */;
INSERT INTO `dim_machines` VALUES (1,'Macchina 101143','101143',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,2,1),(2,'Macchina 101170','101170',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,2,1),(3,'Macchina 101185','101185',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,2,2);
/*!40000 ALTER TABLE `dim_machines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_modes`
--

DROP TABLE IF EXISTS `dim_modes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_modes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `descriptino` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_modes`
--

LOCK TABLES `dim_modes` WRITE;
/*!40000 ALTER TABLE `dim_modes` DISABLE KEYS */;
INSERT INTO `dim_modes` VALUES (1,'G0','0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(2,'G1','1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_modes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_part_programs`
--

DROP TABLE IF EXISTS `dim_part_programs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_part_programs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `descriptino` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_part_programs`
--

LOCK TABLES `dim_part_programs` WRITE;
/*!40000 ALTER TABLE `dim_part_programs` DISABLE KEYS */;
INSERT INTO `dim_part_programs` VALUES (1,'','0171506601',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(2,'','0171506891',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(3,'','0171506825',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(4,'','0101502033 PIPPPO SOLO VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(5,'','0171506796',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(6,'','0101502033 VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(7,'','0171501362',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(8,'','0101501730',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(9,'','0171506406',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(10,'','0171507190',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(11,'','0171506779 GR6187',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(12,'','0171506616 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(13,'','0171507268 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(14,'','0171507300 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(15,'','0171507301 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(16,'','0171506825 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(17,'','EVOS',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(18,'','0171507619',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(19,'','0171507301',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(20,'','0171506736',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(21,'','0171507344',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(22,'','0171506785',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(23,'','0171506998',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(24,'','0171507308',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(25,'','0171507160',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(26,'','0171507300',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(27,'','0171507295 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(28,'','0171507474 GR6204',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(29,'','0171500830',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(30,'','0101502219 SOLO PIN',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(31,'','0171507586 GR6350',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(32,'','0101502184 VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(33,'','0171506751 GR6112',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(34,'','0171507370 GR6112',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(35,'','0171507116',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(36,'','0171507360 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(37,'','0171506954 GR.6161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(38,'','0171507260 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(39,'','0101502189 VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(40,'','0171506406 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(41,'','0171506693 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(42,'','0101502216 SOLO PIN',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(43,'','0171507298',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(44,'','0101502214',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(45,'','0101502033',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(46,'','0171501361',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(47,'','0171507649',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(48,'','0171506584',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(49,'','0171507352',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(50,'','0171507026',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(51,'','0171507300 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(52,'','0171507218',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(53,'','0171507152 GR6249',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(54,'','0171506602 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(55,'','0171507116 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(56,'','0171507186 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(57,'','0171506766 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(58,'','0171506601 NUOVO',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(59,'','0171507298 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(60,'','EVOS-DIVA',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(61,'','EVOS 29 09 15',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(62,'','0171506809 GR.6161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(63,'','0171506809',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(64,'','0171507308 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(65,'','0171507597 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(66,'','0171507601 GR6187',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(67,'','0171506736 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(68,'','6891',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(69,'','0171506640 GR6123',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(70,'','PIPPO',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(71,'','0171506796 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(72,'','0171506601 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(73,'','0171506676 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(74,'','0171506772 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(75,'','0171507171 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(76,'','0171506749 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(77,'','0101502189 GR1877',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(78,'','0171507301   VDS',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(79,'','0171507589   VDS',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(80,'','0171507304-A',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(81,'','0101500802',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(82,'','PER SANDRO',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(83,'','0171507560',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(84,'','0171507560 last',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(85,'','0171506602 GR.6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(86,'','EVOS 1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(87,'','0171506602',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(88,'','0171507560-B',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(89,'','0171506751',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(90,'','0171507304',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(91,'','0171507560-C',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(92,'','0171507304 NEW',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(93,'','0171507304 D',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(94,'','0171507560-A',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(95,'','0171507161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(96,'','0171506822 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(97,'','0171506808',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(98,'','0171506734',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(99,'','0171507287',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(100,'','0171506891 GR6143',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(101,'','0171507583 GR6350',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(102,'','0171507147 GR6161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(103,'','0101502216',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(104,'','0171506771 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(105,'','0171506998 GR6112',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(106,'','0171507327 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(107,'','0171506693',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(108,'','0171507194 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(109,'','0171507165 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_part_programs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_plants`
--

DROP TABLE IF EXISTS `dim_plants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_plants` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `description` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `value_UNIQUE` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_plants`
--

LOCK TABLES `dim_plants` WRITE;
/*!40000 ALTER TABLE `dim_plants` DISABLE KEYS */;
INSERT INTO `dim_plants` VALUES (1,'Impianto 1','Impianto 1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,1,1),(2,'Impianto 2','Impianto 2',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,1,1);
/*!40000 ALTER TABLE `dim_plants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dim_tools`
--

DROP TABLE IF EXISTS `dim_tools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dim_tools` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `descriptino` text,
  `id_parent_dimension` int(11) DEFAULT NULL,
  `id_parent_instance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dim_tools`
--

LOCK TABLES `dim_tools` WRITE;
/*!40000 ALTER TABLE `dim_tools` DISABLE KEYS */;
/*!40000 ALTER TABLE `dim_tools` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimensions`
--

DROP TABLE IF EXISTS `dimensions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dimensions` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ideaas_meta_model',
  `name` varchar(100) NOT NULL,
  `table_name` varchar(100) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `description` text,
  `input_position` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `id_parent_dimension` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_dimensions_dimensions1_idx` (`id_parent_dimension`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimensions`
--

LOCK TABLES `dimensions` WRITE;
/*!40000 ALTER TABLE `dimensions` DISABLE KEYS */;
INSERT INTO `dimensions` VALUES (1,'Azienda','dim_enterprises',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL),(2,'Impianto','dim_plants',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,1),(3,'Macchina','dim_machines',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,2),(4,'Componente','dim_components',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,3),(5,'Part Program','dim_part_programs',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL),(6,'Utensili','dim_tools',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL),(7,'Mode','dim_modes',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL);
/*!40000 ALTER TABLE `dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimensions_has_dimensions_specific_attributes`
--

DROP TABLE IF EXISTS `dimensions_has_dimensions_specific_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dimensions_has_dimensions_specific_attributes` (
  `dimension_id` int(11) NOT NULL,
  `dimension_specific_attribute_id` int(11) NOT NULL,
  PRIMARY KEY (`dimension_id`,`dimension_specific_attribute_id`),
  KEY `fk_dimensions_has_dimensions_specific_attributes_dimensions_idx` (`dimension_specific_attribute_id`),
  KEY `fk_dimensions_has_dimensions_specific_attributes_dimensions_idx1` (`dimension_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimensions_has_dimensions_specific_attributes`
--

LOCK TABLES `dimensions_has_dimensions_specific_attributes` WRITE;
/*!40000 ALTER TABLE `dimensions_has_dimensions_specific_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `dimensions_has_dimensions_specific_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimensions_specific_attributes`
--

DROP TABLE IF EXISTS `dimensions_specific_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dimensions_specific_attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `field` varchar(45) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `collaction` varchar(45) DEFAULT NULL,
  `attributes` varchar(45) DEFAULT NULL,
  `null` varchar(45) DEFAULT NULL,
  `predefined` varchar(45) DEFAULT NULL,
  `extra` varchar(45) DEFAULT NULL,
  `index` varchar(45) DEFAULT NULL,
  `autoincrement` varchar(45) DEFAULT NULL,
  `comments` varchar(45) DEFAULT NULL,
  `length` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimensions_specific_attributes`
--

LOCK TABLES `dimensions_specific_attributes` WRITE;
/*!40000 ALTER TABLE `dimensions_specific_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `dimensions_specific_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feature_spaces`
--

DROP TABLE IF EXISTS `feature_spaces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feature_spaces` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feature_spaces`
--

LOCK TABLES `feature_spaces` WRITE;
/*!40000 ALTER TABLE `feature_spaces` DISABLE KEYS */;
INSERT INTO `feature_spaces` VALUES (1,'Indurimento Asse',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL),(2,'Assorbimento energetico',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL),(3,'Usura Utensile',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL),(4,'Corrente - Velocità Asse Z',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL);
/*!40000 ALTER TABLE `feature_spaces` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feature_spaces_has_feature`
--

DROP TABLE IF EXISTS `feature_spaces_has_feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feature_spaces_has_feature` (
  `feature_space_id` int(11) NOT NULL,
  `feature_id` int(11) NOT NULL,
  PRIMARY KEY (`feature_space_id`,`feature_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feature_spaces_has_feature`
--

LOCK TABLES `feature_spaces_has_feature` WRITE;
/*!40000 ALTER TABLE `feature_spaces_has_feature` DISABLE KEYS */;
INSERT INTO `feature_spaces_has_feature` VALUES (1,1),(1,2),(2,1),(2,3),(2,4),(2,5),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(4,5),(4,8);
/*!40000 ALTER TABLE `feature_spaces_has_feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `features`
--

DROP TABLE IF EXISTS `features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `features` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `max` double DEFAULT NULL,
  `min` double DEFAULT NULL,
  `warning_lower_bound` double DEFAULT NULL,
  `warning_upper_bound` double DEFAULT NULL,
  `error_lower_bound` double DEFAULT NULL,
  `error_upper_bound` double DEFAULT NULL,
  `measure_unit` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `input_position` int(11) DEFAULT NULL,
  `context_warning_upper_bound` double DEFAULT NULL,
  `context_warning_upper_error` double DEFAULT NULL,
  `context_warning_lower_error` double DEFAULT NULL,
  `context_warning_lower_warning` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `features`
--

LOCK TABLES `features` WRITE;
/*!40000 ALTER TABLE `features` DISABLE KEYS */;
INSERT INTO `features` VALUES (1,'Carico mandrino',218.8,0,20,200,10,210,'%',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,3,NULL,NULL,NULL,NULL),(2,'Numero di giri mandrino',6400,0,700,5600,500,6000,'rpm',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,4,NULL,NULL,NULL,NULL),(3,'Corrente asse X',19.4,-20.3,-16,15,-19,18,'Amp',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,5,NULL,NULL,NULL,NULL),(4,'Corrente asse Y',22.6,-22.4,-17,17,-20,20,'Amp',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,7,NULL,NULL,NULL,NULL),(5,'Corrente asse Z',17.8,-13.9,-7,15,-10,16,'Amp',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,9,NULL,NULL,NULL,NULL),(6,'Velocità asse X',23980,-23980,-20000,20000,-22500,22500,'mm/min',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,6,NULL,NULL,NULL,NULL),(7,'Velocità asse Y',23980,-23980,-20000,20000,-22500,22500,'mm/min',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,8,NULL,NULL,NULL,NULL),(8,'Velocità asse Z',20020,-20020,-18000,18000,-19000,19000,'mm/min',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,10,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `features` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-10 12:20:57
