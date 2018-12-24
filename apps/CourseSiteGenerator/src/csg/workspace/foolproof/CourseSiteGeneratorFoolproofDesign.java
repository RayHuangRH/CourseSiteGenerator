package csg.workspace.foolproof;

import djf.modules.AppGUIModule;
import djf.ui.foolproof.FoolproofDesign;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;
import static csg.workspace.style.CSGStyle.CLASS_CSG_TEXT_FIELD;
import static csg.workspace.style.CSGStyle.CLASS_CSG_TEXT_FIELD_ERROR;
import java.time.LocalDate;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;

public class CourseSiteGeneratorFoolproofDesign implements FoolproofDesign {

	CourseSiteGeneratorApp app;

	public CourseSiteGeneratorFoolproofDesign(CourseSiteGeneratorApp initApp) {
		app = initApp;
	}

	@Override
	public void updateControls() {
		updateAddTAFoolproofDesign();
		updateTimeRangeFoolproofDesign();
		updatePagesFoolproofDesign();
		updateDatePickerFoolproofDesign();
	}

	private void updateAddTAFoolproofDesign() {
		AppGUIModule gui = app.getGUIModule();

		// FOOLPROOF DESIGN STUFF FOR ADD TA BUTTON
		TextField nameTextField = ((TextField) gui.getGUINode(OH_NAME_TEXT_FIELD));
		TextField emailTextField = ((TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD));
		String name = nameTextField.getText();
		String email = emailTextField.getText();
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		Button addTAButton = (Button) gui.getGUINode(OH_ADD_TA_BUTTON);

		// FIRST, IF NO TYPE IS SELECTED WE'LL JUST DISABLE
		// THE CONTROLS AND BE DONE WITH IT
		boolean isTypeSelected = data.isTATypeSelected();
		if (!isTypeSelected) {
			nameTextField.setDisable(true);
			emailTextField.setDisable(true);
			addTAButton.setDisable(true);
			return;
		} // A TYPE IS SELECTED SO WE'LL CONTINUE
		else {
			nameTextField.setDisable(false);
			emailTextField.setDisable(false);
			addTAButton.setDisable(false);
		}

		// NOW, IS THE USER-ENTERED DATA GOOD?
		boolean isLegalNewTA = data.isLegalNewTA(name, email);

		// ENABLE/DISABLE THE CONTROLS APPROPRIATELY
		addTAButton.setDisable(!isLegalNewTA);
		if (isLegalNewTA) {
			nameTextField.setOnAction(addTAButton.getOnAction());
			emailTextField.setOnAction(addTAButton.getOnAction());
		} else {
			nameTextField.setOnAction(null);
			emailTextField.setOnAction(null);
		}

		// UPDATE THE CONTROL TEXT DISPLAY APPROPRIATELY
		boolean isLegalNewName = data.isLegalNewName(name);
		boolean isLegalNewEmail = data.isLegalNewEmail(email);
		foolproofTextField(nameTextField, isLegalNewName);
		foolproofTextField(emailTextField, isLegalNewEmail);
	}

	private void updateTimeRangeFoolproofDesign() {
		AppGUIModule gui = app.getGUIModule();
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		ComboBox startBox = ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_START_BOX));
		ComboBox endBox = ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_END_BOX));
		int startTime = data.getTimeInt((String) startBox.getValue());
		int endTime = data.getTimeInt((String) endBox.getValue());
		startBox.setCellFactory(lv -> new ListCell<String>() {
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setText(item);
					setDisable(data.getTimeInt(item) > endTime);
				}
			}
		});
		endBox.setCellFactory(lv -> new ListCell<String>() {
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setText(item);
					setDisable(data.getTimeInt(item) < startTime);
				}
			}
		});
	}

	private void updateDatePickerFoolproofDesign() {
		AppGUIModule gui = app.getGUIModule();
		CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
		DatePicker startDateBox = ((DatePicker) gui.getGUINode(SC_START_DATE_BOX));
		DatePicker endDateBox = ((DatePicker) gui.getGUINode(SC_END_DATE_BOX));
		DatePicker eventDateBox = ((DatePicker) gui.getGUINode(SC_DATE_BOX));
		LocalDate startDate = startDateBox.getValue();
		LocalDate endDate = endDateBox.getValue();
		LocalDate eventDate = eventDateBox.getValue();
		startDateBox.setDayCellFactory(lv -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setDisable(item.isAfter(endDate));
				}
			}
		});
		endDateBox.setDayCellFactory(lv -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setDisable(item.isBefore(startDate));
				}
			}
		});
		eventDateBox.setDayCellFactory(lv -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setDisable(item.isBefore(startDate)||item.isAfter(endDate));	
				}
			}
		});
	}

	private void updatePagesFoolproofDesign() {
		AppGUIModule gui = app.getGUIModule();
		CheckBox homeBox = ((CheckBox) gui.getGUINode(SI_HOME_BOX));
		CheckBox sylBox = ((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX));
		CheckBox schBox = ((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX));
		CheckBox hwBox = ((CheckBox) gui.getGUINode(SI_HWS_BOX));
		if (homeBox.isSelected() && !sylBox.isSelected() && !schBox.isSelected() && !hwBox.isSelected()) {
			homeBox.setDisable(true);
		} else if (!homeBox.isSelected() && sylBox.isSelected() && !schBox.isSelected() && !hwBox.isSelected()) {
			sylBox.setDisable(true);
		} else if (!homeBox.isSelected() && !sylBox.isSelected() && schBox.isSelected() && !hwBox.isSelected()) {
			schBox.setDisable(true);
		} else if (!homeBox.isSelected() && !sylBox.isSelected() && !schBox.isSelected() && hwBox.isSelected()) {
			hwBox.setDisable(true);
		} else {
			homeBox.setDisable(false);
			sylBox.setDisable(false);
			schBox.setDisable(false);
			hwBox.setDisable(false);
		}
	}

	public void foolproofTextField(TextField textField, boolean hasLegalData) {
		if (hasLegalData) {
			textField.getStyleClass().remove(CLASS_CSG_TEXT_FIELD_ERROR);
			if (!textField.getStyleClass().contains(CLASS_CSG_TEXT_FIELD)) {
				textField.getStyleClass().add(CLASS_CSG_TEXT_FIELD);
			}
		} else {
			textField.getStyleClass().remove(CLASS_CSG_TEXT_FIELD);
			if (!textField.getStyleClass().contains(CLASS_CSG_TEXT_FIELD_ERROR)) {
				textField.getStyleClass().add(CLASS_CSG_TEXT_FIELD_ERROR);
			}
		}
	}
}
