package utils;

import java.util.ArrayList;

import xml_models.DataSetXML;
import xml_models.ParameterListXML;

public class ManageArrayList {

	public static int getIndexOf(ArrayList<DataSetXML> datasets, ParameterListXML parameterList) {
		int i = 0;
		
		for(DataSetXML ds : datasets) {
			if(ds.getParameterList().equals(parameterList)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
}
