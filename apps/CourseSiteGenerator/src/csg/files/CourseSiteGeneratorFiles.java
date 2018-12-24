package csg.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.CourseSiteGeneratorApp;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab;
import csg.data.Lecture;
import csg.data.Recitation;
import csg.data.Schedule;
import csg.data.TAType;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;
import static csg.workspace.style.CSGStyle.CLASS_ICON;
import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import properties_manager.PropertiesManager;

/**
 * This class serves as the file component for the TA manager app. It provides
 * all saving and loading services for the application.
 *
 * @author Richard McKenna
 */
public class CourseSiteGeneratorFiles implements AppFileComponent {
	// THIS IS THE APP ITSELF

	CourseSiteGeneratorApp app;

	static final String COMBO_PATH = "app_data/properties/combo_options.json";
	// THESE ARE USED FOR IDENTIFYING JSON TYPES
	//SI JSON TYPES
	static final String JSON_INSTRUCTOR = "instructor";
	static final String JSON_LINK = "link";
	static final String JSON_HOURS = "hours";
	static final String JSON_PAGES = "pages";
	static final String JSON_HOME = "home";
	static final String JSON_SYLLABUS = "syllabus";
	static final String JSON_HWs = "HWs";
	static final String JSON_SUBJECT = "subject";
	static final String JSON_NUMBER = "number";
	static final String JSON_YEAR = "year";
	static final String JSON_SEMESTER = "semester";
	static final String JSON_EXPORT = "export";
	static final String JSON_ICONS = "logos";
	static final String JSON_FAVICON = "favicon";
	static final String JSON_NAVBAR = "navbar";
	static final String JSON_LEFT = "bottom_left";
	static final String JSON_RIGHT = "bottom_right";
	//SY JSON TYPES
	static final String JSON_DESCRIPTION = "description";
	static final String JSON_TOPICS = "topics";
	static final String JSON_PREREQ = "prerequisites";
	static final String JSON_OUTCOMES = "outcomes";
	static final String JSON_TEXTBOOKS = "textbooks";
	static final String JSON_COMPONENTS = "gradedComponents";
	static final String JSON_NOTE = "gradingNote";
	static final String JSON_DISHONESTY = "academicDishonesty";
	static final String JSON_ASSISTANCE = "specialAssistance";
	//MT JSON TYPES
	static final String JSON_LECTURES = "lectures";
	static final String JSON_LABS = "labs";
	static final String JSON_RECITATIONS = "recitations";
	static final String JSON_SECTIONS = "section";
	static final String JSON_DAYS = "days";
	static final String JSON_TIME = "time";
	static final String JSON_ROOM = "room";
	static final String JSON_DAY_TIME = "day_time";
	static final String JSON_LOCATION = "location";
	static final String JSON_TA1 = "ta_1";
	static final String JSON_TA2 = "ta_2";
	//OH JSON TYPES
	static final String JSON_GRAD_TAS = "grad_tas";
	static final String JSON_UNDERGRAD_TAS = "undergrad_tas";
	static final String JSON_NAME = "name";
	static final String JSON_EMAIL = "email";
	static final String JSON_TYPE = "type";
	static final String JSON_OFFICE_HOURS = "officeHours";
	static final String JSON_START_HOUR = "startHour";
	static final String JSON_END_HOUR = "endHour";
	static final String JSON_START_TIME = "time";
	static final String JSON_DAY_OF_WEEK = "day";
	static final String JSON_MONDAY = "monday";
	static final String JSON_TUESDAY = "tuesday";
	static final String JSON_WEDNESDAY = "wednesday";
	static final String JSON_THURSDAY = "thursday";
	static final String JSON_FRIDAY = "friday";
	//SCHEDULE JSON TYPES
	static final String JSON_SCHEDULE = "schedule";
	static final String JSON_DATE = "date";
	static final String JSON_TITLE = "title";
	static final String JSON_TOPIC = "topic";
	static final String JSON_STARTING_DATE = "startingDate";
	static final String JSON_ENDING_DATE = "endingDate";

	public CourseSiteGeneratorFiles(CourseSiteGeneratorApp initApp) {
		app = initApp;
	}

	@Override
	public void loadData(AppDataComponent data, String filePath) throws IOException {
		// CLEAR THE OLD DATA OUT
		AppGUIModule gui = app.getGUIModule();
		CourseSiteGeneratorData dataManager = (CourseSiteGeneratorData) data;
		dataManager.reset();

		// LOAD THE JSON FILE WITH ALL THE DATA
		JsonObject json = loadJSONFile(filePath);

		//LOAD START AND END HOURS
		String startHour = json.getString(JSON_START_HOUR);
		String endHour = json.getString(JSON_END_HOUR);
		dataManager.initHours("9", "20");

		loadComboOptions(dataManager);
		loadSite(dataManager, json);
		loadSyllabus(dataManager, json);

		//lOAD LECTURES
		JsonArray jsonLecturesArray = json.getJsonArray(JSON_LECTURES);
		for (int i = 0; i < jsonLecturesArray.size(); i++) {
			JsonObject jsonLecture = jsonLecturesArray.getJsonObject(i);
			String section = jsonLecture.getString(JSON_SECTIONS);
			String day = jsonLecture.getString(JSON_DAYS);
			String time = jsonLecture.getString(JSON_TIME);
			String room = jsonLecture.getString(JSON_ROOM);
			Lecture lecture = new Lecture(section, day, time, room);
			dataManager.addLecture(lecture);
		}

		JsonArray jsonLabArray = json.getJsonArray(JSON_LABS);
		for (int i = 0; i < jsonLabArray.size(); i++) {
			JsonObject jsonLab = jsonLabArray.getJsonObject(i);
			String section = jsonLab.getString(JSON_SECTIONS);
			String day = jsonLab.getString(JSON_DAY_TIME);
			String location = jsonLab.getString(JSON_LOCATION);
			String ta1 = jsonLab.getString(JSON_TA1);
			String ta2 = jsonLab.getString(JSON_TA2);
			Lab lab = new Lab(section, day, location, ta1, ta2);
			dataManager.addLab(lab);
		}

		JsonArray jsonRecitationArray = json.getJsonArray(JSON_RECITATIONS);
		for (int i = 0; i < jsonRecitationArray.size(); i++) {
			JsonObject jsonRecitation = jsonRecitationArray.getJsonObject(i);
			String section = jsonRecitation.getString(JSON_SECTIONS);
			String day = jsonRecitation.getString(JSON_DAY_TIME);
			String location = jsonRecitation.getString(JSON_LOCATION);
			String ta1 = jsonRecitation.getString(JSON_TA1);
			String ta2 = jsonRecitation.getString(JSON_TA2);
			Recitation recitation = new Recitation(section, day, location, ta1, ta2);
			dataManager.addRecitation(recitation);
		}

		// LOAD ALL THE GRAD TAs
		loadTAs(dataManager, json, JSON_GRAD_TAS);
		loadTAs(dataManager, json, JSON_UNDERGRAD_TAS);

		// AND THEN ALL THE OFFICE HOURS
		JsonArray jsonOfficeHoursArray = json.getJsonArray(JSON_OFFICE_HOURS);
		for (int i = 0; i < jsonOfficeHoursArray.size(); i++) {
			JsonObject jsonOfficeHours = jsonOfficeHoursArray.getJsonObject(i);
			String startTime = jsonOfficeHours.getString(JSON_START_TIME);
			DayOfWeek dow = DayOfWeek.valueOf(jsonOfficeHours.getString(JSON_DAY_OF_WEEK));
			String name = jsonOfficeHours.getString(JSON_NAME);
			TeachingAssistantPrototype ta = dataManager.getTAWithName(name);
			TimeSlot timeSlot = dataManager.getTimeSlot(startTime);
			timeSlot.toggleTA(dow, ta);
		}

		((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_START_BOX)).setValue(dataManager.getTimeString(Integer.parseInt(startHour), true));
		((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_END_BOX)).setValue(dataManager.getTimeString(Integer.parseInt(endHour), true));
		dataManager.setTimeRange((String) ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_START_BOX)).getValue(), (String) ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_END_BOX)).getValue());

		//LOAD SCHEDULE
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
		String startDate = json.getString(JSON_STARTING_DATE);
		LocalDate localStart = LocalDate.parse(startDate, formatter);
		String endDate = json.getString(JSON_ENDING_DATE);
		LocalDate localEnd = LocalDate.parse(endDate, formatter);
		((DatePicker) gui.getGUINode(SC_START_DATE_BOX)).setValue(localStart);
		((DatePicker) gui.getGUINode(SC_END_DATE_BOX)).setValue(localEnd);
		JsonArray jsonScheduleArray = json.getJsonArray(JSON_SCHEDULE);
		for (int i = 0; i < jsonScheduleArray.size(); i++) {
			JsonObject jsonSchedule = jsonScheduleArray.getJsonObject(i);
			String type = jsonSchedule.getString(JSON_TYPE);
			String date = jsonSchedule.getString(JSON_DATE);
			String title = jsonSchedule.getString(JSON_TITLE);
			String topic = jsonSchedule.getString(JSON_TOPIC);
			String link = jsonSchedule.getString(JSON_LINK);
			Schedule schedule = new Schedule(type, date, title, topic, link);
			dataManager.addSchedule(schedule);
		}
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	private void loadTAs(CourseSiteGeneratorData data, JsonObject json, String tas) {
		JsonArray jsonTAArray = json.getJsonArray(tas);
		for (int i = 0; i < jsonTAArray.size(); i++) {
			JsonObject jsonTA = jsonTAArray.getJsonObject(i);
			String name = jsonTA.getString(JSON_NAME);
			String email = jsonTA.getString(JSON_EMAIL);
			TAType type = TAType.valueOf(jsonTA.getString(JSON_TYPE));
			TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name, email, type);
			data.addTA(ta);
		}
	}

	private void loadComboOptions(CourseSiteGeneratorData data) throws IOException {
		File file = new File(COMBO_PATH);
		AppGUIModule gui = app.getGUIModule();
		String absolute = file.getAbsolutePath();
		try {
			data.clearCombo();
			JsonObject json = loadJSONFile(absolute);
			//LOAD SUBJECT BOX OPTIONS
			JsonArray subject = json.getJsonArray(JSON_SUBJECT);
			ObservableList subList = ((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getItems();
			for (int i = 0; i < subject.size(); i++) {
				String option = subject.getString(i);
				subList.add(option);
			}
			//LOAD NUMBER BOX OPTIONS
			JsonArray number = json.getJsonArray(JSON_NUMBER);
			ObservableList numList = ((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getItems();
			for (int i = 0; i < number.size(); i++) {
				String option = number.getString(i);
				numList.add(option);
			}
			//LOAD SEMESTER BOX OPTIONS
			JsonArray semester = json.getJsonArray(JSON_SEMESTER);
			ObservableList semList = ((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getItems();
			for (int i = 0; i < semester.size(); i++) {
				String option = semester.getString(i);
				semList.add(option);
			}
			//LOAD YEAR BOX OPTIONS
			JsonArray year = json.getJsonArray(JSON_YEAR);
			ObservableList yearList = ((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getItems();
			for (int i = 0; i < semester.size(); i++) {
				String option = year.getString(i);
				yearList.add(option);
			}

		} catch (FileNotFoundException e) {
			File newFile = new File(absolute);
			newFile.createNewFile();
			data.clearCombo();
		}

	}

	private void loadSite(CourseSiteGeneratorData data, JsonObject json) {
		//LOAD SITE TAB
		AppGUIModule gui = app.getGUIModule();
		//LOAD BANNER
		String subject = json.getString(JSON_SUBJECT);
		String number = json.getString(JSON_NUMBER);
		String semester = json.getString(JSON_SEMESTER);
		String year = json.getString(JSON_YEAR);
		data.addYear();
		String title = json.getString(JSON_TITLE);
		String export = json.getString(JSON_EXPORT);
		((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).setValue(subject);
		((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).setValue(number);
		((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).setValue(semester);
		((ComboBox) gui.getGUINode(SI_YEAR_BOX)).setValue(year);
		((TextField) gui.getGUINode(SI_TITLE_BOX)).setText(title);
		((Label) gui.getGUINode(SI_EXPORT_TEXT)).setText(export);
		//LOAD IMAGES
		JsonObject jsonIcons = json.getJsonObject(JSON_ICONS);
		String favicon = jsonIcons.getString(JSON_FAVICON);
		String navbar = jsonIcons.getString(JSON_NAVBAR);
		String bottom_left = jsonIcons.getString(JSON_LEFT);
		String bottom_right = jsonIcons.getString(JSON_RIGHT);
		AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
		GridPane stylePane = ((GridPane) gui.getGUINode(SI_STYLE_PANE));
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		props.removeProperty(SI_FAVICON_BASE_ICON);
		props.addProperty(SI_FAVICON_BASE_ICON, favicon);
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_FAVICON_BASE_ICON));
		ohBuilder.buildImage(SI_FAVICON_BASE_ICON, stylePane, 1, 1, 1, 1, CLASS_ICON, ENABLED);
		props.removeProperty(SI_NAVBAR_BASE_ICON);
		props.addProperty(SI_NAVBAR_BASE_ICON, navbar);
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_NAVBAR_BASE_ICON));
		ohBuilder.buildImage(SI_NAVBAR_BASE_ICON, stylePane, 1, 2, 1, 1, CLASS_ICON, ENABLED);
		props.removeProperty(SI_LEFT_BASE_ICON);
		props.addProperty(SI_LEFT_BASE_ICON, bottom_left);
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_LEFT_BASE_ICON));
		ohBuilder.buildImage(SI_LEFT_BASE_ICON, stylePane, 1, 3, 1, 1, CLASS_ICON, ENABLED);
		props.removeProperty(SI_RIGHT_BASE_ICON);
		props.addProperty(SI_RIGHT_BASE_ICON, bottom_right);
		stylePane.getChildren().remove((ImageView) gui.getGUINode(SI_RIGHT_BASE_ICON));
		ohBuilder.buildImage(SI_RIGHT_BASE_ICON, stylePane, 1, 4, 1, 1, CLASS_ICON, ENABLED);
		//LOAD PAGES
		JsonObject jsonPages = json.getJsonObject(JSON_PAGES);
		String home = jsonPages.getString(JSON_HOME);
		String syllabus = jsonPages.getString(JSON_SYLLABUS);
		String schedule = jsonPages.getString(JSON_SCHEDULE);
		String hw = jsonPages.getString(JSON_HWs);
		((CheckBox) gui.getGUINode(SI_HOME_BOX)).setSelected(Boolean.parseBoolean(home));
		((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)).setSelected(Boolean.parseBoolean(syllabus));
		((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)).setSelected(Boolean.parseBoolean(schedule));
		((CheckBox) gui.getGUINode(SI_HWS_BOX)).setSelected(Boolean.parseBoolean(hw));
		data.loadCSS();
		//LOAD INSTRUCTOR
		JsonObject jsonInstructor = json.getJsonObject(JSON_INSTRUCTOR);
		String name = jsonInstructor.getString(JSON_NAME);
		String link = jsonInstructor.getString(JSON_LINK);
		String email = jsonInstructor.getString(JSON_EMAIL);
		String room = jsonInstructor.getString(JSON_ROOM);
		String hours = jsonInstructor.getString(JSON_HOURS);
		((TextField) gui.getGUINode(SI_NAME_BOX)).setText(name);
		((TextField) gui.getGUINode(SI_ROOM_BOX)).setText(room);
		((TextField) gui.getGUINode(SI_EMAIL_BOX)).setText(email);
		((TextField) gui.getGUINode(SI_HOME_PAGE_BOX)).setText(link);
		((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX)).setText(hours);
	}

	private void loadSyllabus(CourseSiteGeneratorData data, JsonObject json) {
		AppGUIModule gui = app.getGUIModule();
		String description = json.getString(JSON_DESCRIPTION);
		((TextArea) gui.getGUINode(SY_DESCRIPTION_AREA)).setText(description);
		String topics = json.getString(JSON_TOPICS);
		((TextArea) gui.getGUINode(SY_TOPICS_AREA)).setText(topics);
		String prereq = json.getString(JSON_PREREQ);
		((TextArea) gui.getGUINode(SY_PREREQUISITES_AREA)).setText(prereq);
		String outcomes = json.getString(JSON_OUTCOMES);
		((TextArea) gui.getGUINode(SY_OUTCOMES_AREA)).setText(outcomes);
		String textbooks = json.getString(JSON_TEXTBOOKS);
		((TextArea) gui.getGUINode(SY_TEXTBOOKS_AREA)).setText(textbooks);
		String graded = json.getString(JSON_COMPONENTS);
		((TextArea) gui.getGUINode(SY_COMPONENTS_AREA)).setText(graded);
		String note = json.getString(JSON_NOTE);
		((TextArea) gui.getGUINode(SY_NOTE_AREA)).setText(note);
		String dishonesty = json.getString(JSON_DISHONESTY);
		((TextArea) gui.getGUINode(SY_DISHONESTY_AREA)).setText(dishonesty);
		String assistance = json.getString(JSON_ASSISTANCE);
		((TextArea) gui.getGUINode(SY_ASSISTANCE_AREA)).setText(assistance);
	}

	// HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
	private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
		InputStream is = new FileInputStream(jsonFilePath);
		JsonReader jsonReader = Json.createReader(is);
		JsonObject json = jsonReader.readObject();
		jsonReader.close();
		is.close();
		return json;
	}

	@Override
	public void saveData(AppDataComponent data, String filePath) throws IOException {
		// GET THE DATA
		CourseSiteGeneratorData dataManager = (CourseSiteGeneratorData) data;
		AppGUIModule gui = app.getGUIModule();
		PropertiesManager props = PropertiesManager.getPropertiesManager();

		saveComboOptions();

		JsonObject logoJson = Json.createObjectBuilder()
				.add(JSON_FAVICON, props.getProperty(SI_FAVICON_BASE_ICON))
				.add(JSON_NAVBAR, props.getProperty(SI_NAVBAR_BASE_ICON))
				.add(JSON_LEFT, props.getProperty(SI_LEFT_BASE_ICON))
				.add(JSON_RIGHT, props.getProperty(SI_RIGHT_BASE_ICON))
				.build();

		JsonObject instructorJson = Json.createObjectBuilder()
				.add(JSON_NAME, ((TextField) gui.getGUINode(SI_NAME_BOX)).getText())
				.add(JSON_LINK, ((TextField) gui.getGUINode(SI_HOME_PAGE_BOX)).getText())
				.add(JSON_EMAIL, ((TextField) gui.getGUINode(SI_EMAIL_BOX)).getText())
				.add(JSON_ROOM, ((TextField) gui.getGUINode(SI_ROOM_BOX)).getText())
				.add(JSON_HOURS, ((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX)).getText())
				.build();

		JsonObject pagesJson = Json.createObjectBuilder()
				.add(JSON_HOME, "" + ((CheckBox) gui.getGUINode(SI_HOME_BOX)).isSelected())
				.add(JSON_SYLLABUS, "" + ((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)).isSelected())
				.add(JSON_SCHEDULE, "" + ((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)).isSelected())
				.add(JSON_HWs, "" + ((CheckBox) gui.getGUINode(SI_HWS_BOX)).isSelected())
				.build();
		// NOW BUILD THE TA JSON OBJCTS TO SAVE
		JsonArrayBuilder gradTAsArrayBuilder = Json.createArrayBuilder();
		JsonArrayBuilder undergradTAsArrayBuilder = Json.createArrayBuilder();
		Iterator<TeachingAssistantPrototype> tasIterator = dataManager.teachingAssistantsIterator();
		while (tasIterator.hasNext()) {
			TeachingAssistantPrototype ta = tasIterator.next();
			JsonObject taJson = Json.createObjectBuilder()
					.add(JSON_NAME, ta.getName())
					.add(JSON_EMAIL, ta.getEmail())
					.add(JSON_TYPE, ta.getType().toString()).build();
			if (ta.getType().equals(TAType.Graduate.toString())) {
				gradTAsArrayBuilder.add(taJson);
			} else {
				undergradTAsArrayBuilder.add(taJson);
			}
		}
		JsonArray gradTAsArray = gradTAsArrayBuilder.build();
		JsonArray undergradTAsArray = undergradTAsArrayBuilder.build();

		dataManager.setTimeRange("9:00am", "8:00pm");
		// NOW BUILD THE OFFICE HOURS JSON OBJCTS TO SAVE
		JsonArrayBuilder officeHoursArrayBuilder = Json.createArrayBuilder();
		Iterator<TimeSlot> timeSlotsIterator = dataManager.officeHoursIterator();
		while (timeSlotsIterator.hasNext()) {
			TimeSlot timeSlot = timeSlotsIterator.next();
			for (int i = 0; i < DayOfWeek.values().length; i++) {
				DayOfWeek dow = DayOfWeek.values()[i];
				tasIterator = timeSlot.getTAsIterator(dow);
				while (tasIterator.hasNext()) {
					TeachingAssistantPrototype ta = tasIterator.next();
					JsonObject tsJson = Json.createObjectBuilder()
							.add(JSON_START_TIME, timeSlot.getStartTime().replace(":", "_"))
							.add(JSON_DAY_OF_WEEK, dow.toString())
							.add(JSON_NAME, ta.getName()).build();
					officeHoursArrayBuilder.add(tsJson);
				}
			}
		}
		JsonArray officeHoursArray = officeHoursArrayBuilder.build();

		dataManager.setTimeRange((String) ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_START_BOX)).getValue(), (String) ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_END_BOX)).getValue());

		JsonArrayBuilder lectureArrayBuilder = Json.createArrayBuilder();
		Iterator<Lecture> lectureIterator = dataManager.lecturesIterator();
		while (lectureIterator.hasNext()) {
			Lecture lec = lectureIterator.next();
			JsonObject lecJson = Json.createObjectBuilder()
					.add(JSON_SECTIONS, lec.getSection())
					.add(JSON_DAYS, lec.getDay())
					.add(JSON_TIME, lec.getTime())
					.add(JSON_ROOM, lec.getRoom()).build();
			lectureArrayBuilder.add(lecJson);
		}
		JsonArray lectureArray = lectureArrayBuilder.build();

		JsonArrayBuilder recitationArrayBuilder = Json.createArrayBuilder();
		Iterator<Recitation> recitationIterator = dataManager.recitationIterator();
		while (recitationIterator.hasNext()) {
			Recitation rec = recitationIterator.next();
			JsonObject recJson = Json.createObjectBuilder()
					.add(JSON_SECTIONS, rec.getSection())
					.add(JSON_DAY_TIME, rec.getDay())
					.add(JSON_LOCATION, rec.getRoom())
					.add(JSON_TA1, rec.getTa1())
					.add(JSON_TA2, rec.getTa2()).build();
			recitationArrayBuilder.add(recJson);
		}
		JsonArray recitationArray = recitationArrayBuilder.build();

		JsonArrayBuilder labArrayBuilder = Json.createArrayBuilder();
		Iterator<Lab> labIterator = dataManager.labIterator();
		while (labIterator.hasNext()) {
			Lab lab = labIterator.next();
			JsonObject labJson = Json.createObjectBuilder()
					.add(JSON_SECTIONS, lab.getSection())
					.add(JSON_DAY_TIME, lab.getDay())
					.add(JSON_LOCATION, lab.getRoom())
					.add(JSON_TA1, lab.getTa1())
					.add(JSON_TA2, lab.getTa2()).build();
			labArrayBuilder.add(labJson);
		}
		JsonArray labArray = labArrayBuilder.build();

		JsonArrayBuilder scheduleArrayBuilder = Json.createArrayBuilder();
		Iterator<Schedule> scheduleIterator = dataManager.scheduleIterator();
		while (scheduleIterator.hasNext()) {
			Schedule schedule = scheduleIterator.next();
			JsonObject scheduleJson = Json.createObjectBuilder()
					.add(JSON_TYPE, schedule.getType())
					.add(JSON_DATE, schedule.getDate())
					.add(JSON_TITLE, schedule.getTitle())
					.add(JSON_TOPIC, schedule.getTopic())
					.add(JSON_LINK, schedule.getLink()).build();
			scheduleArrayBuilder.add(scheduleJson);
		}
		JsonArray scheduleArray = scheduleArrayBuilder.build();

		String startHour = (String) ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_START_BOX)).getValue();
		String endHour = (String) ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_END_BOX)).getValue();
		int startTime = Integer.parseInt(startHour.substring(0, startHour.indexOf(":")));
		if (startHour.substring(startHour.length() - 2).equals("pm") && startTime != 12) {
			startTime += 12;
		}
		int endTime = Integer.parseInt(endHour.substring(0, endHour.indexOf(":")));
		if (endHour.substring(endHour.length() - 2).equals("pm") && endTime != 12) {
			endTime += 12;
		}
		// THEN PUT IT ALL TOGETHER IN A JsonObject
		JsonObject dataManagerJSO = Json.createObjectBuilder()
				//BUILD SITE SECTION
				.add(JSON_SUBJECT, "" + ((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getValue())
				.add(JSON_NUMBER, "" + ((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getValue())
				.add(JSON_SEMESTER, "" + ((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getValue())
				.add(JSON_YEAR, "" + ((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getValue())
				.add(JSON_TITLE, "" + ((TextField) gui.getGUINode(SI_TITLE_BOX)).getText())
				.add(JSON_EXPORT, ((Label) gui.getGUINode(SI_EXPORT_TEXT)).getText())
				.add(JSON_ICONS, logoJson)
				.add(JSON_INSTRUCTOR, instructorJson)
				.add(JSON_PAGES, pagesJson)
				//SYLLABUS SECTION
				.add(JSON_DESCRIPTION, ((TextArea) gui.getGUINode(SY_DESCRIPTION_AREA)).getText())
				.add(JSON_TOPICS, ((TextArea) gui.getGUINode(SY_TOPICS_AREA)).getText())
				.add(JSON_PREREQ, ((TextArea) gui.getGUINode(SY_PREREQUISITES_AREA)).getText())
				.add(JSON_OUTCOMES, ((TextArea) gui.getGUINode(SY_OUTCOMES_AREA)).getText())
				.add(JSON_TEXTBOOKS, ((TextArea) gui.getGUINode(SY_TEXTBOOKS_AREA)).getText())
				.add(JSON_COMPONENTS, ((TextArea) gui.getGUINode(SY_COMPONENTS_AREA)).getText())
				.add(JSON_NOTE, ((TextArea) gui.getGUINode(SY_NOTE_AREA)).getText())
				.add(JSON_DISHONESTY, ((TextArea) gui.getGUINode(SY_DISHONESTY_AREA)).getText())
				.add(JSON_ASSISTANCE, ((TextArea) gui.getGUINode(SY_ASSISTANCE_AREA)).getText())
				//MEETING TIMES SECTIONS
				.add(JSON_LECTURES, lectureArray)
				.add(JSON_RECITATIONS, recitationArray)
				.add(JSON_LABS, labArray)
				//BUILD OH SECTION
				.add(JSON_START_HOUR, "" + startTime)
				.add(JSON_END_HOUR, "" + endTime)
				.add(JSON_GRAD_TAS, gradTAsArray)
				.add(JSON_UNDERGRAD_TAS, undergradTAsArray)
				.add(JSON_OFFICE_HOURS, officeHoursArray)
				//BUILD SCHEDULE SECTION
				.add(JSON_STARTING_DATE, ("" + ((DatePicker) gui.getGUINode(SC_START_DATE_BOX)).getValue()))
				.add(JSON_ENDING_DATE, ("" + ((DatePicker) gui.getGUINode(SC_END_DATE_BOX)).getValue()))
				.add(JSON_SCHEDULE, scheduleArray)
				.build();

		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(filePath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(filePath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public void saveComboOptions() throws IOException {
		File file = new File(COMBO_PATH);
		AppGUIModule gui = app.getGUIModule();
		String absolute = file.getAbsolutePath();

		ObservableList subList = ((ComboBox) gui.getGUINode(SI_SUBJECT_BOX)).getItems();
		JsonArrayBuilder subjectArrayBuilder = Json.createArrayBuilder();
		for (int i = 0; i < subList.size(); i++) {
			subjectArrayBuilder.add((String) subList.get(i));
		}
		JsonArray subjectArray = subjectArrayBuilder.build();

		ObservableList numList = ((ComboBox) gui.getGUINode(SI_NUMBER_BOX)).getItems();
		JsonArrayBuilder numberArrayBuilder = Json.createArrayBuilder();
		for (int i = 0; i < numList.size(); i++) {
			numberArrayBuilder.add((String) numList.get(i));
		}
		JsonArray numberArray = numberArrayBuilder.build();

		ObservableList semList = ((ComboBox) gui.getGUINode(SI_SEMESTER_BOX)).getItems();
		JsonArrayBuilder semesterArrayBuilder = Json.createArrayBuilder();
		for (int i = 0; i < semList.size(); i++) {
			semesterArrayBuilder.add((String) semList.get(i));
		}
		JsonArray semesterArray = semesterArrayBuilder.build();

		ObservableList yearList = ((ComboBox) gui.getGUINode(SI_YEAR_BOX)).getItems();
		JsonArrayBuilder yearArrayBuilder = Json.createArrayBuilder();
		for (int i = 0; i < yearList.size(); i++) {
			yearArrayBuilder.add((String) yearList.get(i));
		}
		JsonArray yearArray = yearArrayBuilder.build();

		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add(JSON_SUBJECT, subjectArray)
				.add(JSON_NUMBER, numberArray)
				.add(JSON_SEMESTER, semesterArray)
				.add(JSON_YEAR, yearArray)
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(absolute);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(absolute);
		pw.write(prettyPrinted);
		pw.close();
	}

	// IMPORTING/EXPORTING DATA IS USED WHEN WE READ/WRITE DATA IN AN
	// ADDITIONAL FORMAT USEFUL FOR ANOTHER PURPOSE, LIKE ANOTHER APPLICATION
	@Override
	public void importData(AppDataComponent data, String filePath) throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String exportData(AppDataComponent data, String filePath) throws IOException {
		AppGUIModule gui = app.getGUIModule();
		String export = ((Label) gui.getGUINode(SI_EXPORT_TEXT)).getText();
		export = export.substring(2);
		File file = new File(export);
		String absoluteExport = file.getAbsolutePath() + "/";
		file = new File(absoluteExport);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			deleteDirectory(file);
			file.mkdirs();
		}
		//EXPORT HTML FILES
		if (((CheckBox) gui.getGUINode(SI_HOME_BOX)).isSelected()) {
			String homeHTML = "app_data/html/index.html";
			File htmlFile = new File(homeHTML);
			Files.copy(htmlFile.toPath(), new File(absoluteExport + homeHTML.substring(homeHTML.lastIndexOf("/")) + "/").toPath(), REPLACE_EXISTING);
		}
		if (((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)).isSelected()) {
			String syllabusHTML = "app_data/html/syllabus.html";
			File syllabusFile = new File(syllabusHTML);
			Files.copy(syllabusFile.toPath(), new File(absoluteExport + syllabusHTML.substring(syllabusHTML.lastIndexOf("/")) + "/").toPath(), REPLACE_EXISTING);
		}
		if (((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)).isSelected()) {
			String scheduleHTML = "app_data/html/schedule.html";
			File scheduleFile = new File(scheduleHTML);
			Files.copy(scheduleFile.toPath(), new File(absoluteExport + scheduleHTML.substring(scheduleHTML.lastIndexOf("/")) + "/").toPath(), REPLACE_EXISTING);
		}
		if (((CheckBox) gui.getGUINode(SI_HWS_BOX)).isSelected()) {
			String hwsHTML = "app_data/html/hws.html";
			File hwsFile = new File(hwsHTML);
			Files.copy(hwsFile.toPath(), new File(absoluteExport + hwsHTML.substring(hwsHTML.lastIndexOf("/")) + "/").toPath(), REPLACE_EXISTING);
		}
		//EXPORT JS FILES
		String jsFolder = "app_data/js";
		File jsFile = new File(jsFolder);
		String jsPath = absoluteExport + jsFolder.substring(jsFolder.lastIndexOf("/")) + "/";
		if (!new File(jsPath).exists()) {
			Files.copy(jsFile.toPath(), new File(jsPath).toPath(), REPLACE_EXISTING);
		}
		Files.copy(new File(jsFolder + "/CourseBuilder.js").toPath(), new File(jsPath + "/CourseBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/HWsBuilder.js").toPath(), new File(jsPath + "/HWsBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/OfficeHoursBuilder.js").toPath(), new File(jsPath + "/OfficeHoursBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/PageBuilder.js").toPath(), new File(jsPath + "/PageBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/ScheduleBuilder.js").toPath(), new File(jsPath + "/ScheduleBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/SectionsBuilder.js").toPath(), new File(jsPath + "/SectionsBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/SyllabusBuilder.js").toPath(), new File(jsPath + "/SyllabusBuilder.js" + "/").toPath(), REPLACE_EXISTING);
		Files.copy(new File(jsFolder + "/jquery.min.js").toPath(), new File(jsPath + "/jquery.min.js" + "/").toPath(), REPLACE_EXISTING);
		//EXPORT CSS FILES
		String cssFolder = "app_data/css";
		File cssFile = new File(cssFolder);
		String cssPath = absoluteExport + cssFolder.substring(cssFolder.lastIndexOf("/")) + "/";
		if (!new File(cssPath).exists()) {
			Files.copy(cssFile.toPath(), new File(cssPath).toPath(), REPLACE_EXISTING);
		}
		Files.copy(new File(cssFolder + "/course_homepage_layout.css").toPath(), new File(cssPath + "/course_homepage_layout.css" + "/").toPath(), REPLACE_EXISTING);
		String style = ((ComboBox) gui.getGUINode(SI_FONT_BOX)).getValue().toString();
		Files.copy(new File("work/css/" + style).toPath(), new File(cssPath + "/" + "sea_wolf.css" + "/").toPath(), REPLACE_EXISTING);
		//EXPORT IMAGES
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		String imagePath = absoluteExport + "/images/";
		if (!new File(imagePath).exists()) {
			new File(imagePath).mkdir();
		}
		String sourcePath = "images/icons/";
		String fav = sourcePath + props.getProperty(SI_FAVICON_BASE_ICON);
		Files.copy(new File(fav).toPath(), new File(imagePath + props.getProperty(SI_FAVICON_BASE_ICON) + "/").toPath(), REPLACE_EXISTING);
		String nav = sourcePath + props.getProperty(SI_NAVBAR_BASE_ICON);
		Files.copy(new File(nav).toPath(), new File(imagePath + props.getProperty(SI_NAVBAR_BASE_ICON) + "/").toPath(), REPLACE_EXISTING);
		String left = sourcePath + props.getProperty(SI_LEFT_BASE_ICON);
		Files.copy(new File(left).toPath(), new File(imagePath + props.getProperty(SI_LEFT_BASE_ICON) + "/").toPath(), REPLACE_EXISTING);
		String right = sourcePath + props.getProperty(SI_RIGHT_BASE_ICON);
		Files.copy(new File(right).toPath(), new File(imagePath + props.getProperty(SI_RIGHT_BASE_ICON) + "/").toPath(), REPLACE_EXISTING);
		//MAKE JSON FILES
		new File(jsPath + "/PageData.json").createNewFile();
		new File(jsPath + "/OfficeHoursData.json").createNewFile();
		new File(jsPath + "/SectionsData.json").createNewFile();
		//new File(jsPath + "/SyllabusData.json").createNewFile();
		new File(jsPath + "/ScheduleData.json").createNewFile();
		new File(jsPath + "/CourseData.json").createNewFile();
		exportPageData(jsPath, filePath);
		exportOfficeHoursData(jsPath, filePath);
		exportSectionsData(jsPath, filePath);
		exportSyllabusData(jsPath, filePath);
		exportScheduleData(jsPath, filePath);
		exportCourseData(jsPath, filePath);
		return absoluteExport;
	}

	public void exportPageData(String jsPath, String filePath) throws IOException {
		jsPath = jsPath + "/PageData.json";
		File file = new File("work/" + filePath);
		JsonObject srcJson = loadJSONFile(file.getAbsolutePath());
		AppGUIModule gui = app.getGUIModule();

		JsonObject jsonIcons = srcJson.getJsonObject(JSON_ICONS);
		JsonObject favJson = Json.createObjectBuilder()
				.add("href", "./images/" + jsonIcons.getString(JSON_FAVICON))
				.build();
		JsonObject navJson = Json.createObjectBuilder()
				.add("href", "http://www.stonybrook.edu")
				.add("src", "./images/" + jsonIcons.getString(JSON_NAVBAR))
				.build();
		JsonObject leftJson = Json.createObjectBuilder()
				.add("href", "http://www.stonybrook.edu")
				.add("src", "./images/" + jsonIcons.getString(JSON_LEFT))
				.build();
		JsonObject rightJson = Json.createObjectBuilder()
				.add("href", "http://www.stonybrook.edu")
				.add("src", "./images/" + jsonIcons.getString(JSON_RIGHT))
				.build();
		JsonObject logoJson = Json.createObjectBuilder()
				.add(JSON_FAVICON, favJson)
				.add(JSON_NAVBAR, navJson)
				.add(JSON_LEFT, leftJson)
				.add(JSON_RIGHT, rightJson)
				.build();
		JsonObject jsonInstructor = srcJson.getJsonObject(JSON_INSTRUCTOR);
		JsonArrayBuilder instructorHoursArray = Json.createArrayBuilder();
		String instructorHours = ((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX)).getText();
		if (instructorHours.equals("")) {
			instructorHoursArray.add("");
		} else {
			while (instructorHours.contains("day")) {
				int dayIndex = instructorHours.indexOf("day");
				int dayComma = instructorHours.indexOf(",");
				String dayValue = instructorHours.substring(dayIndex + 5, dayComma);
				dayValue = dayValue.replace("\"", " ");
				dayValue = dayValue.trim();
				instructorHours = instructorHours.substring(dayComma + 1);

				int timeIndex = instructorHours.indexOf("time");
				int brace = instructorHours.indexOf("}");
				String timeValue = instructorHours.substring(timeIndex + 6, brace);
				timeValue = timeValue.replace("\"", " ");
				timeValue = timeValue.trim();
				instructorHours = instructorHours.substring(brace + 2);

				JsonObject instructorHoursJson = Json.createObjectBuilder()
						.add("day", dayValue)
						.add(JSON_TIME, timeValue)
						.build();
				instructorHoursArray.add(instructorHoursJson);
			}
		}
		JsonObject instructorJson = Json.createObjectBuilder()
				.add(JSON_NAME, jsonInstructor.getString(JSON_NAME))
				.add(JSON_LINK, jsonInstructor.getString(JSON_LINK))
				.add(JSON_EMAIL, jsonInstructor.getString(JSON_EMAIL))
				.add(JSON_ROOM, jsonInstructor.getString(JSON_ROOM))
				.add("photo", "")
				.add(JSON_HOURS, instructorHoursArray)
				.build();

		JsonArrayBuilder pageArray = Json.createArrayBuilder();
		if (((CheckBox) gui.getGUINode(SI_HOME_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "Home")
					.add(JSON_LINK, "index.html")
					.build();
			pageArray.add(pageJson);
		}
		if (((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "Syllabus")
					.add(JSON_LINK, "syllabus.html")
					.build();
			pageArray.add(pageJson);
		}
		if (((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "Schedule")
					.add(JSON_LINK, "schedule.html")
					.build();
			pageArray.add(pageJson);
		}
		if (((CheckBox) gui.getGUINode(SI_HWS_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "HWs")
					.add(JSON_LINK, "hws.html")
					.build();
			pageArray.add(pageJson);
		}
		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add(JSON_SUBJECT, srcJson.getString(JSON_SUBJECT))
				.add(JSON_NUMBER, srcJson.getString(JSON_NUMBER))
				.add(JSON_SEMESTER, srcJson.getString(JSON_SEMESTER))
				.add(JSON_YEAR, srcJson.getString(JSON_YEAR))
				.add(JSON_TITLE, srcJson.getString(JSON_TITLE))
				.add(JSON_ICONS, logoJson)
				.add(JSON_INSTRUCTOR, instructorJson)
				.add(JSON_PAGES, pageArray)
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(jsPath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(jsPath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public void exportOfficeHoursData(String jsPath, String filePath) throws IOException {
		jsPath = jsPath + "/OfficeHoursData.json";
		File file = new File("work/" + filePath);
		JsonObject srcJson = loadJSONFile(file.getAbsolutePath());
		AppGUIModule gui = app.getGUIModule();

		JsonObject jsonInstructor = srcJson.getJsonObject(JSON_INSTRUCTOR);
		JsonArrayBuilder instructorHoursArray = Json.createArrayBuilder();
		String instructorHours = ((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX)).getText();
		if (instructorHours.equals("")) {
			instructorHoursArray.add("");
		} else {
			while (instructorHours.contains("day")) {
				int dayIndex = instructorHours.indexOf("day");
				int dayComma = instructorHours.indexOf(",");
				String dayValue = instructorHours.substring(dayIndex + 5, dayComma);
				dayValue = dayValue.replace("\"", " ");
				dayValue = dayValue.trim();
				instructorHours = instructorHours.substring(dayComma + 1);

				int timeIndex = instructorHours.indexOf("time");
				int brace = instructorHours.indexOf("}");
				String timeValue = instructorHours.substring(timeIndex + 6, brace);
				timeValue = timeValue.replace("\"", " ");
				timeValue = timeValue.trim();
				instructorHours = instructorHours.substring(brace + 2);

				JsonObject instructorHoursJson = Json.createObjectBuilder()
						.add("day", dayValue)
						.add(JSON_TIME, timeValue)
						.build();
				instructorHoursArray.add(instructorHoursJson);
			}
		}
		JsonObject instructorJson = Json.createObjectBuilder()
				.add(JSON_NAME, jsonInstructor.getString(JSON_NAME))
				.add(JSON_LINK, jsonInstructor.getString(JSON_LINK))
				.add(JSON_EMAIL, jsonInstructor.getString(JSON_EMAIL))
				.add(JSON_ROOM, jsonInstructor.getString(JSON_ROOM))
				.add("photo", "")
				.add(JSON_HOURS, instructorHoursArray)
				.build();

		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add(JSON_START_HOUR, srcJson.getString(JSON_START_HOUR))
				.add(JSON_END_HOUR, srcJson.getString(JSON_END_HOUR))
				.add(JSON_INSTRUCTOR, instructorJson)
				.add(JSON_GRAD_TAS, srcJson.getJsonArray(JSON_GRAD_TAS))
				.add(JSON_UNDERGRAD_TAS, srcJson.getJsonArray(JSON_UNDERGRAD_TAS))
				.add(JSON_OFFICE_HOURS, srcJson.getJsonArray(JSON_OFFICE_HOURS))
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(jsPath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(jsPath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public void exportSectionsData(String jsPath, String filePath) throws IOException {
		jsPath = jsPath + "/SectionsData.json";
		File file = new File("work/" + filePath);
		JsonObject srcJson = loadJSONFile(file.getAbsolutePath());

		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add(JSON_LECTURES, srcJson.getJsonArray(JSON_LECTURES))
				.add(JSON_LABS, srcJson.getJsonArray(JSON_LABS))
				.add(JSON_RECITATIONS, srcJson.getJsonArray(JSON_RECITATIONS))
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(jsPath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(jsPath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public void exportSyllabusData(String jsPath, String filePath) throws IOException {
		jsPath = jsPath + "/SyllabusData.json";
		File file = new File("work/" + filePath);
		JsonObject srcJson = loadJSONFile(file.getAbsolutePath());
		AppGUIModule gui = app.getGUIModule();

		String topicString = ((TextArea) gui.getGUINode(SY_TOPICS_AREA)).getText();
		JsonArrayBuilder topicsJsonArray = Json.createArrayBuilder();
		if (topicString.equals("")) {
			topicsJsonArray.add("");
		} else {
			topicString = topicString.substring(1, topicString.length() - 1);
			String[] topicArr = topicString.split(",\n");
			for (String str : topicArr) {
				str = str.trim();
				str = str.substring(1, str.length() - 1);
				topicsJsonArray.add(str);
			}
		}

		String outcomeString = ((TextArea) gui.getGUINode(SY_OUTCOMES_AREA)).getText();
		JsonArrayBuilder outcomeJsonArray = Json.createArrayBuilder();
		if (outcomeString.equals("")) {
			outcomeJsonArray.add("");
		} else {
			outcomeString = outcomeString.substring(1, outcomeString.length() - 1);
			String[] outcomeArr = outcomeString.split(",\n");
			for (String str : outcomeArr) {
				str = str.trim();
				str = str.substring(1, str.length() - 1);
				outcomeJsonArray.add(str);
			}
		}

		JsonArrayBuilder textbooksJsonArray = Json.createArrayBuilder();
		String textbookText = ((TextArea) gui.getGUINode(SY_TEXTBOOKS_AREA)).getText();
		if (textbookText.equals("")) {
			textbooksJsonArray.add("");
		} else {
			while (textbookText.contains("title")) {
				int titleIndex = textbookText.indexOf("title");
				int titleComma = textbookText.indexOf(",");
				String titleValue = textbookText.substring(titleIndex + 7, titleComma);
				titleValue = titleValue.replace("\"", " ");
				titleValue = titleValue.trim();
				textbookText = textbookText.substring(titleComma + 1);

				int linkIndex = textbookText.indexOf("link");
				int linkComma = textbookText.indexOf(",");
				String linkValue = textbookText.substring(linkIndex + 6, linkComma);
				linkValue = linkValue.replace("\"", " ");
				linkValue = linkValue.trim();
				textbookText = textbookText.substring(linkComma + 1);

				int photoIndex = textbookText.indexOf("photo");
				int photoComma = textbookText.indexOf(",");
				String photoValue = textbookText.substring(photoIndex + 7, photoComma);
				photoValue = photoValue.replace("\"", " ");
				photoValue = photoValue.trim();
				textbookText = textbookText.substring(photoComma + 1);

				JsonArrayBuilder authorJsonArray = Json.createArrayBuilder();
				String authorsString = textbookText.substring(textbookText.indexOf("[") + 1, textbookText.indexOf("]"));
				String[] authorArr = authorsString.split(",");
				for (String str : authorArr) {
					str = str.trim();
					str = str.substring(1, str.length() - 1);
					authorJsonArray.add(str);
				}
				textbookText = textbookText.substring(textbookText.indexOf("]") + 2);

				int publisherIndex = textbookText.indexOf("publisher");
				int publisherComma = textbookText.indexOf(",");
				String publisherValue = textbookText.substring(publisherIndex + 11, publisherComma);
				publisherValue = publisherValue.replace("\"", " ");
				publisherValue = publisherValue.trim();
				textbookText = textbookText.substring(publisherComma + 1);

				int yearIndex = textbookText.indexOf("year");
				int brace = textbookText.indexOf("}");
				String yearValue = textbookText.substring(yearIndex + 6, brace);
				yearValue = yearValue.replace("\"", " ");
				yearValue = yearValue.trim();
				textbookText = textbookText.substring(brace + 2);

				JsonObject textJson = Json.createObjectBuilder()
						.add(JSON_TITLE, titleValue)
						.add(JSON_LINK, linkValue)
						.add("photo", photoValue)
						.add("authors", authorJsonArray)
						.add("publisher", publisherValue)
						.add(JSON_YEAR, yearValue)
						.build();
				textbooksJsonArray.add(textJson);
			}
		}

		JsonArrayBuilder componentsJsonArray = Json.createArrayBuilder();
		String componentText = ((TextArea) gui.getGUINode(SY_COMPONENTS_AREA)).getText();
		if (componentText.equals("")) {
			componentsJsonArray.add("");
		} else {
			while (componentText.contains("name")) {
				int nameIndex = componentText.indexOf("name");
				int nameComma = componentText.indexOf(",");
				String nameValue = componentText.substring(nameIndex + 6, nameComma);
				nameValue = nameValue.replace("\"", " ");
				nameValue = nameValue.trim();
				componentText = componentText.substring(nameComma + 1);
				int descriptionIndex = componentText.indexOf("description");
				int descriptionComma = componentText.indexOf(",");
				String descriptionValue = componentText.substring(descriptionIndex + 13, descriptionComma);
				descriptionValue = descriptionValue.replace("\"", " ");
				descriptionValue = descriptionValue.trim();
				componentText = componentText.substring(descriptionComma + 1);

				int weightIndex = componentText.indexOf("weight");
				int brace = componentText.indexOf("}");
				String weightValue = componentText.substring(weightIndex + 8, brace);
				weightValue = weightValue.replace("\"", " ");
				weightValue = weightValue.trim();
				componentText = componentText.substring(brace + 2);

				JsonObject gradedJson = Json.createObjectBuilder()
						.add(JSON_NAME, nameValue)
						.add(JSON_DESCRIPTION, descriptionValue)
						.add("weight", weightValue)
						.build();
				componentsJsonArray.add(gradedJson);
			}
		}

		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add(JSON_DESCRIPTION, srcJson.getString(JSON_DESCRIPTION))
				.add(JSON_TOPICS, topicsJsonArray)
				.add(JSON_PREREQ, srcJson.getString(JSON_PREREQ))
				.add(JSON_OUTCOMES, outcomeJsonArray)
				.add(JSON_TEXTBOOKS, textbooksJsonArray)
				.add(JSON_COMPONENTS, componentsJsonArray)
				.add(JSON_NOTE, srcJson.getString(JSON_NOTE))
				.add(JSON_DISHONESTY, srcJson.getString(JSON_DISHONESTY))
				.add(JSON_ASSISTANCE, srcJson.getString(JSON_ASSISTANCE))
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(jsPath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(jsPath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public void exportScheduleData(String jsPath, String filePath) throws IOException {
		jsPath = jsPath + "/ScheduleData.json";
		File file = new File("work/" + filePath);
		JsonObject srcJson = loadJSONFile(file.getAbsolutePath());

		String startMonMonth = srcJson.getString(JSON_STARTING_DATE).substring(5, 7);
		String startMonDay = srcJson.getString(JSON_STARTING_DATE).substring(8);
		String startFriMonth = srcJson.getString(JSON_ENDING_DATE).substring(5, 7);
		String startFriDay = srcJson.getString(JSON_ENDING_DATE).substring(8);

		JsonArray scheduleArray = srcJson.getJsonArray(JSON_SCHEDULE);
		JsonArrayBuilder holidayArray = Json.createArrayBuilder();
		JsonArrayBuilder referenceArray = Json.createArrayBuilder();
		JsonArrayBuilder lectureArray = Json.createArrayBuilder();
		JsonArrayBuilder recitationArray = Json.createArrayBuilder();
		JsonArrayBuilder hwArray = Json.createArrayBuilder();
		for (int i = 0; i < scheduleArray.size(); i++) {
			JsonObject object = scheduleArray.getJsonObject(i);
			String type = object.getString(JSON_TYPE);
			String month = object.getString(JSON_DATE).substring(0, object.getString(JSON_DATE).indexOf("/"));
			int monthInt = Integer.parseInt(month);
			month = monthInt + "";
			String day = object.getString(JSON_DATE).substring(object.getString(JSON_DATE).indexOf("/") + 1, object.getString(JSON_DATE).lastIndexOf("/"));
			int dayInt = Integer.parseInt(day);
			day = dayInt + "";
			String title = object.getString(JSON_TITLE);
			String topic = object.getString(JSON_TOPIC);
			String link = object.getString(JSON_LINK);
			if (type.equals("Holiday")) {
				JsonObject holidayObj = Json.createObjectBuilder()
						.add("month", month)
						.add("day", day)
						.add(JSON_TITLE, title)
						.add(JSON_LINK, link)
						.build();
				holidayArray.add(holidayObj);
			} else if (type.equals("HW")) {
				JsonObject hwObj = Json.createObjectBuilder()
						.add("month", month)
						.add("day", day)
						.add(JSON_TITLE, title)
						.add(JSON_TOPIC, topic)
						.add(JSON_LINK, link)
						.add(JSON_TIME, "")
						.add("criteria", "none")
						.build();
				hwArray.add(hwObj);
			} else {
				JsonObject genericObj = Json.createObjectBuilder()
						.add("month", month)
						.add("day", day)
						.add(JSON_TITLE, title)
						.add(JSON_TOPIC, topic)
						.add(JSON_LINK, link)
						.build();
				if (type.equals("Lecture")) {
					lectureArray.add(genericObj);
				} else if (type.equals("Reference")) {
					referenceArray.add(genericObj);
				} else {
					recitationArray.add(genericObj);
				}
			}
		}

		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add("startingMondayMonth", startMonMonth)
				.add("startingMondayDay", startMonDay)
				.add("endingFridayMonth", startFriMonth)
				.add("endingFridayDay", startFriDay)
				.add("holidays", holidayArray)
				.add("lectures", lectureArray)
				.add("references", referenceArray)
				.add("recitations", recitationArray)
				.add("hws", hwArray)
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(jsPath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(jsPath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public void exportCourseData(String jsPath, String filePath) throws IOException {
		jsPath = jsPath + "/CourseData.json";
		File file = new File("work/" + filePath);
		JsonObject srcJson = loadJSONFile(file.getAbsolutePath());
		AppGUIModule gui = app.getGUIModule();

		JsonObject jsonIcons = srcJson.getJsonObject(JSON_ICONS);
		JsonObject favJson = Json.createObjectBuilder()
				.add("href", "./images/" + jsonIcons.getString(JSON_FAVICON))
				.build();
		JsonObject navJson = Json.createObjectBuilder()
				.add("href", "http://www.stonybrook.edu")
				.add("src", "./images/" + jsonIcons.getString(JSON_NAVBAR))
				.add("alt", "Stony Brook University")
				.build();
		JsonObject leftJson = Json.createObjectBuilder()
				.add("href", "http://www.stonybrook.edu")
				.add("src", "./images/" + jsonIcons.getString(JSON_LEFT))
				.add("alt", "Stony Brook University")
				.build();
		JsonObject rightJson = Json.createObjectBuilder()
				.add("href", "http://www.stonybrook.edu")
				.add("src", "./images/" + jsonIcons.getString(JSON_RIGHT))
				.add("alt", "Stony Brook University Computer Science Department")
				.build();
		JsonObject logoJson = Json.createObjectBuilder()
				.add(JSON_FAVICON, favJson)
				.add(JSON_NAVBAR, navJson)
				.add(JSON_LEFT, leftJson)
				.add(JSON_RIGHT, rightJson)
				.build();

		JsonObject jsonInstructor = srcJson.getJsonObject(JSON_INSTRUCTOR);
		JsonObject authorJson = Json.createObjectBuilder()
				.add(JSON_NAME, jsonInstructor.getString(JSON_NAME))
				.add(JSON_LINK, jsonInstructor.getString(JSON_LINK))
				.build();

		JsonArrayBuilder pageArray = Json.createArrayBuilder();
		if (((CheckBox) gui.getGUINode(SI_HOME_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "Home")
					.add(JSON_LINK, "index.html")
					.build();
			pageArray.add(pageJson);
		}
		if (((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "Syllabus")
					.add(JSON_LINK, "syllabus.html")
					.build();
			pageArray.add(pageJson);
		}
		if (((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "Schedule")
					.add(JSON_LINK, "schedule.html")
					.build();
			pageArray.add(pageJson);
		}
		if (((CheckBox) gui.getGUINode(SI_HWS_BOX)).isSelected()) {
			JsonObject pageJson = Json.createObjectBuilder()
					.add(JSON_NAME, "HWs")
					.add(JSON_LINK, "hws.html")
					.build();
			pageArray.add(pageJson);
		}

		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add("code", srcJson.getString(JSON_SUBJECT) + " " + srcJson.getString(JSON_NUMBER))
				.add(JSON_SEMESTER, srcJson.getString(JSON_SEMESTER) + " " + srcJson.getString(JSON_YEAR))
				.add(JSON_TITLE, srcJson.getString(JSON_TITLE))
				.add(JSON_ICONS, logoJson)
				.add("author", authorJson)
				.add(JSON_PAGES, pageArray)
				.build();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		StringWriter sw = new StringWriter();
		JsonWriter jsonWriter = writerFactory.createWriter(sw);
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();

		// INIT THE WRITER
		OutputStream os = new FileOutputStream(jsPath);
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter(jsPath);
		pw.write(prettyPrinted);
		pw.close();
	}

	public static void deleteDirectory(File file)
			throws IOException {

		if (file.isDirectory()) {

			//directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();

			} else {

				//list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);

					//recursive delete
					deleteDirectory(fileDelete);
				}

				//check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			//if file, then delete it
			file.delete();
		}
	}
}
