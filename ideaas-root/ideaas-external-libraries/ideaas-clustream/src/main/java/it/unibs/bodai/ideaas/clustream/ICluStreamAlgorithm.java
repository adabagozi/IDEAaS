package it.unibs.bodai.ideaas.clustream;

import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.custom_libraries.MyClustering;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;

public interface ICluStreamAlgorithm {

	public void setNumMicroKernels(int maxKernels);
	
	public void setTimeHorizont(int timeHorizont);
	
	public void resetLearningImpl(MyClustreamKernel[] oldKernels, long timestamp);
	
	public void resetLearningImpl();
	
	public void trainOnInstanceImpl(Instance instance);
	

	public void trainOnInstanceImpl(Instances instances);
	
	public long getTimestamp();
	
	public MyClustreamKernel[] getKernels();
	
	/**
	 * TODO serve solo nel caso in cui si voglia eseguire kmeans
	 * @return
	 */
//	public MyClustering getClusteringResult();
}
