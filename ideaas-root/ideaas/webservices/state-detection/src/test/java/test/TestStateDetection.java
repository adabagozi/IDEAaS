package test;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.implementation.storage.SendAlertMySQL;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.SendAlert;
import send_alert_objects.FspDataAndState;
import statedetection.IStateDetectionBusinessLogic;
import statedetection.StateDetectionFactory;
import utils.CheckBoundaries;
import utils.DateTime;
import utils.DateUtils;

public class TestStateDetection {
	private IDAO dao;
	private IStateDetectionBusinessLogic stateDetection;
	private DateUtils dateUtils;
	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");

	@Before
	public void setup() {
		try {
			this.dateUtils = new DateUtils();
			this.dao = DAOFactory.getDAOFactory().getDAO();
			this.stateDetection = StateDetectionFactory.getStateDetectionFactory().getStateDetectionBusinessLogic();
			mongoLogger.setLevel(Level.SEVERE);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void sendAlert() {

		String end_date = "14/01/2020 16:03";
		String interval = "3";

		String interval_type = "minutes";
		// Get collection name from end date
		String[] input_date = end_date.split(" ");
		String date = input_date[0];
		String[] date_parts = date.split("/");

		FspDataAndState fspDataAndState = new FspDataAndState();

		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();
		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();

		Date b = DateTime.dateTimePickerToISO(end_date);
		Date a = DateTime.sendAlertStartDate(interval_type, Integer.valueOf(interval), b);
		Date t = DateTime.addMinuteInterval(a);

		int i = 1;
		while (a.before(b)) {

			Collection<Measure> dimensions_measures_minute = this.stateDetection
					.updatedSendAlertQuery(date_parts[2] + "_" + date_parts[1], a, t, systemDimensions);
			CheckBoundaries.featureBoundaries(features_mysql, dimensions_measures_minute);
			fspDataAndState.featureDataAndFeatureSpace(feature_spaces, dimensions_measures_minute, systemDimensions);

			// long start_send_alert_service = System.currentTimeMillis();
			a = t;
			t = DateTime.addMinuteInterval(a);

			// i++;
			// System.out.println("Interval number: " + i);
			// long end_send_alert_service = System.currentTimeMillis();
			// System.out.println("Total time seconds: " +
			// TimeUnit.MILLISECONDS.toSeconds(end_send_alert_service -
			// start_send_alert_service) + " Total time in milliseconds: " +
			// TimeUnit.MILLISECONDS.toMillis(end_send_alert_service -
			// start_send_alert_service));
		}

		System.out.println("Send Alert completed");

	}


//	@Test
//	public void sendAlertTime() {
//
//		String start_date = "02/11/2016 03:20:00";
//		String interval_type = "seconds";
//
//
//		FspDataAndState fspDataAndState = new FspDataAndState();
//
//		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();
//
//		// TODO modificare queste nel db al fine di avere un'analisi su meno valori
//		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
////		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();
//
//		long start_extraction_time = 0;
//		long extraction_time = 0;
//		long end_elab_time = 0;
//
//		for(int i=60; i<=3600; i=i+60) {
//
//			//TODO migliorare questa parte ovviamente
//			ArrayList<FeatureSpace> feature_spaces = this.dao.readFeatureSpaces();
//			for (FeatureSpace feature_space : feature_spaces) {
//				feature_space.setFeatures_list(this.dao.readFeaturesByFeatureSpaceId(feature_space.getId()));
//			}
//			/********************************/
//
//			String end_date = "03/11/2016 00:00:00";
//			LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
//			LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);
//			System.out.print("\nseconds: " + i + "    ");
//
//			int iterations = 0;
//			while (start_datetime.isBefore(end_datetime)) {
//
//
//        			System.out.print("=");
//				start_extraction_time = System.currentTimeMillis();
//				LocalDateTime begin = start_datetime;
//				start_datetime = start_datetime.plusSeconds(i);
//				LocalDateTime datetime = LocalDateTime.from(start_datetime);//LocalDateTime.of(start_datetime.getYear(), start_datetime.getMonth(), start_datetime.getDayOfMonth(),start_datetime.getHour(), start_datetime.getMinute(), start_datetime.getSecond());
//
//				Collection<Measure> measures = this.dao.readMeasuresWithQueryTimeInterval(this.dateUtils.getCollectionName(start_datetime), datetime, interval_type, i);
//				extraction_time = System.currentTimeMillis() - start_extraction_time;
//
//				if (measures.size() >0) {
//					long time = System.currentTimeMillis();
//					CheckBoundaries.featureBoundaries(features_mysql, measures);
//
//					int alert_numbers = fspDataAndState.checkDataAndSendAlert(feature_spaces, measures, systemDimensions);
//
//					end_elab_time = System.currentTimeMillis() - time;
//					SendAlert sendAlert = new SendAlert(features_mysql.size(), ""+i, interval_type, measures.size(), extraction_time, end_elab_time, alert_numbers, begin, start_datetime);
//
//					this.dao.saveSendAlertTest(sendAlert);
//
//					iterations++;
//					if (iterations == 50) {
//						break;
//					}
//				}
//			}
//		}
//	}

//	@Test
//	public void sendAlertTiming() {
//		Timestamp start_test = new Timestamp(System.currentTimeMillis());
//		long start_elaboration_time = System.currentTimeMillis();
//		long start_extraction_time = 0;
//		long end_extraction_time = 0;
//		String end_date = "01/10/2016 16:01:00";
//		String interval = "1";
//		String interval_type = "minutes";
//
//
//		String interval_type = "minutes";
//		// Get collection name from end date
//
//		String[] input_date = end_date.split(" ");
//		String date = input_date[0];
//		String[] date_parts = date.split("/");
//
//		FspDataAndState fspDataAndState = new FspDataAndState();
//
//		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();
//		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
//		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();
//
//		Date b = DateTime.dateTimePickerToISO(end_date);
//		Date a = DateTime.sendAlertStartDate(interval_type, Integer.valueOf(interval), b);
//
//		long internal_elaboration = 0;
//		long xml_submit_time = 0;
//		int number_of_measures = 0;
//		Date t = DateTime.addMinuteInterval(a);
//		int i = 1;
//
//		while(a.before(b)) {
//			start_extraction_time = System.currentTimeMillis();
//			Collection<Measure> dimensions_measures_minute = this.stateDetection.updatedSendAlertQuery(date_parts[2] + "_" + date_parts[1], a, t, systemDimensions);
//			end_extraction_time = System.currentTimeMillis();
//			number_of_measures = dimensions_measures_minute.size() + number_of_measures;
//
//			CheckBoundaries.featureBoundaries(features_mysql, dimensions_measures_minute);
//			xml_submit_time = fspDataAndState.featureDataAndFeatureSpace(feature_spaces, dimensions_measures_minute, systemDimensions);
//
//			a = t;
//			t = DateTime.addMinuteInterval(a);
//
//			i++;
//			internal_elaboration = System.currentTimeMillis() - end_extraction_time;
//    //IDEAaS
//		// while (a.before(b)) {
//		//
//		// 	Collection<Measure> dimensions_measures_minute = this.stateDetection
//		// 			.updatedSendAlertQuery(date_parts[2] + "_" + date_parts[1], a, t, systemDimensions);
//		// 	CheckBoundaries.featureBoundaries(features_mysql, dimensions_measures_minute);
//		// 	fspDataAndState.featureDataAndFeatureSpace(feature_spaces, dimensions_measures_minute, systemDimensions);
//		//
//		// 	// long start_send_alert_service = System.currentTimeMillis();
//		// 	a = t;
//		// 	t = DateTime.addMinuteInterval(a);
//		//
//		// }
//
//		long total_elaboration_time = (System.currentTimeMillis() + internal_elaboration) - start_elaboration_time - xml_submit_time;
//
////		SendAlertMySQL sendAlert = new SendAlertMySQL(features_mysql.size(), interval_type, number_of_measures, end_extraction_time - start_extraction_time, total_elaboration_time, xml_submit_time, start_test, new Timestamp(System.currentTimeMillis()));
////		this.dao.saveSendAlertTest(sendAlert);
//	}
//

	@Test
	public void sendAlertTime() {

		String start_date = "02/11/2016 03:20:00";
		String interval_type = "seconds";


		FspDataAndState fspDataAndState = new FspDataAndState();

		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();

		// TODO modificare queste nel db al fine di avere un'analisi su meno valori
		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
//		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();

		long start_extraction_time = 0;
		long extraction_time = 0;
		long end_elab_time = 0;

		for(int i=60; i<=3600; i=i+60) {

//			//TODO migliorare questa parte ovviamente
			ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();
			for (FeatureSpace feature_space : feature_spaces) {
				feature_space.setFeatures(this.dao.readFeaturesByFeatureSpaceId(feature_space.getId()));
			}
			/********************************/

			String end_date = "03/11/2016 00:00:00";
			LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
			LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);
			System.out.print("\nseconds: " + i + "    ");

			int iterations = 0;
			while (start_datetime.isBefore(end_datetime)) {


        			System.out.print("=");
				start_extraction_time = System.currentTimeMillis();
				LocalDateTime begin = start_datetime;
				start_datetime = start_datetime.plusSeconds(i);
				LocalDateTime datetime = LocalDateTime.from(start_datetime);//LocalDateTime.of(start_datetime.getYear(), start_datetime.getMonth(), start_datetime.getDayOfMonth(),start_datetime.getHour(), start_datetime.getMinute(), start_datetime.getSecond());

				Collection<Measure> measures = this.dao.readMeasuresWithQueryTimeInterval(this.dateUtils.getCollectionName(start_datetime), datetime, interval_type, i);
				extraction_time = System.currentTimeMillis() - start_extraction_time;

				if (measures.size() >0) {
					long time = System.currentTimeMillis();
					CheckBoundaries.featureBoundaries(features_mysql, measures);

					int alert_numbers = fspDataAndState.checkDataAndSendAlert(feature_spaces, measures, systemDimensions);

					end_elab_time = System.currentTimeMillis() - time;
//					SendAlert sendAlert = new SendAlert(features_mysql.size(), ""+i, interval_type, measures.size(), extraction_time, end_elab_time, alert_numbers, begin, start_datetime);
//
//					this.dao.saveSendAlertTest(sendAlert);

					iterations++;
					if (iterations == 50) {
						break;
					}
				}
			}
		}
	}

//	@Test
	public void sendAlertTiming() {
		Timestamp start_test = new Timestamp(System.currentTimeMillis());
		long start_elaboration_time = System.currentTimeMillis();
		long start_extraction_time = 0;
		long end_extraction_time = 0;
		String end_date = "01/10/2016 16:01:00";
		String interval = "1";
		String interval_type = "minutes";

		//Get collection name from end date
		String[] input_date = end_date.split(" ");
		String date = input_date[0];
		String[] date_parts = date.split("/");

		FspDataAndState fspDataAndState = new FspDataAndState();

		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();
		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();

		Date b = DateTime.dateTimePickerToISO(end_date);
		Date a = DateTime.sendAlertStartDate(interval_type, Integer.valueOf(interval), b);

		long internal_elaboration = 0;
		long xml_submit_time = 0;
		int number_of_measures = 0;
		Date t = DateTime.addMinuteInterval(a);
		int i = 1;
		while(a.before(b)) {
			start_extraction_time = System.currentTimeMillis();
			Collection<Measure> dimensions_measures_minute = this.stateDetection.updatedSendAlertQuery(date_parts[2] + "_" + date_parts[1], a, t, systemDimensions);
			end_extraction_time = System.currentTimeMillis();
			number_of_measures = dimensions_measures_minute.size() + number_of_measures;

			CheckBoundaries.featureBoundaries(features_mysql, dimensions_measures_minute);
			xml_submit_time = fspDataAndState.featureDataAndFeatureSpace(feature_spaces, dimensions_measures_minute, systemDimensions);

			a = t;
			t = DateTime.addMinuteInterval(a);

			i++;
			internal_elaboration = System.currentTimeMillis() - end_extraction_time;
		}

		long total_elaboration_time = (System.currentTimeMillis() + internal_elaboration) - start_elaboration_time - xml_submit_time;

//		SendAlertMySQL sendAlert = new SendAlertMySQL(features_mysql.size(), interval_type, number_of_measures, end_extraction_time - start_extraction_time, total_elaboration_time, xml_submit_time, start_test, new Timestamp(System.currentTimeMillis()));
//		this.dao.saveSendAlertTest(sendAlert);
	}

}
