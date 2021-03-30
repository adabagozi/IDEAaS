package it.unibs.bodai.ideaas.dao.model;

import java.io.Serializable;
import java.util.Comparator;

public class MinSynthesisDistance implements Serializable, Comparator<MinSynthesisDistance>{
	
	private static final long serialVersionUID = 1L;

	private MultidimensionalRecordMap minSynthesis;
	private double minDistance;
	
	public MinSynthesisDistance() {
		super();
	}
	
	@Override
	public String toString() {
		return minSynthesis.hashCode() + " " + minDistance;
	}

	public MinSynthesisDistance(MultidimensionalRecordMap minSynthesis, double minDistance) {
		super();
		this.minSynthesis = minSynthesis;
		this.minDistance = minDistance;
	}
	public MultidimensionalRecordMap getMinSynthesis() {
		return minSynthesis;
	}
	public void setMinSynthesis(MultidimensionalRecordMap minSynthesis) {
		this.minSynthesis = minSynthesis;
	}
	public double getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}
	
	public int compare(MinSynthesisDistance o1, MinSynthesisDistance o2) {
		return Double.compare(o1.getMinDistance(), o2.getMinDistance());
	}
	
	

}
