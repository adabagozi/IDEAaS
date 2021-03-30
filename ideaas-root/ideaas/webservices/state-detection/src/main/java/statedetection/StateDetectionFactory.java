package statedetection;

import java.io.IOException;
import java.util.Optional;

import statedetection.implementation.StateDetectionLogicImpl;

public class StateDetectionFactory {
	private static final StateDetectionFactory factory = new StateDetectionFactory();
    private static Optional<IStateDetectionBusinessLogic> stateDetectionBusinessLogicInstance = Optional.empty(); 
    
    private StateDetectionFactory() {
		// TODO Auto-generated constructor stub
	}
    
    public static final StateDetectionFactory getStateDetectionFactory(){
        return factory;
    }
    
    public IStateDetectionBusinessLogic getStateDetectionBusinessLogic() throws IOException {
        if (!stateDetectionBusinessLogicInstance.isPresent()) {
        	stateDetectionBusinessLogicInstance = Optional.of(new StateDetectionLogicImpl());
        }
        return stateDetectionBusinessLogicInstance.get();
    }
}
