package it.unibs.bodai.ideaas.dao.utils;

public class BestMetch {

	private double diffCentroids = 0;
	private double diffRadius = 0;
	private double diffDensity = 0;
	private double diffRadiusVariation = 0;
	private double diffDensityVariation = 0;
	
	/**
	 * @param diffCentroids
	 * @param diffRadius
	 * @param diffDensity
	 */
	public BestMetch(double diffCentroids, double diffRadius, double diffDensity, double diffRadiusVariation, double diffDensityVariation) {
		super();
		this.diffCentroids = diffCentroids;
		this.diffRadius = diffRadius;
		this.diffDensity = diffDensity;
		this.diffRadiusVariation = diffRadiusVariation;
		this.diffDensityVariation = diffDensityVariation;
	}
	
	
	public BestMetch() {
		this(-1, -1,- 1, -1,- 1);
	}


	/**
	 * @return the diff
	 */
	public double getDiffCentroids() {
		return diffCentroids;
	}
	/**
	 * @return the diffRadius
	 */
	public double getDiffRadius() {
		return diffRadius;
	}
	/**
	 * @return the diffDensity
	 */
	public double getDiffDensity() {
		return diffDensity;
	}
	/**
	 * @param diff the diffCentroids to set
	 */
	public void setDiffCentroids(double diffCentroids) {
		this.diffCentroids = diffCentroids;
	}
	/**
	 * @param diffRadius the diffRadius to set
	 */
	public void setDiffRadius(double diffRadius) {
		this.diffRadius = diffRadius;
	}
	/**
	 * @param diffDensity the diffDensity to set
	 */
	public void setDiffDensity(double diffDensity) {
		this.diffDensity = diffDensity;
	}


	/**
	 * @return the diffRadiusVariation
	 */
	public double getDiffRadiusVariation() {
		return diffRadiusVariation;
	}


	/**
	 * @return the diffDensityVariation
	 */
	public double getDiffDensityVariation() {
		return diffDensityVariation;
	}


	/**
	 * @param diffRadiusVariation the diffRadiusVariation to set
	 */
	public void setDiffRadiusVariation(double diffRadiusVariation) {
		this.diffRadiusVariation = diffRadiusVariation;
	}


	/**
	 * @param diffDensityVariation the diffDensityVariation to set
	 */
	public void setDiffDensityVariation(double diffDensityVariation) {
		this.diffDensityVariation = diffDensityVariation;
	}
	
	
	
	
}
