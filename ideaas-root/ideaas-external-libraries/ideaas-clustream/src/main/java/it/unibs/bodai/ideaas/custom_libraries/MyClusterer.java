package it.unibs.bodai.ideaas.custom_libraries;

import moa.MOAObject;
import moa.gui.AWTRenderable;
import moa.options.OptionHandler;

import moa.MOAObject;
import moa.cluster.Clustering;
import moa.clusterers.Clusterer;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.core.Measurement;
import moa.gui.AWTRenderable;
import moa.options.OptionHandler;
import com.yahoo.labs.samoa.instances.Instance;

public interface MyClusterer extends MOAObject, OptionHandler, AWTRenderable {

	public void setModelContext(InstancesHeader ih);

	public InstancesHeader getModelContext();

	public boolean isRandomizable();

	public void setRandomSeed(int s);

	public boolean trainingHasStarted();

	public double trainingWeightSeenByModel();

	public void resetLearning();

	public void trainOnInstance(Instance inst);

	public double[] getVotesForInstance(Instance inst);

	public Measurement[] getModelMeasurements();

	public Clusterer[] getSubClusterers();

	public MyClusterer copy();

    public MyClustering getClusteringResult();

    public boolean implementsMicroClusterer();

    public MyClustering getMicroClusteringResult();
    
    public boolean keepClassLabel();

}
