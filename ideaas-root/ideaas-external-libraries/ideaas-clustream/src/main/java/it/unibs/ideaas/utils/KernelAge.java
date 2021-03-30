package it.unibs.ideaas.utils;

import java.io.Serializable;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;

public class KernelAge implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private double age = Double.MIN_VALUE;
	
	private MyClustreamKernel kernel = null;
	
	public KernelAge(MyClustreamKernel kernel, double age) {
		super();
		this.age = age;
		this.kernel = kernel;
		
	}

	public double getAge() {
		return age;
	}

	public MyClustreamKernel getOldestKernel() {
		return kernel;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public void setNearestKernel(MyClustreamKernel nearestKernel) {
		this.kernel = nearestKernel;
	}

	
}