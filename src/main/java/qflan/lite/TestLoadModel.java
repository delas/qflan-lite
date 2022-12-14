package qflan.lite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
		
		int tracesToSimulate = 1;
		int eventsPerTrace = 60;
		
		XLog log = XLogHelper.generateNewXLog("qflan-lite-test");
		
		QFlanModel m = new BikesDiagramLite().createModel();
		QFlanJavaState s = new QFlanJavaState(m);
		
		for (int i = 0; i < tracesToSimulate; i++) {
			s.setSimulatorForNewSimulation(i);
			XTrace trace = XLogHelper.createTrace("case-" + i);
			for (int j = 0; j < eventsPerTrace; j++) {
				s.performOneStepOfSimulation();
				
				XEvent e = XLogHelper.insertEvent(trace, "" + s.rval("price(Bike)"));
				
			}
			log.add(trace);
		}
		
		new XesXmlSerializer().serialize(log, new FileOutputStream(new File("log.xes")));
	}
}
