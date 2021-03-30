package it.unibs.bodai.ideaas.data_acquisition.implementation;

import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import it.unibs.bodai.ideaas.dao.model.Measure;

public class DataAcquisitionPair {
	private HashMap<String, List<Measure>> measuresToInsert;
	private long transformationTime;
	private int numFiles;
	private String description;
	
	public DataAcquisitionPair(HashMap<String, List<Measure>> measuresToInsert, long transformationTime, int numFiles, String description) {
		this.measuresToInsert = measuresToInsert;
		this.transformationTime = transformationTime;
		this.numFiles = numFiles;
		this.description = description;
	}

	/**
	 * @return the measuresToInsert
	 */
	public HashMap<String, List<Measure>> getDocumentsToInsert() {
		return measuresToInsert;
	}

	/**
	 * @param measuresToInsert the documentsToInsert to set
	 */
	public void setDocumentsToInsert(HashMap<String, List<Measure>> measuresToInsert) {
		this.measuresToInsert = measuresToInsert;
	}

	/**
	 * @return the transformationTime
	 */
	public long getTransformationTime() {
		return transformationTime;
	}

	/**
	 * @param transformationTime the transformationTime to set
	 */
	public void setTransformationTime(long transformationTime) {
		this.transformationTime = transformationTime;
	}

	/**
	 * @return the numFiles
	 */
	public int getNumFiles() {
		return numFiles;
	}

	/**
	 * @param numFiles the numFiles to set
	 */
	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
