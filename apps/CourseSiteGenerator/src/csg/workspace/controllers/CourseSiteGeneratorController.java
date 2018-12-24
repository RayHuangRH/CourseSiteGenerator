package csg.workspace.controllers;

import djf.modules.AppGUIModule;
import djf.ui.dialogs.AppDialogsFacade;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab;
import csg.data.Lecture;
import csg.data.Recitation;
import csg.data.Schedule;
import csg.data.TAType;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;
import csg.files.CourseSiteGeneratorFiles;
import csg.transactions.*;
import csg.workspace.dialogs.TADialog;
import static csg.workspace.style.CSGStyle.CLASS_ICON;
import static djf.AppPropertyType.LOAD_ERROR_CONTENT;
import static djf.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.AppPropertyType.LOAD_IMAGE_TITLE;
import static djf.modules.AppGUIModule.ENABLED;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import properties_manager.PropertiesManager;

/**
 *
 * @author McKillaGorilla
 */
public class CourseSiteGeneratorController {

	CourseSiteGeneratorApp app;

	public CourseSiteGeneratorController(CourseSiteGeneratorApp initApp) {
		app = initApp;
	}

	public void processEditTextField(TextField field, String oldValue, String newValue) {
		EditTextField_Transaction transaction = new EditTextField_Transaction(field, oldValue, newValue);
		app.processTransaction(transaction);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processEditTextArea(TextArea area, String oldValue, String newValue) {
		EditTextArea_Transaction transaction = new EditTextArea_Transaction(area, oldValue, newValue);
		app.processTransaction(transaction);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processSelectDate(DatePicker date, LocalDate oldValue, LocalDate newValue) {
		EditDatePicker_Transaction transaction = new EditDatePicker_Transaction(date, oldValue, newValue);
		app.processTransaction(transaction);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processSelectCombo(ComboBox combo, Object oldValue, Object newValue) {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		ComboSelect_Transaction transaction = new ComboSelect_Transaction(data, combo, oldValue, newValue);
		app.processTransaction(transaction);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processAddTA() {
		AppGUIModule gui = app.getGUIModule();
		TextField nameTF = (TextField) gui.getGUINode(OH_NAME_TEXT_FIELD);
		String name = nameTF.getText();
		TextField emailTF = (TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD);
		String email = emailTF.getText();
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		TAType type = data.getSelectedType();
		if (data.isLegalNewTA(name, email)) {
			TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name.trim(), email.trim(), type);
			AddTA_Transaction addTATransaction = new AddTA_Transaction(data, ta);
			app.processTransaction(addTATransaction);

			// NOW CLEAR THE TEXT FIELDS
			nameTF.setText("");
			emailTF.setText("");
			nameTF.requestFocus();
		}
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processRemoveTA() {
		AppGUIModule gui = app.getGUIModule();
		TableView<TeachingAssistantPrototype> taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
		ObservableList<TablePosition> selectedCells = taTableView.getSelectionModel().getSelectedCells();
		if (selectedCells.size() > 0) {
			TablePosition cell = selectedCells.get(0);
			CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
			TeachingAssistantPrototype ta = taTableView.getSelectionModel().getSelectedItem();
			HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours = data.getTATimeSlots(ta);
			if (ta != null) {
				RemoveTA_Transaction transaction = new RemoveTA_Transaction(app, ta, officeHours);
				app.processTransaction(transaction);
			} else {
				Stage window = app.getGUIModule().getWindow();
				AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
			}
			int row = cell.getRow();
			cell.getTableView().refresh();
		}
	}

	public void processAddLecture() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		Lecture lecture = new Lecture("?", "?", "?", "?");
		AddLecture_Transaction transaction = new AddLecture_Transaction(data, lecture);
		app.processTransaction(transaction);
	}

	public void processAddRecitation() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		Recitation recitation = new Recitation("?", "?", "?", "?", "?");
		AddRecitation_Transaction transaction = new AddRecitation_Transaction(data, recitation);
		app.processTransaction(transaction);
	}

	public void processAddLab() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		Lab lab = new Lab("?", "?", "?", "?", "?");
		AddLab_Transaction transaction = new AddLab_Transaction(data, lab);
		app.processTransaction(transaction);
	}

	public void processEditLecture(Lecture lecture, String sec, String day, String time, String room) {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		EditLecture_Transaction transaction = new EditLecture_Transaction(data, lecture, sec, day, time, room);
		app.processTransaction(transaction);
		data.sortLecture();
	}
	
	public void processEditRecitation(Recitation recitation, String sec, String day, String room, String ta1, String ta2){
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		EditRecitation_Transaction transaction = new EditRecitation_Transaction(data, recitation, sec, day, room, ta1, ta2);
		app.processTransaction(transaction);
		data.sortRecitation();
	}
	
	public void processEditLab(Lab lab, String sec, String day, String room, String ta1, String ta2){
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		EditLab_Transaction transaction = new EditLab_Transaction(data, lab, sec, day, room, ta1, ta2);
		app.processTransaction(transaction);
		data.sortLab();
	}

	public void processRemoveLecture() {
		AppGUIModule gui = app.getGUIModule();
		TableView<Lecture> lectureTable = (TableView) gui.getGUINode(MT_LECTURES_TABLE);
		ObservableList<TablePosition> selectedCells = lectureTable.getSelectionModel().getSelectedCells();
		if (selectedCells.size() > 0) {
			TablePosition cell = selectedCells.get(0);
			CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
			Lecture lec = lectureTable.getSelectionModel().getSelectedItem();
			if (lec != null) {
				RemoveLecture_Transaction transaction = new RemoveLecture_Transaction(data, lec);
				app.processTransaction(transaction);
			} else {
				Stage window = app.getGUIModule().getWindow();
				AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
			}
			int row = cell.getRow();
			cell.getTableView().refresh();
		}
	}

	public void processRemoveRecitation() {
		AppGUIModule gui = app.getGUIModule();
		TableView<Recitation> recitationTable = (TableView) gui.getGUINode(MT_RECITATION_TABLE);
		ObservableList<TablePosition> selectedCells = recitationTable.getSelectionModel().getSelectedCells();
		if (selectedCells.size() > 0) {
			TablePosition cell = selectedCells.get(0);
			CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
			Recitation rec = recitationTable.getSelectionModel().getSelectedItem();
			if (rec != null) {
				RemoveRecitation_Transaction transaction = new RemoveRecitation_Transaction(data, rec);
				app.processTransaction(transaction);
			} else {
				Stage window = app.getGUIModule().getWindow();
				AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
			}
			int row = cell.getRow();
			cell.getTableView().refresh();
		}
	}

	public void processRemoveLab() {
		AppGUIModule gui = app.getGUIModule();
		TableView<Lab> labTable = (TableView) gui.getGUINode(MT_LAB_TABLE);
		ObservableList<TablePosition> selectedCells = labTable.getSelectionModel().getSelectedCells();
		if (selectedCells.size() > 0) {
			TablePosition cell = selectedCells.get(0);
			CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
			Lab lab = labTable.getSelectionModel().getSelectedItem();
			if (lab != null) {
				RemoveLab_Transaction transaction = new RemoveLab_Transaction(data, lab);
				app.processTransaction(transaction);
			} else {
				Stage window = app.getGUIModule().getWindow();
				AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
			}
			int row = cell.getRow();
			cell.getTableView().refresh();
		}
	}

	public void processAddSchedule() {//FIX FORMATTING FOR DATEPICKER
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		AppGUIModule gui = app.getGUIModule();
		String type = (String) ((ComboBox) gui.getGUINode(SC_TYPE_BOX)).getValue();
		String preformat = ((DatePicker) gui.getGUINode(SC_DATE_BOX)).getValue().toString();
		String date = preformat.substring(preformat.indexOf("-") + 1, preformat.lastIndexOf("-")) + "/";
		date += preformat.substring(preformat.lastIndexOf("-") + 1) + "/";
		date += preformat.substring(0, preformat.indexOf("-"));
		String title = ((TextField) gui.getGUINode(SC_TITLE_BOX)).getText();
		String topic = ((TextField) gui.getGUINode(SC_TOPIC_BOX)).getText();
		String link = ((TextField) gui.getGUINode(SC_LINK_BOX)).getText();
		Schedule schedule = new Schedule(type, date, title, topic, link);
		AddSchedule_Transaction transaction = new AddSchedule_Transaction(data, schedule);
		app.processTransaction(transaction);
		processClearAddEdit();
	}

	public void processEditSchedule(Schedule schedule) {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		AppGUIModule gui = app.getGUIModule();
		String type = (String) ((ComboBox) gui.getGUINode(SC_TYPE_BOX)).getValue();
		String preformat = ((DatePicker) gui.getGUINode(SC_DATE_BOX)).getValue().toString();
		String date = preformat.substring(preformat.indexOf("-") + 1, preformat.lastIndexOf("-")) + "/";
		date += preformat.substring(preformat.lastIndexOf("-") + 1) + "/";
		date += preformat.substring(0, preformat.indexOf("-"));
		String title = ((TextField) gui.getGUINode(SC_TITLE_BOX)).getText();
		String topic = ((TextField) gui.getGUINode(SC_TOPIC_BOX)).getText();
		String link = ((TextField) gui.getGUINode(SC_LINK_BOX)).getText();
		EditSchedule_Transaction transaction = new EditSchedule_Transaction(data, schedule, type, date, title, topic, link);
		app.processTransaction(transaction);
		processClearAddEdit();
	}

	public void processRemoveSchedule() {
		AppGUIModule gui = app.getGUIModule();
		TableView<Schedule> scheduleTable = (TableView) gui.getGUINode(SC_SCHEDULE_TABLE);
		ObservableList<TablePosition> selectedCells = scheduleTable.getSelectionModel().getSelectedCells();
		if (selectedCells.size() > 0) {
			TablePosition cell = selectedCells.get(0);
			CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
			Schedule schedule = scheduleTable.getSelectionModel().getSelectedItem();
			if (schedule != null) {
				RemoveSchedule_Transaction transaction = new RemoveSchedule_Transaction(data, schedule);
				app.processTransaction(transaction);
			} else {
				Stage window = app.getGUIModule().getWindow();
				AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
			}
			int row = cell.getRow();
			cell.getTableView().refresh();
		}
	}

	public void processClickAddEdit(Schedule schedule) {
		AppGUIModule gui = app.getGUIModule();
		((ComboBox) gui.getGUINode(SC_TYPE_BOX)).setValue(schedule.getType());
		((TextField) gui.getGUINode(SC_TITLE_BOX)).setText(schedule.getTitle());
		String date = schedule.getDate();
		int month = Integer.parseInt(date.substring(0, date.indexOf("/")));
		int day = Integer.parseInt(date.substring(date.indexOf("/") + 1, date.lastIndexOf("/")));
		int year = Integer.parseInt(date.substring(date.lastIndexOf("/") + 1));
		((DatePicker) gui.getGUINode(SC_DATE_BOX)).setValue(LocalDate.of(year, month, day));
		((TextField) gui.getGUINode(SC_TOPIC_BOX)).setText(schedule.getTopic());
		((TextField) gui.getGUINode(SC_LINK_BOX)).setText(schedule.getLink());
		((Button) gui.getGUINode(SC_ADD_BUTTON)).setText("Update");
	}

	public void processClearAddEdit() {
		AppGUIModule gui = app.getGUIModule();
		TableView<Schedule> scheduleTable = (TableView) gui.getGUINode(SC_SCHEDULE_TABLE);
		scheduleTable.getSelectionModel().clearSelection();
		((ComboBox) gui.getGUINode(SC_TYPE_BOX)).setValue("Holiday");
		((DatePicker) gui.getGUINode(SC_DATE_BOX)).setValue(LocalDate.now());
		((TextField) gui.getGUINode(SC_TITLE_BOX)).setText("");
		((TextField) gui.getGUINode(SC_TOPIC_BOX)).setText("");
		((TextField) gui.getGUINode(SC_LINK_BOX)).setText("");
		((Button) gui.getGUINode(SC_ADD_BUTTON)).setText("Add");
	}

	public void processVerifyTA() {

	}

	public void processToggleOfficeHours() {
		AppGUIModule gui = app.getGUIModule();
		TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
		ObservableList<TablePosition> selectedCells = officeHoursTableView.getSelectionModel().getSelectedCells();
		if (selectedCells.size() > 0) {
			TablePosition cell = selectedCells.get(0);
			int cellColumnNumber = cell.getColumn();
			CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
			if (data.isDayOfWeekColumn(cellColumnNumber)) {
				DayOfWeek dow = data.getColumnDayOfWeek(cellColumnNumber);
				TableView<TeachingAssistantPrototype> taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
				TeachingAssistantPrototype ta = taTableView.getSelectionModel().getSelectedItem();
				if (ta != null) {
					TimeSlot timeSlot = officeHoursTableView.getSelectionModel().getSelectedItem();
					ToggleOfficeHours_Transaction transaction = new ToggleOfficeHours_Transaction(data, timeSlot, dow, ta);
					app.processTransaction(transaction);
				} else {
					Stage window = app.getGUIModule().getWindow();
					AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
				}
			}
			int row = cell.getRow();
			cell.getTableView().refresh();
		}
	}

	public void processTypeTA() {
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processPageSelect(CheckBox box) {
		CheckBox_Transaction transaction = new CheckBox_Transaction(box, box.isSelected());
		app.processTransaction(transaction);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processEditTA() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		if (data.isTASelected()) {
			TeachingAssistantPrototype taToEdit = data.getSelectedTA();
			TADialog taDialog = (TADialog) app.getGUIModule().getDialog(OH_TA_EDIT_DIALOG);
			taDialog.showEditDialog(taToEdit);
			TeachingAssistantPrototype editTA = taDialog.getEditTA();
			if (editTA != null) {
				EditTA_Transaction transaction = new EditTA_Transaction(data, taToEdit, editTA.getName(), editTA.getEmail(), editTA.getType());
				app.processTransaction(transaction);
			}
		}
	}

	public void addComboBox(ComboBox comboBox, int id) throws IOException {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		CourseSiteGeneratorFiles files = (CourseSiteGeneratorFiles) app.getFileComponent();
		String item = comboBox.getEditor().getText();
		if (data.isValidComboItem(comboBox, item)) {
			comboBox.getItems().add(item);
			files.saveComboOptions();
		}
		data.storeBox(id);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processSelectTime(ComboBox startBox, ComboBox endBox, String oldStartText, String newStartText, String oldEndText, String newEndText) {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		SelectTimeRange_Transaction transaction = new SelectTimeRange_Transaction(data, startBox, endBox, oldStartText, newStartText, oldEndText, newEndText);
		app.processTransaction(transaction);
		app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
	}

	public void processOpenImages(int id) {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		File selectedFile = AppDialogsFacade.showImageDialog(app.getGUIModule().getWindow(), LOAD_IMAGE_TITLE);
		if (selectedFile != null) {
			PropertiesManager props = PropertiesManager.getPropertiesManager();
			String currFile;
			if (id == 0) {
				currFile = props.getProperty(SI_FAVICON_BASE_ICON);
			} else if (id == 1) {
				currFile = props.getProperty(SI_NAVBAR_BASE_ICON);
			} else if (id == 2) {
				currFile = props.getProperty(SI_LEFT_BASE_ICON);
			} else {
				currFile = props.getProperty(SI_RIGHT_BASE_ICON);
			}
			SelectImage_Transaction selectTrans = new SelectImage_Transaction(id, data, app, currFile, selectedFile.toString());
			app.processTransaction(selectTrans);

		}
	}

	public void processSelectAllTAs() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		data.selectTAs(TAType.All);
	}

	public void processSelectGradTAs() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		data.selectTAs(TAType.Graduate);
	}

	public void processSelectUndergradTAs() {
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		data.selectTAs(TAType.Undergraduate);
	}

	public void processSelectTA() {
		AppGUIModule gui = app.getGUIModule();
		TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
		officeHoursTableView.refresh();
	}
}
