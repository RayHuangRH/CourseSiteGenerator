package csg.transactions;

import csg.CourseSiteGeneratorApp;
import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author McKillaGorilla
 */
public class RemoveTA_Transaction implements jTPS_Transaction {

	CourseSiteGeneratorApp app;
	TeachingAssistantPrototype taToCut;
	HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours;

	public RemoveTA_Transaction(CourseSiteGeneratorApp initApp,
			TeachingAssistantPrototype initTAToCut,
			HashMap<TimeSlot, ArrayList<DayOfWeek>> initOfficeHours) {
		app = initApp;
		taToCut = initTAToCut;
		officeHours = initOfficeHours;
	}

	@Override
	public void doTransaction() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		data.removeTA(taToCut, officeHours);
	}

	@Override
	public void undoTransaction() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		data.addTA(taToCut, officeHours);
	}
}
