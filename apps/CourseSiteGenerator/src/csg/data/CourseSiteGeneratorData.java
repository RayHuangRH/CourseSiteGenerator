package csg.data;

import javafx.collections.ObservableList;
import djf.components.AppDataComponent;
import djf.modules.AppGUIModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import static csg.CourseSiteGeneratorPropertyType.OH_GRAD_RADIO_BUTTON;
import static csg.CourseSiteGeneratorPropertyType.OH_OFFICE_HOURS_TABLE_VIEW;
import static csg.CourseSiteGeneratorPropertyType.OH_TAS_TABLE_VIEW;
import csg.data.TimeSlot.DayOfWeek;
import static csg.workspace.style.CSGStyle.CLASS_ICON;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import java.io.File;
import java.util.Calendar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import properties_manager.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;

/**
 * This is the data component for TAManagerApp. It has all the data needed to be
 * set by the user via the User Interface and file I/O can set and get all the
 * data from this object
 *
 * @author Richard McKenna
 */
public class CourseSiteGeneratorData implements AppDataComponent {

	// WE'LL NEED ACCESS TO THE APP TO NOTIFY THE GUI WHEN DATA CHANGES
	CourseSiteGeneratorApp app;

	// THESE ARE ALL THE TEACHING ASSISTANTS
	HashMap<TAType, ArrayList<TeachingAssistantPrototype>> allTAs;

	// NOTE THAT THIS DATA STRUCTURE WILL DIRECTLY STORE THE
	// DATA IN THE ROWS OF THE TABLE VIEW
	ObservableList<Lecture> lectures;
	ObservableList<Recitation> recitations;
	ObservableList<Lab> labs;
	ObservableList<Schedule> schedules;

	ObservableList<TeachingAssistantPrototype> teachingAssistants;
	ObservableList<TimeSlot> officeHours;
	ArrayList<TimeSlot> beforeHours;
	ArrayList<TimeSlot> holderHours;
	ArrayList<TimeSlot> afterHours;
	ArrayList<TimeSlot> hoursToRemove;
	ArrayList<TimeSlot> hoursToAdd;

	HashMap<String, String> syllabusDetails;
	// THESE ARE THE TIME BOUNDS FOR THE OFFICE HOURS GRID. NOTE
	// THAT THESE VALUES CAN BE DIFFERENT FOR DIFFERENT FILES, BUT
	// THAT OUR APPLICATION USES THE DEFAULT TIME VALUES AND PROVIDES
	// NO MEANS FOR CHANGING THESE VALUES
	int startHour;
	int endHour;

	// DEFAULT VALUES FOR START AND END HOURS IN MILITARY HOURS
	public static final int MIN_START_HOUR = 9;
	public static final int MAX_END_HOUR = 20;

	/**
	 * This constructor will setup the required data structures for use, but
	 * will have to wait on the office hours grid, since it receives the
	 * StringProperty objects from the Workspace.
	 *
	 * @param initApp The application this data manager belongs to.
	 */
	public CourseSiteGeneratorData(CourseSiteGeneratorApp initApp) {
		// KEEP THIS FOR LATER
		app = initApp;
		AppGUIModule gui = app.getGUIModule();

		// SETUP THE DATA STRUCTURES
		allTAs = new HashMap();
		allTAs.put(TAType.Graduate, new ArrayList());
		allTAs.put(TAType.Undergraduate, new ArrayList());

		beforeHours = new ArrayList();
		holderHours = new ArrayList();
		afterHours = new ArrayList();
		hoursToRemove = new ArrayList();
		hoursToAdd = new ArrayList();

		TableView<Lecture> lectureTable = (TableView) gui.getGUINode(MT_LECTURES_TABLE);
		lectures = lectureTable.getItems();

		TableView<Recitation> recitationTable = (TableView) gui.getGUINode(MT_RECITATION_TABLE);
		recitations = recitationTable.getItems();

		TableView<Lab> labTable = (TableView) gui.getGUINode(MT_LAB_TABLE);
		labs = labTable.getItems();

		TableView<Schedule> scheduleTable = (TableView) gui.getGUINode(SC_SCHEDULE_TABLE);
		schedules = scheduleTable.getItems();

		// GET THE LIST OF TAs FOR THE LEFT TABLE
		TableView<TeachingAssistantPrototype> taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
		teachingAssistants = taTableView.getItems();

		// THESE ARE THE DEFAULT OFFICE HOURS
		startHour = MIN_START_HOUR;
		endHour = MAX_END_HOUR;

		resetOfficeHours();
	}

	// ACCESSOR METHODS
	public int getStartHour() {
		return startHour;
	}

	public int getEndHour() {
		return endHour;
	}

	// PRIVATE HELPER METHODS
	private void sortTAs() {
		Collections.sort(teachingAssistants);
	}
	
	public void sortSchedule(){
		Collections.sort(schedules);
	}
	
	public void sortLecture(){
		Collections.sort(lectures);
	}
	
	public void sortRecitation(){
		Collections.sort(recitations);
	}
	
	public void sortLab(){
		Collections.sort(labs);
	}

	private void resetOfficeHours() {
		//THIS WILL STORE OUR OFFICE HOURS
		AppGUIModule gui = app.getGUIModule();
		TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
		officeHours = officeHoursTableView.getItems();
		officeHours.clear();
		for (int i = startHour; i <= endHour; i++) {
			TimeSlot timeSlot = new TimeSlot(this.getTimeString(i, true),
					this.getTimeString(i, false));
			officeHours.add(timeSlot);

			TimeSlot halfTimeSlot = new TimeSlot(this.getTimeString(i, false),
					this.getTimeString(i + 1, true));
			officeHours.add(halfTimeSlot);
		}
	}

	public String getTimeString(int militaryHour, boolean onHour) {
		String minutesText = "00";
		if (!onHour) {
			minutesText = "30";
		}

		// FIRST THE START AND END CELLS
		int hour = militaryHour;
		if (hour > 12) {
			hour -= 12;
		}
		String cellText = "" + hour + ":" + minutesText;
		if (militaryHour < 12) {
			cellText += "am";
		} else {
			cellText += "pm";
		}
		return cellText;
	}

	public int getTimeInt(String time) {
		String id = time.substring(time.length() - 2);
		int militaryHour = 0;
		String hours = time.substring(0, time.indexOf(":"));
		int intHour = Integer.parseInt(hours);
		if (id.equals("pm") && intHour != 12) {
			militaryHour += 12;
		}
		militaryHour += intHour;
		return militaryHour;
	}

	// METHODS TO OVERRIDE
	/**
	 * Called each time new work is created or loaded, it resets all data and
	 * data structures such that they can be used for new values.
	 */
	@Override
	public void reset() {
		AppGUIModule gui = app.getGUIModule();
		AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
		GridPane stylePane = ((GridPane) gui.getGUINode(SI_STYLE_PANE));

		startHour = MIN_START_HOUR;
		endHour = MAX_END_HOUR;
		teachingAssistants.clear();
		lectures.clear();
		recitations.clear();
		labs.clear();
		schedules.clear();
		
		loadCSS();
		addYear();
		resetTexts();

		allTAs = new HashMap();
		allTAs.put(TAType.Graduate, new ArrayList());
		allTAs.put(TAType.Undergraduate, new ArrayList());

		for (TimeSlot timeSlot : officeHours) {
			timeSlot.reset();
		}

		PropertiesManager props = PropertiesManager.getPropertiesManager();
		props.removeProperty(SI_FAVICON_BASE_ICON);
		props.addProperty(SI_FAVICON_BASE_ICON, props.getProperty(DEFAULT_FAV));
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_FAVICON_BASE_ICON));
		ohBuilder.buildImage(SI_FAVICON_BASE_ICON, stylePane, 1, 1, 1, 1, CLASS_ICON, ENABLED);
		props.removeProperty(SI_NAVBAR_BASE_ICON);
		props.addProperty(SI_NAVBAR_BASE_ICON, props.getProperty(DEFAULT_NAV));
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_NAVBAR_BASE_ICON));
		ohBuilder.buildImage(SI_NAVBAR_BASE_ICON, stylePane, 1, 2, 1, 1, CLASS_ICON, ENABLED);
		props.removeProperty(SI_LEFT_BASE_ICON);
		props.addProperty(SI_LEFT_BASE_ICON, props.getProperty(DEFAULT_LEFT));
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_LEFT_BASE_ICON));
		ohBuilder.buildImage(SI_LEFT_BASE_ICON, stylePane, 1, 3, 1, 1, CLASS_ICON, ENABLED);
		props.removeProperty(SI_RIGHT_BASE_ICON);
		props.addProperty(SI_RIGHT_BASE_ICON, props.getProperty(DEFAULT_RIGHT));
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_RIGHT_BASE_ICON));
		ohBuilder.buildImage(SI_RIGHT_BASE_ICON, stylePane, 1, 4, 1, 1, CLASS_ICON, ENABLED);
	}
	
	public void resetTexts(){
		AppGUIModule gui = app.getGUIModule();
		((TextField) gui.getGUINode(SI_TITLE_BOX)).setText("");
		((TextField) gui.getGUINode(SI_NAME_BOX)).setText("");
		((TextField) gui.getGUINode(SI_ROOM_BOX)).setText("");
		((TextField) gui.getGUINode(SI_EMAIL_BOX)).setText("");
		((TextField) gui.getGUINode(SI_HOME_PAGE_BOX)).setText("");
		((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX)).setText("");
		
		((TextArea) gui.getGUINode(SY_DESCRIPTION_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_TOPICS_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_PREREQUISITES_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_OUTCOMES_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_TEXTBOOKS_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_COMPONENTS_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_NOTE_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_DISHONESTY_AREA)).setText("");
		((TextArea) gui.getGUINode(SY_ASSISTANCE_AREA)).setText("");
		
		((TextField) gui.getGUINode(OH_NAME_TEXT_FIELD)).setText("");
		((TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD)).setText("");
	
		((TextField) gui.getGUINode(SC_TITLE_BOX)).setText("");
		((TextField) gui.getGUINode(SC_TOPIC_BOX)).setText("");
		((TextField) gui.getGUINode(SC_LINK_BOX)).setText("");
	}

	public void loadCSS() {
		AppGUIModule gui = app.getGUIModule();
		ObservableList list = ((ComboBox) gui.getGUINode(SI_FONT_BOX)).getItems();
		list.clear();
		File folder = new File("./work/css/");
		File[] listOfFiles = folder.listFiles();
		((ComboBox) gui.getGUINode(SI_FONT_BOX)).setValue(listOfFiles[0].getName());
		for (int i = 0; i < listOfFiles.length; i++) {
			if(list.contains(listOfFiles[i].getName())){
				continue;
			}else{
				list.add(listOfFiles[i].getName());
			}
		}
	}
	
	public void addYear(){
		AppGUIModule gui = app.getGUIModule();
		ObservableList list = ((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getItems();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if(!list.contains(""+year)){
			list.add(""+year);
		}
		if(!list.contains(""+(year+1))){
			list.add(""+(year+1));
		}
	}
	
	public void setDirectory(){
		AppGUIModule gui = app.getGUIModule();
		String export = ".\\export\\";
		export += ((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getValue()+"_";
		export += ((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getValue()+"_";
		export += ((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getValue()+"_";
		export += ((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getValue()+"\\";
		export += "public_html";
		((Label) gui.getGUINode(SI_EXPORT_TEXT)).setText(export);
		
	}
	
	public void clearCombo(){
		AppGUIModule gui = app.getGUIModule();
		((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getItems().clear();
		((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getItems().clear();
		((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getItems().clear();
		((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getItems().clear();
	}

	// SERVICE METHODS
	public void initHours(String startHourText, String endHourText) {
		int initStartHour = Integer.parseInt(startHourText);
		int initEndHour = Integer.parseInt(endHourText);
		if (initStartHour <= initEndHour) {
			// THESE ARE VALID HOURS SO KEEP THEM
			// NOTE THAT THESE VALUES MUST BE PRE-VERIFIED
			startHour = initStartHour;
			endHour = initEndHour;
		}
		resetOfficeHours();
	}

	public void setTimeRange(String startTime, String endTime) {
		//REMOVE REMAINING HOURS FROM OFFICE HOURS
		this.startHour = Integer.parseInt(startTime.substring(0,startTime.indexOf(":")));
		this.endHour = Integer.parseInt(endTime.substring(0,endTime.indexOf(":")));
		for (TimeSlot t : officeHours) {
			hoursToRemove.add(t);
			hoursToAdd.add(t);
		}
		holderHours.addAll(hoursToAdd);
		officeHours.removeAll(hoursToRemove);
		hoursToAdd.clear();
		hoursToRemove.clear();
		officeHours.clear();
		//ADD BEFORE HOURS 
		for (TimeSlot t : beforeHours) {
			hoursToRemove.add(t);
			hoursToAdd.add(t);
		}
		beforeHours.removeAll(hoursToRemove);
		officeHours.addAll(hoursToAdd);
		hoursToAdd.clear();
		hoursToRemove.clear();
		//ADD HOLD HOURS
		for (TimeSlot t : holderHours) {
			hoursToRemove.add(t);
			hoursToAdd.add(t);
		}
		holderHours.removeAll(hoursToRemove);
		officeHours.addAll(hoursToAdd);
		hoursToAdd.clear();
		hoursToRemove.clear();
		//ADD AFTER HOURS
		for (TimeSlot t : afterHours) {
			hoursToRemove.add(t);
			hoursToAdd.add(t);
		}
		afterHours.removeAll(hoursToRemove);
		officeHours.addAll(hoursToAdd);
		hoursToAdd.clear();
		hoursToRemove.clear();
		//REMOVE EARLY HOURS
		for (TimeSlot t : officeHours) {
			if (getTimeInt(t.getStartTime()) < getTimeInt(startTime)) {
				hoursToRemove.add(t);
				hoursToAdd.add(t);
			}
		}
		beforeHours.addAll(hoursToAdd);
		officeHours.removeAll(hoursToRemove);
		hoursToAdd.clear();
		hoursToRemove.clear();
		//REMOVE LATE HOURS
		for (TimeSlot t : officeHours) {
			if (getTimeInt(t.getStartTime()) > getTimeInt(endTime)) {
				hoursToAdd.add(t);
				hoursToRemove.add(t);
			}
		}
		afterHours.addAll(hoursToAdd);
		officeHours.removeAll(hoursToRemove);
		hoursToAdd.clear();
		hoursToRemove.clear();
		updateHoursTableView();
	}

	public void addLecture(Lecture lecture) {
		lectures.add(lecture);
		sortLecture();
	}

	public void removeLecture(Lecture lecture) {
		lectures.remove(lecture);
	}

	public void addLab(Lab lab) {
		labs.add(lab);
		sortLab();
	}

	public void removeLab(Lab lab) {
		labs.remove(lab);
	}

	public void addRecitation(Recitation recitation) {
		recitations.add(recitation);
		sortRecitation();
	}

	public void removeRecitation(Recitation recitation) {
		recitations.remove(recitation);
	}

	public void addSchedule(Schedule schedule) {
		schedules.add(schedule);
		sortSchedule();
	}

	public void removeSchedule(Schedule schedule) {
		schedules.remove(schedule);
	}

	public void addTA(TeachingAssistantPrototype ta) {
		if (!hasTA(ta)) {
			TAType taType = TAType.valueOf(ta.getType());
			ArrayList<TeachingAssistantPrototype> tas = allTAs.get(taType);
			tas.add(ta);
			this.updateTAs();
		}
	}

	public void addTA(TeachingAssistantPrototype ta, HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours) {
		addTA(ta);
		for (TimeSlot timeSlot : officeHours.keySet()) {
			ArrayList<DayOfWeek> days = officeHours.get(timeSlot);
			for (DayOfWeek dow : days) {
				timeSlot.addTA(dow, ta);
			}
		}
	}

	public void removeTA(TeachingAssistantPrototype ta) {
		// REMOVE THE TA FROM THE LIST OF TAs
		TAType taType = TAType.valueOf(ta.getType());
		allTAs.get(taType).remove(ta);

		// REMOVE THE TA FROM ALL OF THEIR OFFICE HOURS
		for (TimeSlot timeSlot : officeHours) {
			timeSlot.removeTA(ta);
		}

		// AND REFRESH THE TABLES
		this.updateTAs();
	}

	public void removeTA(TeachingAssistantPrototype ta, HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours) {
		removeTA(ta);
		for (TimeSlot timeSlot : officeHours.keySet()) {
			ArrayList<DayOfWeek> days = officeHours.get(timeSlot);
			for (DayOfWeek dow : days) {
				timeSlot.removeTA(dow, ta);
			}
		}
	}

	public void addImage(int id, String file) throws InvalidXMLFileFormatException {
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		AppGUIModule gui = app.getGUIModule();
		AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
		GridPane stylePane = ((GridPane) gui.getGUINode(SI_STYLE_PANE));
		file = file.substring(file.lastIndexOf("\\") + 1);
		if (id == 0) {
			props.removeProperty(SI_FAVICON_BASE_ICON);
			props.addProperty(SI_FAVICON_BASE_ICON, file);
			stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_FAVICON_BASE_ICON));
			ohBuilder.buildImage(SI_FAVICON_BASE_ICON, stylePane, 1, 1, 1, 1, CLASS_ICON, ENABLED);
		} else if (id == 1) {
			props.removeProperty(SI_NAVBAR_BASE_ICON);
			props.addProperty(SI_NAVBAR_BASE_ICON, file);
			stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_NAVBAR_BASE_ICON));
			ohBuilder.buildImage(SI_NAVBAR_BASE_ICON, stylePane, 1, 2, 1, 1, CLASS_ICON, ENABLED);
		} else if (id == 2) {
			props.removeProperty(SI_LEFT_BASE_ICON);
			props.addProperty(SI_LEFT_BASE_ICON, file);
			stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_LEFT_BASE_ICON));
			ohBuilder.buildImage(SI_LEFT_BASE_ICON, stylePane, 1, 3, 1, 1, CLASS_ICON, ENABLED);
		} else {
			props.removeProperty(SI_RIGHT_BASE_ICON);
			props.addProperty(SI_RIGHT_BASE_ICON, file);
			stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_RIGHT_BASE_ICON));
			ohBuilder.buildImage(SI_RIGHT_BASE_ICON, stylePane, 1, 4, 1, 1, CLASS_ICON, ENABLED);
		}
	}

	public void storeBox(int id) {
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		AppGUIModule gui = app.getGUIModule();
		if (id == 0) {
			props.removeProperty(DEFAULT_SUBJECT);
			props.removePropertyOptionsList(SUBJECT_OPTIONS);
			props.addProperty(DEFAULT_SUBJECT, (String) ((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getValue());
			props.addPropertyOptionsList(SUBJECT_OPTIONS, new ArrayList(((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getItems()));
		} else if (id == 1) {
			props.removeProperty(DEFAULT_NUMBER);
			props.removePropertyOptionsList(NUMBER_OPTIONS);
			props.addProperty(DEFAULT_NUMBER, (String) ((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getValue());
			props.addPropertyOptionsList(NUMBER_OPTIONS, new ArrayList(((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getItems()));
		} else if (id == 2) {
			props.removeProperty(DEFAULT_SEMESTER);
			props.removePropertyOptionsList(SEMESTER_OPTIONS);
			props.addProperty(DEFAULT_SEMESTER, (String) ((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getValue());
			props.addPropertyOptionsList(SEMESTER_OPTIONS, new ArrayList(((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getItems()));
		} else {
			props.removeProperty(DEFAULT_YEAR);
			props.removePropertyOptionsList(YEAR_OPTIONS);
			props.addProperty(DEFAULT_YEAR, (String) ((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getValue());
			props.addPropertyOptionsList(YEAR_OPTIONS, new ArrayList(((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getItems()));
		}
	}

	public DayOfWeek getColumnDayOfWeek(int columnNumber) {
		return TimeSlot.DayOfWeek.values()[columnNumber - 2];
	}

	public TeachingAssistantPrototype getTAWithName(String name) {
		Iterator<TeachingAssistantPrototype> taIterator = teachingAssistants.iterator();
		while (taIterator.hasNext()) {
			TeachingAssistantPrototype ta = taIterator.next();
			if (ta.getName().equals(name)) {
				return ta;
			}
		}
		return null;
	}

	public TeachingAssistantPrototype getTAWithEmail(String email) {
		Iterator<TeachingAssistantPrototype> taIterator = teachingAssistants.iterator();
		while (taIterator.hasNext()) {
			TeachingAssistantPrototype ta = taIterator.next();
			if (ta.getEmail().equals(email)) {
				return ta;
			}
		}
		return null;
	}

	public TimeSlot getTimeSlot(String startTime) {
		Iterator<TimeSlot> timeSlotsIterator = officeHours.iterator();
		while (timeSlotsIterator.hasNext()) {
			TimeSlot timeSlot = timeSlotsIterator.next();
			String timeSlotStartTime = timeSlot.getStartTime().replace(":", "_");
			if (timeSlotStartTime.equals(startTime)) {
				return timeSlot;
			}
		}
		return null;
	}

	public TAType getSelectedType() {
		RadioButton allRadio = (RadioButton) app.getGUIModule().getGUINode(OH_ALL_RADIO_BUTTON);
		if (allRadio.isSelected()) {
			return TAType.All;
		}
		RadioButton gradRadio = (RadioButton) app.getGUIModule().getGUINode(OH_GRAD_RADIO_BUTTON);
		if (gradRadio.isSelected()) {
			return TAType.Graduate;
		} else {
			return TAType.Undergraduate;
		}
	}

	public TeachingAssistantPrototype getSelectedTA() {
		AppGUIModule gui = app.getGUIModule();
		TableView<TeachingAssistantPrototype> tasTable = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
		return tasTable.getSelectionModel().getSelectedItem();
	}

	public HashMap<TimeSlot, ArrayList<DayOfWeek>> getTATimeSlots(TeachingAssistantPrototype ta) {
		HashMap<TimeSlot, ArrayList<DayOfWeek>> timeSlots = new HashMap();
		for (TimeSlot timeSlot : officeHours) {
			if (timeSlot.hasTA(ta)) {
				ArrayList<DayOfWeek> daysForTA = timeSlot.getDaysForTA(ta);
				timeSlots.put(timeSlot, daysForTA);
			}
		}
		return timeSlots;
	}

	private boolean hasTA(TeachingAssistantPrototype testTA) {
		return allTAs.get(TAType.Graduate).contains(testTA)
				|| allTAs.get(TAType.Undergraduate).contains(testTA);
	}

	public boolean isTASelected() {
		AppGUIModule gui = app.getGUIModule();
		TableView tasTable = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
		return tasTable.getSelectionModel().getSelectedItem() != null;
	}

	public boolean isLegalNewTA(String name, String email) {
		if ((name.trim().length() > 0)
				&& (email.trim().length() > 0)) {
			// MAKE SURE NO TA ALREADY HAS THE SAME NAME
			TAType type = this.getSelectedType();
			TeachingAssistantPrototype testTA = new TeachingAssistantPrototype(name, email, type);
			if (this.teachingAssistants.contains(testTA)) {
				return false;
			}
			if (this.isLegalNewEmail(email)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLegalNewName(String testName) {
		if (testName.trim().length() > 0) {
			for (TeachingAssistantPrototype testTA : this.teachingAssistants) {
				if (testTA.getName().equals(testName)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean isLegalNewEmail(String email) {
		Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
				"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		if (matcher.find()) {
			for (TeachingAssistantPrototype ta : this.teachingAssistants) {
				if (ta.getEmail().equals(email.trim())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isDayOfWeekColumn(int columnNumber) {
		return columnNumber >= 2;
	}

	public boolean isTATypeSelected() {
		AppGUIModule gui = app.getGUIModule();
		RadioButton allRadioButton = (RadioButton) gui.getGUINode(OH_ALL_RADIO_BUTTON);
		return !allRadioButton.isSelected();
	}

	public boolean isValidTAEdit(TeachingAssistantPrototype taToEdit, String name, String email) {
		if (!taToEdit.getName().equals(name)) {
			if (!this.isLegalNewName(name)) {
				return false;
			}
		}
		if (!taToEdit.getEmail().equals(email)) {
			if (!this.isLegalNewEmail(email)) {
				return false;
			}
		}
		return true;
	}

	public boolean isValidNameEdit(TeachingAssistantPrototype taToEdit, String name) {
		if (!taToEdit.getName().equals(name)) {
			if (!this.isLegalNewName(name)) {
				return false;
			}
		}
		return true;
	}

	public boolean isValidEmailEdit(TeachingAssistantPrototype taToEdit, String email) {
		if (!taToEdit.getEmail().equals(email)) {
			if (!this.isLegalNewEmail(email)) {
				return false;
			}
		}
		return true;
	}

	public boolean isValidComboItem(ComboBox comboBox, String item) {
		ObservableList<String> items = comboBox.getItems();
		if (items.contains(item)) {
			return false;
		}
		return true;
	}

	public void updateTAs() {
		TAType type = getSelectedType();
		selectTAs(type);
	}

	public void selectTAs(TAType type) {
		teachingAssistants.clear();
		Iterator<TeachingAssistantPrototype> tasIt = this.teachingAssistantsIterator();
		while (tasIt.hasNext()) {
			TeachingAssistantPrototype ta = tasIt.next();
			if (type.equals(TAType.All)) {
				teachingAssistants.add(ta);
			} else if (ta.getType().equals(type.toString())) {
				teachingAssistants.add(ta);
			}
		}

		// SORT THEM BY NAME
		sortTAs();

		// CLEAR ALL THE OFFICE HOURS
		Iterator<TimeSlot> officeHoursIt = officeHours.iterator();
		while (officeHoursIt.hasNext()) {
			TimeSlot timeSlot = officeHoursIt.next();
			timeSlot.filter(type);
		}

		app.getFoolproofModule().updateAll();
	}

	public Iterator<TimeSlot> officeHoursIterator() {
		return officeHours.iterator();
	}

	public Iterator<TeachingAssistantPrototype> teachingAssistantsIterator() {
		return new AllTAsIterator();
	}
	
	public Iterator<Lecture> lecturesIterator(){
		return lectures.iterator();
	}
	
	public Iterator<Recitation> recitationIterator(){
		return recitations.iterator();
	}

	public Iterator<Lab> labIterator(){
		return labs.iterator();
	}
	
	public Iterator<Schedule> scheduleIterator(){
		return schedules.iterator();
	}
	

	public void updateHoursTableView() {
		AppGUIModule gui = app.getGUIModule();
		RadioButton allBtn = (RadioButton) gui.getGUINode(OH_ALL_RADIO_BUTTON);
		RadioButton gradBtn = (RadioButton) gui.getGUINode(OH_GRAD_RADIO_BUTTON);
		TAType taType;
		if (allBtn.isSelected()) {
			taType = TAType.All;
		} else if (gradBtn.isSelected()) {
			taType = TAType.Graduate;
		} else {
			taType = TAType.Undergraduate;
		}

		TableView officeHoursTable = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
		Iterator<TimeSlot> hoursIterator = officeHoursIterator();
		while (hoursIterator.hasNext()) {
			TimeSlot tSlot = hoursIterator.next();
			tSlot.updateDayText(getColumnDayOfWeek(2), taType);
			tSlot.updateDayText(getColumnDayOfWeek(3), taType);
			tSlot.updateDayText(getColumnDayOfWeek(4), taType);
			tSlot.updateDayText(getColumnDayOfWeek(5), taType);
			tSlot.updateDayText(getColumnDayOfWeek(6), taType);
		}
		officeHoursTable.refresh();
	}
	
	public void updateScheduleTable(){
		AppGUIModule gui = app.getGUIModule();
		TableView scheduleTable = (TableView) gui.getGUINode(SC_SCHEDULE_TABLE);
		scheduleTable.refresh();
	}
	
	public void updateLectureTable(){
		AppGUIModule gui = app.getGUIModule();
		TableView lectureTable = (TableView) gui.getGUINode(MT_LECTURES_TABLE);
		lectureTable.refresh();
	}
	
	public void updateRecitationTable(){
		AppGUIModule gui = app.getGUIModule();
		TableView recitationTable = (TableView) gui.getGUINode(MT_RECITATION_TABLE);
		recitationTable.refresh();
	}
	
	public void updateLabTable(){
		AppGUIModule gui = app.getGUIModule();
		TableView labTable = (TableView) gui.getGUINode(MT_LAB_TABLE);
		labTable.refresh();
	}

	private class AllTAsIterator implements Iterator {

		Iterator gradIt = allTAs.get(TAType.Graduate).iterator();
		Iterator undergradIt = allTAs.get(TAType.Undergraduate).iterator();

		public AllTAsIterator() {
		}

		@Override
		public boolean hasNext() {
			if (gradIt.hasNext() || undergradIt.hasNext()) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Object next() {
			if (gradIt.hasNext()) {
				return gradIt.next();
			} else if (undergradIt.hasNext()) {
				return undergradIt.next();
			} else {
				return null;
			}
		}
	}
}
