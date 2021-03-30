package it.unibs.bodai.ideaas.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;

public class NearestKernel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private double minDistance = Double.MAX_VALUE;
	private MyClustreamKernel nearestKernel = null;
	
	public NearestKernel(double distance, MyClustreamKernel kernel) {
		super();
		this.minDistance = distance;
		this.nearestKernel = kernel;
		
	}

	public double getMinDistance() {
		return minDistance;
	}

	public MyClustreamKernel getNearestKernel() {
		return nearestKernel;
	}

	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	public void setNearestKernel(MyClustreamKernel nearestKernel) {
		this.nearestKernel = nearestKernel;
	}

	
}