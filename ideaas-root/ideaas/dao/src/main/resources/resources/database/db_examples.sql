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
-- Dumping data for table `contexts`
--

LOCK TABLES `contexts` WRITE;
/*!40000 ALTER TABLE `contexts` DISABLE KEYS */;
INSERT INTO `contexts` VALUES (1,'Part Program - Tool - Mode','Contesto combinazione di 3 parametri di contesto',1,'2019-12-20 15:22:42','2019-12-20 15:22:42',NULL),(2,'Part Program - Tool','Contesto combinazione di 2 parametri di contesto',1,'2019-12-20 15:22:42','2019-12-20 15:22:42',NULL);
/*!40000 ALTER TABLE `contexts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `contexts_has_dimensions`
--

LOCK TABLES `contexts_has_dimensions` WRITE;
/*!40000 ALTER TABLE `contexts_has_dimensions` DISABLE KEYS */;
INSERT INTO `contexts_has_dimensions` VALUES (1,1),(2,1),(1,2),(1,3),(2,3);
/*!40000 ALTER TABLE `contexts_has_dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `contexts_has_features`
--

LOCK TABLES `contexts_has_features` WRITE;
/*!40000 ALTER TABLE `contexts_has_features` DISABLE KEYS */;
/*!40000 ALTER TABLE `contexts_has_features` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `data_acquisition_anomalies`
--

LOCK TABLES `data_acquisition_anomalies` WRITE;
/*!40000 ALTER TABLE `data_acquisition_anomalies` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_acquisition_anomalies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `data_summarisation_anomalies_anomaly`
--

LOCK TABLES `data_summarisation_anomalies_anomaly` WRITE;
/*!40000 ALTER TABLE `data_summarisation_anomalies_anomaly` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_summarisation_anomalies_anomaly` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_components`
--

LOCK TABLES `dim_components` WRITE;
/*!40000 ALTER TABLE `dim_components` DISABLE KEYS */;
INSERT INTO `dim_components` VALUES (1,'x','Unit 1.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,1),(2,'y','Unit 2.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,1),(3,'z','Unit 3.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,1),(5,'k','Unit 1.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,2),(6,'j','Unit 2.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,2),(7,'i','Unit 3.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,2),(8,'l','Unit 1.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,3),(9,'m','Unit 2.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,3),(10,'n','Unit 3.0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,3,3);
/*!40000 ALTER TABLE `dim_components` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_enterprises`
--

LOCK TABLES `dim_enterprises` WRITE;
/*!40000 ALTER TABLE `dim_enterprises` DISABLE KEYS */;
INSERT INTO `dim_enterprises` VALUES (1,'Azienda 1','Azienda 1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_enterprises` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_machines`
--

LOCK TABLES `dim_machines` WRITE;
/*!40000 ALTER TABLE `dim_machines` DISABLE KEYS */;
INSERT INTO `dim_machines` VALUES (1,'Macchina 101143','101143',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,2,1),(2,'Macchina 101170','101170',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,2,1),(3,'Macchina 101185','101185',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,2,2);
/*!40000 ALTER TABLE `dim_machines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_modes`
--

LOCK TABLES `dim_modes` WRITE;
/*!40000 ALTER TABLE `dim_modes` DISABLE KEYS */;
INSERT INTO `dim_modes` VALUES (1,'G0','0',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(2,'G1','1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_modes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_part_programs`
--

LOCK TABLES `dim_part_programs` WRITE;
/*!40000 ALTER TABLE `dim_part_programs` DISABLE KEYS */;
INSERT INTO `dim_part_programs` VALUES (1,'','0171506601',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(2,'','0171506891',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(3,'','0171506825',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(4,'','0101502033 PIPPPO SOLO VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(5,'','0171506796',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(6,'','0101502033 VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(7,'','0171501362',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(8,'','0101501730',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(9,'','0171506406',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(10,'','0171507190',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(11,'','0171506779 GR6187',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(12,'','0171506616 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(13,'','0171507268 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(14,'','0171507300 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(15,'','0171507301 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(16,'','0171506825 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(17,'','EVOS',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(18,'','0171507619',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(19,'','0171507301',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(20,'','0171506736',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(21,'','0171507344',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(22,'','0171506785',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(23,'','0171506998',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(24,'','0171507308',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(25,'','0171507160',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(26,'','0171507300',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(27,'','0171507295 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(28,'','0171507474 GR6204',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(29,'','0171500830',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(30,'','0101502219 SOLO PIN',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(31,'','0171507586 GR6350',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(32,'','0101502184 VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(33,'','0171506751 GR6112',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(34,'','0171507370 GR6112',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(35,'','0171507116',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(36,'','0171507360 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(37,'','0171506954 GR.6161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(38,'','0171507260 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(39,'','0101502189 VDS-USCITE',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(40,'','0171506406 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(41,'','0171506693 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(42,'','0101502216 SOLO PIN',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(43,'','0171507298',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(44,'','0101502214',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(45,'','0101502033',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(46,'','0171501361',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(47,'','0171507649',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(48,'','0171506584',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(49,'','0171507352',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(50,'','0171507026',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(51,'','0171507300 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(52,'','0171507218',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(53,'','0171507152 GR6249',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(54,'','0171506602 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(55,'','0171507116 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(56,'','0171507186 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(57,'','0171506766 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(58,'','0171506601 NUOVO',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(59,'','0171507298 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(60,'','EVOS-DIVA',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(61,'','EVOS 29 09 15',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(62,'','0171506809 GR.6161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(63,'','0171506809',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(64,'','0171507308 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(65,'','0171507597 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(66,'','0171507601 GR6187',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(67,'','0171506736 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(68,'','6891',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(69,'','0171506640 GR6123',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(70,'','PIPPO',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(71,'','0171506796 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(72,'','0171506601 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(73,'','0171506676 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(74,'','0171506772 GR.6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(75,'','0171507171 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(76,'','0171506749 GR6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(77,'','0101502189 GR1877',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(78,'','0171507301   VDS',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(79,'','0171507589   VDS',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(80,'','0171507304-A',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(81,'','0101500802',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(82,'','PER SANDRO',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(83,'','0171507560',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(84,'','0171507560 last',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(85,'','0171506602 GR.6072',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(86,'','EVOS 1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(87,'','0171506602',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(88,'','0171507560-B',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(89,'','0171506751',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(90,'','0171507304',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(91,'','0171507560-C',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(92,'','0171507304 NEW',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(93,'','0171507304 D',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(94,'','0171507560-A',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(95,'','0171507161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(96,'','0171506822 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(97,'','0171506808',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(98,'','0171506734',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(99,'','0171507287',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(100,'','0171506891 GR6143',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(101,'','0171507583 GR6350',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(102,'','0171507147 GR6161',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(103,'','0101502216',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(104,'','0171506771 GR6077',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(105,'','0171506998 GR6112',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(106,'','0171507327 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(107,'','0171506693',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(108,'','0171507194 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL),(109,'','0171507165 GR6136',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_part_programs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_plants`
--

LOCK TABLES `dim_plants` WRITE;
/*!40000 ALTER TABLE `dim_plants` DISABLE KEYS */;
INSERT INTO `dim_plants` VALUES (1,'Impianto 1','Impianto 1',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,1,1),(2,'Impianto 2','Impianto 2',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL,1,1);
/*!40000 ALTER TABLE `dim_plants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_tools`
--

LOCK TABLES `dim_tools` WRITE;
/*!40000 ALTER TABLE `dim_tools` DISABLE KEYS */;
/*!40000 ALTER TABLE `dim_tools` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dimensions`
--

LOCK TABLES `dimensions` WRITE;
/*!40000 ALTER TABLE `dimensions` DISABLE KEYS */;
INSERT INTO `dimensions` VALUES (1,'Azienda','dim_enterprises',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL),(2,'Impianto','dim_plants',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,1),(3,'Macchina','dim_machines',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,2),(4,'Componente','dim_components',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,3),(5,'Part Program','dim_part_programs',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL),(6,'Utensili','dim_tools',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL),(7,'Mode','dim_modes',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,NULL);
/*!40000 ALTER TABLE `dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dimensions_has_dimensions_specific_attributes`
--

LOCK TABLES `dimensions_has_dimensions_specific_attributes` WRITE;
/*!40000 ALTER TABLE `dimensions_has_dimensions_specific_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `dimensions_has_dimensions_specific_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dimensions_specific_attributes`
--

LOCK TABLES `dimensions_specific_attributes` WRITE;
/*!40000 ALTER TABLE `dimensions_specific_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `dimensions_specific_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feature_spaces`
--

LOCK TABLES `feature_spaces` WRITE;
/*!40000 ALTER TABLE `feature_spaces` DISABLE KEYS */;
INSERT INTO `feature_spaces` VALUES (1,'Indurimento Asse',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL),(2,'Assorbimento energetico',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL),(3,'Usura Utensile',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL),(4,'Corrente - Velocità Asse Z',1,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL);
/*!40000 ALTER TABLE `feature_spaces` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feature_spaces_has_feature`
--

LOCK TABLES `feature_spaces_has_feature` WRITE;
/*!40000 ALTER TABLE `feature_spaces_has_feature` DISABLE KEYS */;
INSERT INTO `feature_spaces_has_feature` VALUES (1,1),(1,2),(2,1),(2,3),(2,4),(2,5),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(4,5),(4,8);
/*!40000 ALTER TABLE `feature_spaces_has_feature` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2021-03-30 15:09:35
-- MySQL dump 10.13  Distrib 8.0.18, for macos10.14 (x86_64)
--
-- Host: 127.0.0.1    Database: ideaas_smart4cpps
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
-- Dumping data for table `data_acquisition_anomalies`
--

LOCK TABLES `data_acquisition_anomalies` WRITE;
/*!40000 ALTER TABLE `data_acquisition_anomalies` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_acquisition_anomalies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `data_summarisation`
--

LOCK TABLES `data_summarisation` WRITE;
/*!40000 ALTER TABLE `data_summarisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_summarisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `data_summarisation_anomalies`
--

LOCK TABLES `data_summarisation_anomalies` WRITE;
/*!40000 ALTER TABLE `data_summarisation_anomalies` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_summarisation_anomalies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_alarm`
--

LOCK TABLES `dim_alarm` WRITE;
/*!40000 ALTER TABLE `dim_alarm` DISABLE KEYS */;
INSERT INTO `dim_alarm` VALUES (1,'Alarm - 0','0',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL),(2,'Alarm - 1','1',1,'2020-04-14 04:26:19','2020-04-14 04:26:19',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_alarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_components`
--

LOCK TABLES `dim_components` WRITE;
/*!40000 ALTER TABLE `dim_components` DISABLE KEYS */;
INSERT INTO `dim_components` VALUES (1,'Componente - U1.0','U1.0',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL),(2,'Componente - U2.0','U2.0',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL),(3,'Componente - U3.0','U3.0',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_components` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_machines`
--

LOCK TABLES `dim_machines` WRITE;
/*!40000 ALTER TABLE `dim_machines` DISABLE KEYS */;
INSERT INTO `dim_machines` VALUES (1,'Macchina - SnapuAuto','SnapuAuto',1,'2020-04-14 04:24:26','2020-04-14 04:24:26',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_machines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_modes`
--

LOCK TABLES `dim_modes` WRITE;
/*!40000 ALTER TABLE `dim_modes` DISABLE KEYS */;
INSERT INTO `dim_modes` VALUES (1,'Mode - 0','0',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL),(2,'Mode - 1','1',1,'2020-04-14 04:17:25','2020-04-14 04:17:25',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_modes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_part_programs`
--

LOCK TABLES `dim_part_programs` WRITE;
/*!40000 ALTER TABLE `dim_part_programs` DISABLE KEYS */;
INSERT INTO `dim_part_programs` VALUES (1,'Part Program - 0171507664','0171507664',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL),(2,'Part Program - 0101502397','0101502397',1,'2020-04-14 04:27:34','2020-04-14 04:27:34',NULL,NULL,NULL,NULL),(3,'Part Program - 0171507851','0171507851',1,'2020-04-14 04:29:18','2020-04-14 04:29:18',NULL,NULL,NULL,NULL),(4,'Part Program - 0171507159','0171507159',1,'2020-04-14 04:36:48','2020-04-14 04:36:48',NULL,NULL,NULL,NULL),(5,'Part Program - 0171507631','0171507631',1,'2020-04-14 04:47:55','2020-04-14 04:47:55',NULL,NULL,NULL,NULL),(6,'Part Program - 0171506655','0171506655',1,'2020-04-14 05:40:37','2020-04-14 05:40:37',NULL,NULL,NULL,NULL),(7,'Part Program - 0171507268','0171507268',1,'2020-04-14 05:51:20','2020-04-14 05:51:20',NULL,NULL,NULL,NULL),(8,'Part Program - 0171507839','0171507839',1,'2020-04-14 06:12:36','2020-04-14 06:12:36',NULL,NULL,NULL,NULL),(9,'Part Program - 0171507817','0171507817',1,'2020-04-14 06:21:36','2020-04-14 06:21:36',NULL,NULL,NULL,NULL),(10,'Part Program - 0171507396','0171507396',1,'2020-04-14 06:23:33','2020-04-14 06:23:33',NULL,NULL,NULL,NULL),(11,'Part Program - 0171507182','0171507182',1,'2020-04-14 06:25:48','2020-04-14 06:25:48',NULL,NULL,NULL,NULL),(12,'Part Program - 0171506406','0171506406',1,'2020-04-14 07:38:16','2020-04-14 07:38:16',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_part_programs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_step`
--

LOCK TABLES `dim_step` WRITE;
/*!40000 ALTER TABLE `dim_step` DISABLE KEYS */;
INSERT INTO `dim_step` VALUES (1,'Step - 0','0',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dim_tools`
--

LOCK TABLES `dim_tools` WRITE;
/*!40000 ALTER TABLE `dim_tools` DISABLE KEYS */;
INSERT INTO `dim_tools` VALUES (1,'Utensili - 25','25',1,'2020-04-14 04:17:13','2020-04-14 04:17:13',NULL,NULL,NULL,NULL),(2,'Utensili - 1','1',1,'2020-04-14 04:21:56','2020-04-14 04:21:56',NULL,NULL,NULL,NULL),(3,'Utensili - 8','8',1,'2020-04-14 04:21:56','2020-04-14 04:21:56',NULL,NULL,NULL,NULL),(4,'Utensili - 9','9',1,'2020-04-14 04:21:56','2020-04-14 04:21:56',NULL,NULL,NULL,NULL),(5,'Utensili - 2','2',1,'2020-04-14 04:21:57','2020-04-14 04:21:57',NULL,NULL,NULL,NULL),(6,'Utensili - 3','3',1,'2020-04-14 04:21:57','2020-04-14 04:21:57',NULL,NULL,NULL,NULL),(7,'Utensili - 5','5',1,'2020-04-14 04:21:57','2020-04-14 04:21:57',NULL,NULL,NULL,NULL),(8,'Utensili - 4','4',1,'2020-04-14 04:21:57','2020-04-14 04:21:57',NULL,NULL,NULL,NULL),(9,'Utensili - 7','7',1,'2020-04-14 04:21:57','2020-04-14 04:21:57',NULL,NULL,NULL,NULL),(10,'Utensili - 6','6',1,'2020-04-14 04:21:57','2020-04-14 04:21:57',NULL,NULL,NULL,NULL),(11,'Utensili - 10','10',1,'2020-04-14 04:27:37','2020-04-14 04:27:37',NULL,NULL,NULL,NULL),(12,'Utensili - 11','11',1,'2020-04-14 04:27:37','2020-04-14 04:27:37',NULL,NULL,NULL,NULL),(13,'Utensili - 12','12',1,'2020-04-14 04:27:37','2020-04-14 04:27:37',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `dim_tools` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dimensions`
--

LOCK TABLES `dimensions` WRITE;
/*!40000 ALTER TABLE `dimensions` DISABLE KEYS */;
INSERT INTO `dimensions` VALUES (3,'Macchina','dim_machines',1,NULL,NULL,'2019-12-20 15:22:43','2019-12-20 15:22:43',NULL,2),(4,'Componente','dim_components',1,'unit',25,'2019-12-20 15:22:43','2020-04-14 01:58:09',NULL,3),(5,'Part Program','dim_part_programs',1,NULL,10,'2019-12-20 15:22:43','2020-04-14 01:58:09',NULL,NULL),(6,'Utensili','dim_tools',1,NULL,15,'2019-12-20 15:22:43','2020-04-14 02:01:29',NULL,NULL),(7,'Mode','dim_modes',1,NULL,9,'2019-12-20 15:22:43','2020-04-14 01:58:09',NULL,NULL),(8,'Alarm','dim_alarm',1,NULL,5,'2020-04-14 01:58:09','2020-04-14 01:58:09',NULL,NULL),(9,'Step','dim_step',1,NULL,24,'2020-04-14 01:58:09','2020-04-14 01:58:09',NULL,NULL);
/*!40000 ALTER TABLE `dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feature_spaces`
--

LOCK TABLES `feature_spaces` WRITE;
/*!40000 ALTER TABLE `feature_spaces` DISABLE KEYS */;
INSERT INTO `feature_spaces` VALUES (1,'XYZ-Amp-Vel',1,'2020-04-14 05:01:16','2020-04-14 05:01:16',NULL),(2,'Xamp-vel',1,'2020-05-14 09:09:20','2020-05-14 09:09:20',NULL),(3,'SpAmp-SpVel',1,'2021-02-22 18:31:46','2021-02-22 18:31:46',NULL);
/*!40000 ALTER TABLE `feature_spaces` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feature_spaces_has_feature`
--

LOCK TABLES `feature_spaces_has_feature` WRITE;
/*!40000 ALTER TABLE `feature_spaces_has_feature` DISABLE KEYS */;
INSERT INTO `feature_spaces_has_feature` VALUES (1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(2,13),(2,14),(3,9),(3,10);
/*!40000 ALTER TABLE `feature_spaces_has_feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `features`
--

LOCK TABLES `features` WRITE;
/*!40000 ALTER TABLE `features` DISABLE KEYS */;
INSERT INTO `features` VALUES (1,'Acc1','Acc1',2,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(2,'Acc2','Acc2',3,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(3,'Acc3','Acc3',4,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(5,'Bamp','Bamp',6,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(6,'Bvel','Bvel',7,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(8,'PPRact','PPRact',11,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(9,'Spamp','Spamp',12,175,0,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2021-02-23 00:45:48',NULL,NULL,NULL,NULL,NULL),(10,'Spvel','Spvel',13,6400,0,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2021-02-23 00:45:48',NULL,NULL,NULL,NULL,NULL),(11,'Vamp','Vamp',16,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(12,'Vvel','Vvel',17,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-04-14 01:51:17',NULL,NULL,NULL,NULL,NULL),(13,'Xamp','Xamp',18,11.34,-12.27,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:51:17','2020-05-14 09:35:55',NULL,NULL,NULL,NULL,NULL),(14,'Xvel','Xvel',19,8815,11840,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:53:41','2020-05-14 09:35:55',NULL,NULL,NULL,NULL,NULL),(15,'Yamp','Yamp',20,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:53:41','2020-04-14 01:53:41',NULL,NULL,NULL,NULL,NULL),(16,'Yvel','Yvel',21,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:53:41','2020-04-14 01:53:41',NULL,NULL,NULL,NULL,NULL),(17,'Zamp','Zamp',22,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:53:41','2020-04-14 01:53:41',NULL,NULL,NULL,NULL,NULL),(18,'Zvel','Zvel',23,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 01:53:41','2020-04-14 01:53:41',NULL,NULL,NULL,NULL,NULL),(19,'CUact','CUact',8,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2020-04-14 02:00:29','2020-04-14 02:00:29',NULL,NULL,NULL,NULL,NULL);
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

-- Dump completed on 2021-03-30 15:09:35
