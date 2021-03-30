package utils;

import java.util.ArrayList;
import java.util.Collection;

import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Measure;

public class SendAlertBoundaries { 

	public static void check(Collection<Measure> send_alert_measures_checked) {
		Collection<DimensionInstance> machine_changes = new ArrayList<>();
		Collection<Measure> measures_for_xml = new ArrayList<>();
		
		for(Measure m : send_alert_measures_checked) {
			Collection<DimensionInstance> dim_instances = m.getDimensionInstances();
			DimensionInstance dim = new DimensionInstance();
			for(DimensionInstance dim_instance : dim_instances) {
				if(dim_instance.getDimension().getId() == Consts.COMPONENT_DIMENSION_ID) {
//					System.out.println("Component: " + dim_instance.getInstanceId());
					dim.setInstanceId(dim_instance.getInstanceId());
				}

				else if(dim_instance.getDimension().getId() == Consts.MACHINE_DIMENSION_ID) {
//					System.out.println("Machine: " + dim_instance.getInstanceId());
					DimensionInstance machine = new DimensionInstance(dim_instance.getInstanceId(), new Dimension(dim_instance.getDimension().getId()));
					dim.setParentInstance(machine);
				}
			}
			
		}

		System.out.println("Size: " + machine_changes.size());
		for(DimensionInstance d : machine_changes) {
			System.out.println("Instance ID: " + d.getInstanceId() + " Parent ID: " + d.getParentInstance().getInstanceId());
		}
	}

}
