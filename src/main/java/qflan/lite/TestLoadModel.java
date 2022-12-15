package qflan.lite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

import com.microsoft.z3.Z3Exception;

import it.imt.qflan.core.model.QFlanModel;
import it.imt.qflan.core.multivesta.QFlanJavaState;
import qflan.lite.utils.XLogHelper;

public class TestLoadModel {

	public static void main(String[] args) throws Z3Exception, FileNotFoundException, IOException {
		
		// right now we use a hard-coded model. new models can be created via java
		QFlanModel m = new BikesDiagramLite().createModel();
		QFlanJavaState s = new QFlanJavaState(m);
		
		int tracesToSimulate = 100; // no of traces to simulate. TODO: change with stat-based
		int eventsPerTrace = 60; // no of events per trace to simulate. TODO: change with stat-based / steady state
		
		// prepare the new log
		XLog log = XLogHelper.generateNewXLog("qflan-lite-test");
		
		for (int i = 0; i < tracesToSimulate; i++) {
			
			// reset simulation with seed for the given iteration
			s.setSimulatorForNewSimulation(i);
			// create a trace corresponding to the new case
			XTrace trace = XLogHelper.createTrace("case-" + i);
			
			for (int j = 0; j < eventsPerTrace; j++) {
				s.performOneStepOfSimulation();
				
				// extract all simulations attributes
				Map<String, String> logData = s.computeDataToLog();
				// activity name is hard-docded on the activity attribute
				XEvent e = XLogHelper.insertEvent(trace, logData.get("activity"));
				
				// add all other attributes. TODO: do proper casting based on the actual attribute type
				for (String k : logData.keySet()) {
					XLogHelper.decorateElement(e, k, logData.get(k));
				}
			}
			log.add(trace);
		}
		
		new XesXmlSerializer().serialize(log, new FileOutputStream(new File("log.xes")));
		System.out.println("Done");
	}
}
