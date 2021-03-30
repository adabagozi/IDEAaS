package it.unibs.bodai.ideaas.utils;

import java.io.Serializable;
import java.util.Comparator;

public class NearesKernelsPositionsInArray implements Serializable{
	private static final long serialVersionUID = 1L;
	private int closestA = -1;
	private int closestB = -1;
	private double distance;
	
	public NearesKernelsPositionsInArray(int closestA, int closestB, double distance){
		super();
		this.closestA = closestA;
		this.closestB = closestB;
		this.distance = distance;
	}

	public int getClosestA() {
		return closestA;
	}

	public int getClosestB() {
		return closestB;
	}

	public double getDistance() {
		return distance;
	}


	public void setClosestA(int closestA) {
		this.closestA = closestA;
	}

	public void setClosestB(int closestB) {
		this.closestB = closestB;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
