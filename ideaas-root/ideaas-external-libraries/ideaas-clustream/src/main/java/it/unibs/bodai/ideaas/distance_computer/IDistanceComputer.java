package it.unibs.bodai.ideaas.distance_computer;

import com.yahoo.labs.samoa.instances.Instance;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;

public interface IDistanceComputer {
	/**
	 * Metodo per il calcolo del kernel piu' vicino al punto e la sua distanza
	 * @param inst
	 * @param kernels
	 * @return
	 */
	public NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels);

	/**
	 * Metodo per il calcolo della distanza tra un kernel ed un altro kernel diverso più vicino
	 * @param kernel
	 * @param kernels
	 * @return
	 */
	public double computeMinDistanceKernels(MyClustreamKernel kernel, MyClustreamKernel[] kernels);

	/**
	 * Metodo per calcolare le posizioni dei kerneò più vicine
	 * @param kernels
	 * @return
	 */
	public NearesKernelsPositionsInArray foundPositionsNearestKernels(MyClustreamKernel[] kernels);
	
	/**
	 * Method to get the oldest kernel
	 * @return
	 */
	public KernelAge getOldestKernel(MyClustreamKernel[] kernels);
}
