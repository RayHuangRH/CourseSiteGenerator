package csg.workspace;

import djf.components.AppWorkspaceComponent;
import djf.modules.AppFoolproofModule;
import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import properties_manager.PropertiesManager;
import csg.CourseSiteGeneratorApp;
import csg.CourseSiteGeneratorPropertyType;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab;
import csg.data.Lecture;
import csg.data.Recitation;
import csg.data.Schedule;
import csg.data.TeachingAssistantPrototype;
import csg.workspace.controllers.CourseSiteGeneratorController;
import csg.workspace.dialogs.TADialog;
import csg.workspace.foolproof.CourseSiteGeneratorFoolproofDesign;
import static csg.workspace.style.CSGStyle.*;
import java.awt.RenderingHints.Key;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

/**
 *
 * @author McKillaGorilla
 */
public class CourseSiteGeneratorWorkspace extends AppWorkspaceComponent {

	public CourseSiteGeneratorWorkspace(CourseSiteGeneratorApp app) {
		super(app);

		// LAYOUT THE APP
		initLayout();

		// INIT THE EVENT HANDLERS
		initControllers();

		// 
		initFoolproofDesign();

		// INIT DIALOGS
		initDialogs();
	}

	private void initDialogs() {
		TADialog taDialog = new TADialog((CourseSiteGeneratorApp) app);
		app.getGUIModule().addDialog(OH_TA_EDIT_DIALOG, taDialog);
	}

	// THIS HELPER METHOD INITIALIZES ALL THE CONTROLS IN THE WORKSPACE
	private void initLayout() {
		// FIRST LOAD THE FONT FAMILIES FOR THE COMBO BOX
		PropertiesManager props = PropertiesManager.getPropertiesManager();

		// THIS WILL BUILD ALL OF OUR JavaFX COMPONENTS FOR US
		AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();

		TabPane tabPane = ohBuilder.buildTabPane(CSG_TAB_PANE, null, CLASS_TAB_PANE, ENABLED);
		Tab siteTab = ohBuilder.buildTab(SI_TAB, tabPane, CLASS_TAB);
		Tab syllabusTab = ohBuilder.buildTab(SY_TAB, tabPane, CLASS_TAB);
		Tab meetTab = ohBuilder.buildTab(MT_TAB, tabPane, CLASS_TAB);
		Tab ohTab = ohBuilder.buildTab(OH_TAB, tabPane, CLASS_TAB);
		Tab scTab = ohBuilder.buildTab(SC_TAB, tabPane, CLASS_TAB);

		tabPane.setTabMinWidth(100);
		tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			tabPane.setTabMinWidth((double) newValue / 5 - 20);
			tabPane.setTabMaxWidth((double) newValue / 5 - 20);
		});

		workspace = new BorderPane();

		// AND PUT EVERYTHING IN THE WORKSPACE
		((BorderPane) workspace).setCenter(tabPane);
		initSiteTab(ohBuilder, siteTab);
		initSyllabusTab(ohBuilder, syllabusTab);
		initMeetingTab(ohBuilder, meetTab);
		initOfficeHoursTab(ohBuilder, ohTab);
		initScheduleTab(ohBuilder, scTab);
	}

	public void initSiteTab(AppNodesBuilder ohBuilder, Tab tab) {
		VBox siteBox = ohBuilder.buildVBox(SI_VBOX, null, CLASS_CSG_PANE, ENABLED);
		tab.setContent(siteBox);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(siteBox);
		tab.setContent(scrollPane);
		GridPane bannerPane = ohBuilder.buildGridPane(SI_BANNER_PANE, siteBox, CLASS_GRID_PANE, ENABLED);
		ohBuilder.buildLabel(SI_BANNER_LABEL, bannerPane, 0, 0, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildLabel(SI_SUBJECT_LABEL, bannerPane, 0, 1, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildComboBox(SI_SUBJECT_BOX, bannerPane, 1, 1, 1, 1, CLASS_COMBO_BOX, ENABLED, SUBJECT_OPTIONS, DEFAULT_SUBJECT).setEditable(true);
		ohBuilder.buildLabel(SI_NUMBER_LABEL, bannerPane, 2, 1, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildComboBox(SI_NUMBER_BOX, bannerPane, 3, 1, 1, 1, CLASS_COMBO_BOX, ENABLED, NUMBER_OPTIONS, DEFAULT_NUMBER).setEditable(true);
		ohBuilder.buildLabel(SI_SEMESTER_LABEL, bannerPane, 0, 2, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildComboBox(SI_SEMESTER_BOX, bannerPane, 1, 2, 1, 1, CLASS_COMBO_BOX, ENABLED, SEMESTER_OPTIONS, DEFAULT_SEMESTER).setEditable(true);
		ohBuilder.buildLabel(SI_YEAR_LABEL, bannerPane, 2, 2, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildComboBox(SI_YEAR_BOX, bannerPane, 3, 2, 1, 1, CLASS_COMBO_BOX, ENABLED, YEAR_OPTIONS, DEFAULT_YEAR).setEditable(true);
		ohBuilder.buildLabel(SI_TITLE_LABEL, bannerPane, 0, 3, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildTextField(SI_TITLE_BOX, bannerPane, 1, 3, 3, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildLabel(SI_EXPORT_LABEL, bannerPane, 0, 4, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildLabel(SI_EXPORT_TEXT, bannerPane, 1, 4, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		GridPane pagePane = ohBuilder.buildGridPane(SI_PAGES_PANE, siteBox, CLASS_GRID_PANE, ENABLED);
		ohBuilder.buildLabel(SI_PAGES_TEXT, pagePane, 0, 0, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildCheckBox(SI_HOME_BOX, pagePane, 1, 0, 1, 1, CLASS_CHECK_BOX, ENABLED);
		ohBuilder.buildCheckBox(SI_SYLLABUS_BOX, pagePane, 2, 0, 1, 1, CLASS_CHECK_BOX, ENABLED);
		ohBuilder.buildCheckBox(SI_SCHEDULE_BOX, pagePane, 3, 0, 1, 1, CLASS_CHECK_BOX, ENABLED);
		ohBuilder.buildCheckBox(SI_HWS_BOX, pagePane, 4, 0, 1, 1, CLASS_CHECK_BOX, ENABLED);
		GridPane stylePane = ohBuilder.buildGridPane(SI_STYLE_PANE, siteBox, CLASS_GRID_PANE, ENABLED);
		ohBuilder.buildLabel(SI_STYLE_LABEL, stylePane, 0, 0, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildTextButton(SI_FAVICON_BUTTON, stylePane, 0, 1, 1, 1, CLASS_CSG_BUTTON, ENABLED);
		ohBuilder.buildImage(SI_FAVICON_BASE_ICON, stylePane, 1, 1, 1, 1, CLASS_ICON, ENABLED);
		ohBuilder.buildTextButton(SI_NAVBAR_BUTTON, stylePane, 0, 2, 1, 1, CLASS_CSG_BUTTON, ENABLED);
		ohBuilder.buildImage(SI_NAVBAR_BASE_ICON, stylePane, 1, 2, 1, 1, CLASS_ICON, ENABLED);
		ohBuilder.buildTextButton(SI_LEFT_BUTTON, stylePane, 0, 3, 1, 1, CLASS_CSG_BUTTON, ENABLED);
		ohBuilder.buildImage(SI_LEFT_BASE_ICON, stylePane, 1, 3, 1, 1, CLASS_ICON, ENABLED);
		ohBuilder.buildTextButton(SI_RIGHT_BUTTON, stylePane, 0, 4, 1, 1, CLASS_CSG_BUTTON, ENABLED);
		ohBuilder.buildImage(SI_RIGHT_BASE_ICON, stylePane, 1, 4, 1, 1, CLASS_ICON, ENABLED);
		ohBuilder.buildLabel(SI_FONT_LABEL, stylePane, 0, 5, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildComboBox(SI_FONT_BOX, stylePane, 1, 5, 1, 1, CLASS_COMBO_BOX, ENABLED, STYLE_OPTIONS, DEFAULT_STYLE);
		ohBuilder.buildLabel(SI_NOTE_LABEL, stylePane, 0, 6, 2, 1, CLASS_LABEL, ENABLED);
		VBox insBox = ohBuilder.buildVBox(SI_OFFICE_HOURS_VBOX, siteBox, CLASS_GRID_PANE, ENABLED);
		GridPane instructorPane = ohBuilder.buildGridPane(SI_INSTRUCTOR_PANE, insBox, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildLabel(SI_INSTRUCTOR_LABEL, instructorPane, 0, 0, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildLabel(SI_NAME_LABEL, instructorPane, 0, 1, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildTextField(SI_NAME_BOX, instructorPane, 1, 1, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildLabel(SI_ROOM_LABEL, instructorPane, 2, 1, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildTextField(SI_ROOM_BOX, instructorPane, 3, 1, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildLabel(SI_EMAIL_LABEL, instructorPane, 0, 2, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildTextField(SI_EMAIL_BOX, instructorPane, 1, 2, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildLabel(SI_HOME_LABEL, instructorPane, 2, 2, 1, 1, CLASS_LABEL, ENABLED);
		ohBuilder.buildTextField(SI_HOME_PAGE_BOX, instructorPane, 3, 2, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		GridPane internalPane = ohBuilder.buildGridPane(SI_INTERNAL_PANE, insBox, CLASS_CSG_BOX, ENABLED);
		HBox ohBox = ohBuilder.buildHBox(SI_OFFICE_HOURS_HBOX, internalPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SI_OFFICE_HOURS_EXPAND, ohBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SI_OFFICE_HOURS_LABEL, ohBox, CLASS_LABEL, ENABLED);
		TextArea ohArea = ohBuilder.buildTextArea(SI_OFFICE_HOURS_BOX, internalPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		internalPane.getChildren().remove(ohArea);
		GridPane.setHgrow(ohArea, Priority.ALWAYS);
	}

	public void initSyllabusTab(AppNodesBuilder ohBuilder, Tab tab) {
		VBox sylBox = ohBuilder.buildVBox(SY_VBOX, null, CLASS_CSG_PANE, ENABLED);
		tab.setContent(sylBox);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(sylBox);
		tab.setContent(scrollPane);
		GridPane desPane = ohBuilder.buildGridPane(SY_DESCRIPTION_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox desBox = ohBuilder.buildHBox(SY_DESCRIPTION_HBOX, desPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_DESCRIPTION_EXPAND, desBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_DESCRIPTION, desBox, CLASS_LABEL, ENABLED);
		TextArea desArea = ohBuilder.buildTextArea(SY_DESCRIPTION_AREA, desPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		desPane.getChildren().remove(desArea);
		GridPane topPane = ohBuilder.buildGridPane(SY_TOPICS_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox topBox = ohBuilder.buildHBox(SY_TOPICS_HBOX, topPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_TOPICS_EXPAND, topBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_TOPICS, topBox, CLASS_LABEL, ENABLED);
		TextArea topArea = ohBuilder.buildTextArea(SY_TOPICS_AREA, topPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		topPane.getChildren().remove(topArea);
		GridPane prePane = ohBuilder.buildGridPane(SY_PREREQUISITES_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox preBox = ohBuilder.buildHBox(SY_PREREQUISITES_HBOX, prePane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_PREREQUISITES_EXPAND, preBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_PREREQUISITES, preBox, CLASS_LABEL, ENABLED);
		TextArea preArea = ohBuilder.buildTextArea(SY_PREREQUISITES_AREA, prePane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		prePane.getChildren().remove(preArea);
		GridPane outPane = ohBuilder.buildGridPane(SY_OUTCOMES_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox outBox = ohBuilder.buildHBox(SY_OUTCOMES_HBOX, outPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_OUTCOMES_EXPAND, outBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_OUTCOMES, outBox, CLASS_LABEL, ENABLED);
		TextArea outArea = ohBuilder.buildTextArea(SY_OUTCOMES_AREA, outPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		outPane.getChildren().remove(outArea);
		GridPane textPane = ohBuilder.buildGridPane(SY_TEXTBOOKS_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox textBox = ohBuilder.buildHBox(SY_TEXTBOOKS_HBOX, textPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_TEXTBOOKS_EXPAND, textBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_TEXTBOOKS, textBox, CLASS_LABEL, ENABLED);
		TextArea textArea = ohBuilder.buildTextArea(SY_TEXTBOOKS_AREA, textPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		textPane.getChildren().remove(textArea);
		GridPane comPane = ohBuilder.buildGridPane(SY_COMPONENTS_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox comBox = ohBuilder.buildHBox(SY_COMPONENTS_HBOX, comPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_COMPONENTS_EXPAND, comBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_COMPONENTS, comBox, CLASS_LABEL, ENABLED);
		TextArea comArea = ohBuilder.buildTextArea(SY_COMPONENTS_AREA, comPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		comPane.getChildren().remove(comArea);
		GridPane notePane = ohBuilder.buildGridPane(SY_NOTE_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox noteBox = ohBuilder.buildHBox(SY_NOTE_HBOX, notePane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_NOTE_EXPAND, noteBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_NOTE, noteBox, CLASS_LABEL, ENABLED);
		TextArea noteArea = ohBuilder.buildTextArea(SY_NOTE_AREA, notePane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		notePane.getChildren().remove(noteArea);
		GridPane disPane = ohBuilder.buildGridPane(SY_DISHONESTY_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox disBox = ohBuilder.buildHBox(SY_DISHONESTY_HBOX, disPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_DISHONESTY_EXPAND, disBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_DISHONESTY, disBox, CLASS_LABEL, ENABLED);
		TextArea disArea = ohBuilder.buildTextArea(SY_DISHONESTY_AREA, disPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		disPane.getChildren().remove(disArea);
		GridPane assPane = ohBuilder.buildGridPane(SY_ASSISTANCE_PANE, sylBox, CLASS_GRID_PANE, ENABLED);
		HBox assBox = ohBuilder.buildHBox(SY_ASSISTANCE_HBOX, assPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SY_ASSISTANCE_EXPAND, assBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SY_ASSISTANCE, assBox, CLASS_LABEL, ENABLED);
		TextArea assArea = ohBuilder.buildTextArea(SY_ASSISTANCE_AREA, assPane, 0, 1, 1, 4, CLASS_CSG_TEXT_AREA, ENABLED);
		assPane.getChildren().remove(assArea);
		GridPane.setHgrow(desArea, Priority.ALWAYS);
		GridPane.setHgrow(topArea, Priority.ALWAYS);
		GridPane.setHgrow(preArea, Priority.ALWAYS);
		GridPane.setHgrow(outArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(comArea, Priority.ALWAYS);
		GridPane.setHgrow(noteArea, Priority.ALWAYS);
		GridPane.setHgrow(disArea, Priority.ALWAYS);
		GridPane.setHgrow(assArea, Priority.ALWAYS);
	}

	public void initMeetingTab(AppNodesBuilder ohBuilder, Tab tab) {
		CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
		VBox mtBox = ohBuilder.buildVBox(MT_VBOX, null, CLASS_CSG_PANE, ENABLED);
		tab.setContent(mtBox);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(mtBox);
		tab.setContent(scrollPane);
		GridPane lecPane = ohBuilder.buildGridPane(MT_LECTURES_PANE, mtBox, CLASS_GRID_PANE, ENABLED);
		HBox lecBox = ohBuilder.buildHBox(SY_ASSISTANCE_HBOX, lecPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(MT_LECTURES_ADD, lecBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildTextButton(MT_LECTURES_REMOVE, lecBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(MT_LECTURES, lecBox, CLASS_LABEL, ENABLED);
		//ADD GENERICS FOR TABLE VIEW
		TableView lecTable = ohBuilder.buildTableView(MT_LECTURES_TABLE, lecPane, 0, 1, 1, 10, CLASS_CSG_TABLE_VIEW, ENABLED);
		lecTable.setEditable(true);
		TableColumn<Lecture, String> lecSectionColumn = ohBuilder.buildTableColumn(MT_LECTURES_SECTION_TABLE_COLUMN, lecTable, CLASS_CSG_COLUMN);
		TableColumn<Lecture, String> lecDayColumn = ohBuilder.buildTableColumn(MT_LECTURES_DAYS_TABLE_COLUMN, lecTable, CLASS_CSG_COLUMN);
		TableColumn<Lecture, String> timeColumn = ohBuilder.buildTableColumn(MT_LECTURES_TIME_TABLE_COLUMN, lecTable, CLASS_CSG_COLUMN);
		TableColumn<Lecture, String> lecRoomColumn = ohBuilder.buildTableColumn(MT_LECTURES_ROOM_TABLE_COLUMN, lecTable, CLASS_CSG_COLUMN);
		lecSectionColumn.setCellValueFactory(new PropertyValueFactory<Lecture, String>("section"));
		lecSectionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lecSectionColumn.setOnEditCommit((CellEditEvent<Lecture, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newSection = e.getNewValue();
            int row = pos.getRow();
            Lecture lecture = e.getTableView().getItems().get(row);
			controller.processEditLecture(lecture, newSection, lecture.getDay(), lecture.getTime(), lecture.getRoom());
		});
		lecDayColumn.setCellValueFactory(new PropertyValueFactory<Lecture, String>("day"));
		lecDayColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lecDayColumn.setOnEditCommit((CellEditEvent<Lecture, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newDay = e.getNewValue();
            int row = pos.getRow();
            Lecture lecture = e.getTableView().getItems().get(row);
			controller.processEditLecture(lecture, lecture.getSection(), newDay, lecture.getTime(), lecture.getRoom());
		});
		timeColumn.setCellValueFactory(new PropertyValueFactory<Lecture, String>("time"));
		timeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		timeColumn.setOnEditCommit((CellEditEvent<Lecture, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newTime = e.getNewValue();
            int row = pos.getRow();
            Lecture lecture = e.getTableView().getItems().get(row);
			controller.processEditLecture(lecture, lecture.getSection(), lecture.getDay(), newTime, lecture.getRoom());
		});
		lecRoomColumn.setCellValueFactory(new PropertyValueFactory<Lecture, String>("room"));
		lecRoomColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lecRoomColumn.setOnEditCommit((CellEditEvent<Lecture, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newRoom = e.getNewValue();
            int row = pos.getRow();
            Lecture lecture = e.getTableView().getItems().get(row);
			controller.processEditLecture(lecture, lecture.getSection(), lecture.getDay(), lecture.getTime(), newRoom);
		});
		lecSectionColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 5.0));
		lecDayColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(2.0 / 5.0));
		timeColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 5.0));
		lecRoomColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 5.0));
		GridPane recPane = ohBuilder.buildGridPane(MT_RECITATION_PANE, mtBox, CLASS_GRID_PANE, ENABLED);
		HBox recBox = ohBuilder.buildHBox(MT_RECITATION_HBOX, recPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(MT_RECITATION_ADD, recBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildTextButton(MT_RECITATION_REMOVE, recBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(MT_RECITATION, recBox, CLASS_LABEL, ENABLED);
		//ADD GENERICS FOR TABLE VIEW
		TableView recTable = ohBuilder.buildTableView(MT_RECITATION_TABLE, recPane, 0, 1, 1, 4, CLASS_CSG_TABLE_VIEW, ENABLED);
		recTable.setEditable(true);
		TableColumn<Recitation, String> recSectionColumn = ohBuilder.buildTableColumn(MT_RECITATION_SECTION_TABLE_COLUMN, recTable, CLASS_CSG_COLUMN);
		TableColumn<Recitation, String> recDayColumn = ohBuilder.buildTableColumn(MT_RECITATION_DAYS_TABLE_COLUMN, recTable, CLASS_CSG_COLUMN);
		TableColumn<Recitation, String> recRoomColumn = ohBuilder.buildTableColumn(MT_RECITATION_ROOM_TABLE_COLUMN, recTable, CLASS_CSG_COLUMN);
		TableColumn<Recitation, String> recTa1Column = ohBuilder.buildTableColumn(MT_RECITATION_TA1_TABLE_COLUMN, recTable, CLASS_CSG_COLUMN);
		TableColumn<Recitation, String> recTa2Column = ohBuilder.buildTableColumn(MT_RECITATION_TA2_TABLE_COLUMN, recTable, CLASS_CSG_COLUMN);
		recSectionColumn.setCellValueFactory(new PropertyValueFactory<Recitation, String>("section"));
		recSectionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		recSectionColumn.setOnEditCommit((CellEditEvent<Recitation, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newSection = e.getNewValue();
            int row = pos.getRow();
            Recitation recitation = e.getTableView().getItems().get(row);
			controller.processEditRecitation(recitation, newSection, recitation.getDay(), recitation.getRoom(), recitation.getTa1(), recitation.getTa2());
		});
		recDayColumn.setCellValueFactory(new PropertyValueFactory<Recitation, String>("day"));
		recDayColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		recDayColumn.setOnEditCommit((CellEditEvent<Recitation, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newDay = e.getNewValue();
            int row = pos.getRow();
            Recitation recitation = e.getTableView().getItems().get(row);
			controller.processEditRecitation(recitation, recitation.getSection(), newDay, recitation.getRoom(), recitation.getTa1(), recitation.getTa2());
		});
		recRoomColumn.setCellValueFactory(new PropertyValueFactory<Recitation, String>("room"));
		recRoomColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		recRoomColumn.setOnEditCommit((CellEditEvent<Recitation, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newRoom = e.getNewValue();
            int row = pos.getRow();
            Recitation recitation = e.getTableView().getItems().get(row);
			controller.processEditRecitation(recitation, recitation.getSection(), recitation.getDay(), newRoom, recitation.getTa1(), recitation.getTa2());
		});
		recTa1Column.setCellValueFactory(new PropertyValueFactory<Recitation, String>("ta1"));
		recTa1Column.setCellFactory(TextFieldTableCell.forTableColumn());
		recTa1Column.setOnEditCommit((CellEditEvent<Recitation, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newTA1 = e.getNewValue();
            int row = pos.getRow();
            Recitation recitation = e.getTableView().getItems().get(row);
			controller.processEditRecitation(recitation, recitation.getSection(), recitation.getDay(), recitation.getRoom(), newTA1, recitation.getTa2());
		});
		recTa2Column.setCellValueFactory(new PropertyValueFactory<Recitation, String>("ta2"));
		recTa2Column.setCellFactory(TextFieldTableCell.forTableColumn());
		recTa2Column.setOnEditCommit((CellEditEvent<Recitation, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newTA2 = e.getNewValue();
            int row = pos.getRow();
            Recitation recitation = e.getTableView().getItems().get(row);
			controller.processEditRecitation(recitation, recitation.getSection(), recitation.getDay(), recitation.getRoom(), recitation.getTa1(), newTA2);
		});
		recSectionColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		recDayColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(2.0 / 6.0));
		recRoomColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		recTa1Column.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		recTa2Column.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		GridPane labPane = ohBuilder.buildGridPane(MT_LAB_PANE, mtBox, CLASS_GRID_PANE, ENABLED);
		HBox labBox = ohBuilder.buildHBox(MT_LAB_HBOX, labPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(MT_LAB_ADD, labBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildTextButton(MT_LAB_REMOVE, labBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(MT_LAB, labBox, CLASS_LABEL, ENABLED);
		//ADD GENERICS FOR TABLE VIEW
		TableView labTable = ohBuilder.buildTableView(MT_LAB_TABLE, labPane, 0, 1, 1, 4, CLASS_CSG_TABLE_VIEW, ENABLED);
		labTable.setEditable(true);
		TableColumn<Lab, String> labSectionColumn = ohBuilder.buildTableColumn(MT_LAB_SECTION_TABLE_COLUMN, labTable, CLASS_CSG_COLUMN);
		TableColumn<Lab, String> labDayColumn = ohBuilder.buildTableColumn(MT_LAB_DAYS_TABLE_COLUMN, labTable, CLASS_CSG_COLUMN);
		TableColumn<Lab, String> labRoomColumn = ohBuilder.buildTableColumn(MT_LAB_ROOM_TABLE_COLUMN, labTable, CLASS_CSG_COLUMN);
		TableColumn<Lab, String> labTa1Column = ohBuilder.buildTableColumn(MT_LAB_TA1_TABLE_COLUMN, labTable, CLASS_CSG_COLUMN);
		TableColumn<Lab, String> labTa2Column = ohBuilder.buildTableColumn(MT_LAB_TA2_TABLE_COLUMN, labTable, CLASS_CSG_COLUMN);
		labSectionColumn.setCellValueFactory(new PropertyValueFactory<Lab, String>("section"));
		labSectionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		labSectionColumn.setOnEditCommit((CellEditEvent<Lab, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newSection = e.getNewValue();
            int row = pos.getRow();
            Lab lab = e.getTableView().getItems().get(row);
			controller.processEditLab(lab, newSection, lab.getDay(), lab.getRoom(), lab.getTa1(), lab.getTa2());
		});
		labDayColumn.setCellValueFactory(new PropertyValueFactory<Lab, String>("day"));
		labDayColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		labDayColumn.setOnEditCommit((CellEditEvent<Lab, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newDay = e.getNewValue();
            int row = pos.getRow();
            Lab lab = e.getTableView().getItems().get(row);
			controller.processEditLab(lab, lab.getSection(), newDay, lab.getRoom(), lab.getTa1(), lab.getTa2());
		});
		labRoomColumn.setCellValueFactory(new PropertyValueFactory<Lab, String>("room"));
		labRoomColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		labRoomColumn.setOnEditCommit((CellEditEvent<Lab, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newRoom = e.getNewValue();
            int row = pos.getRow();
            Lab lab = e.getTableView().getItems().get(row);
			controller.processEditLab(lab, lab.getSection(), lab.getDay(), newRoom, lab.getTa1(), lab.getTa2());
		});
		labTa1Column.setCellValueFactory(new PropertyValueFactory<Lab, String>("ta1"));
		labTa1Column.setCellFactory(TextFieldTableCell.forTableColumn());
		labTa1Column.setOnEditCommit((CellEditEvent<Lab, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newTA1 = e.getNewValue();
            int row = pos.getRow();
            Lab lab = e.getTableView().getItems().get(row);
			controller.processEditLab(lab, lab.getSection(), lab.getDay(), lab.getRoom(), newTA1, lab.getTa2());
		});
		labTa2Column.setCellValueFactory(new PropertyValueFactory<Lab, String>("ta2"));
		labTa2Column.setCellFactory(TextFieldTableCell.forTableColumn());
		labTa2Column.setOnEditCommit((CellEditEvent<Lab, String> e) -> {
			TablePosition pos = e.getTablePosition();
            String newTA2 = e.getNewValue();
            int row = pos.getRow();
            Lab lab = e.getTableView().getItems().get(row);
			controller.processEditLab(lab, lab.getSection(), lab.getDay(), lab.getRoom(), lab.getTa1(), newTA2);
		});
		labSectionColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		labDayColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(2.0 / 6.0));
		labRoomColumn.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		labTa1Column.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		labTa2Column.prefWidthProperty().bind(lecTable.widthProperty().multiply(1.0 / 6.0));
		GridPane.setHgrow(lecTable, Priority.ALWAYS);
		GridPane.setHgrow(recTable, Priority.ALWAYS);
		GridPane.setHgrow(labTable, Priority.ALWAYS);
	}

	public void initOfficeHoursTab(AppNodesBuilder ohBuilder, Tab tab) {
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		VBox ohBox = ohBuilder.buildVBox(OH_VBOX, null, CLASS_CSG_PANE, ENABLED);
		tab.setContent(ohBox);
		GridPane ohPane = ohBuilder.buildGridPane(OH_PANE, ohBox, CLASS_GRID_PANE, ENABLED);
		HBox tasHeaderBox = ohBuilder.buildHBox(OH_TAS_HEADER_PANE, ohPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(OH_TAS_REMOVE, tasHeaderBox, CLASS_CSG_BUTTON, ENABLED);
		ohBuilder.buildLabel(OH_TAS_HEADER_LABEL, tasHeaderBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		HBox typeHeaderBox = ohBuilder.buildHBox(OH_GRAD_UNDERGRAD_TAS_PANE, tasHeaderBox, CLASS_CSG_RADIO_BOX, ENABLED);
		ToggleGroup tg = new ToggleGroup();
		ohBuilder.buildRadioButton(OH_ALL_RADIO_BUTTON, typeHeaderBox, CLASS_CSG_RADIO_BUTTON, ENABLED, tg, true);
		ohBuilder.buildRadioButton(OH_GRAD_RADIO_BUTTON, typeHeaderBox, CLASS_CSG_RADIO_BUTTON, ENABLED, tg, false);
		ohBuilder.buildRadioButton(OH_UNDERGRAD_RADIO_BUTTON, typeHeaderBox, CLASS_CSG_RADIO_BUTTON, ENABLED, tg, false);
		TableView<TeachingAssistantPrototype> taTable = ohBuilder.buildTableView(OH_TAS_TABLE_VIEW, ohPane, 0, 1, 1, 1, CLASS_CSG_TABLE_VIEW, ENABLED);
		taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		TableColumn nameColumn = ohBuilder.buildTableColumn(OH_NAME_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
		TableColumn emailColumn = ohBuilder.buildTableColumn(OH_EMAIL_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
		TableColumn slotsColumn = ohBuilder.buildTableColumn(OH_SLOTS_TABLE_COLUMN, taTable, CLASS_CSG_CENTERED_COLUMN);
		TableColumn typeColumn = ohBuilder.buildTableColumn(OH_TYPE_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
		nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<String, String>("email"));
		slotsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("slots"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("type"));
		nameColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
		emailColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(2.0 / 5.0));
		slotsColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
		typeColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
		HBox taBox = ohBuilder.buildHBox(OH_ADD_TA_PANE, ohPane, 0, 2, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextField(OH_NAME_TEXT_FIELD, taBox, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildTextField(OH_EMAIL_TEXT_FIELD, taBox, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildTextButton(OH_ADD_TA_BUTTON, taBox, CLASS_CSG_BUTTON, !ENABLED);
		GridPane.setHgrow(taTable, Priority.ALWAYS);

		HBox officeHoursHeaderBox = ohBuilder.buildHBox(OH_OFFICE_HOURS_HEADER_PANE, ohPane, 0, 3, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildLabel(OH_OFFICE_HOURS_HEADER_LABEL, officeHoursHeaderBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildLabel(OH_OFFICE_HOURS_START_LABEL, officeHoursHeaderBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildComboBox(OH_OFFICE_HOURS_START_BOX, START_OPTIONS, DEFAULT_START, officeHoursHeaderBox, CLASS_COMBO_BOX, ENABLED);
		ohBuilder.buildLabel(OH_OFFICE_HOURS_END_LABEL, officeHoursHeaderBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildComboBox(OH_OFFICE_HOURS_END_BOX, END_OPTIONS, DEFAULT_END, officeHoursHeaderBox, CLASS_COMBO_BOX, ENABLED).setValue(props.getProperty(DEFAULT_END));
		TableView officeHoursTable = ohBuilder.buildTableView(OH_OFFICE_HOURS_TABLE_VIEW, ohPane, 0, 4, 1, 1, CLASS_CSG_OFFICE_HOURS_TABLE_VIEW, ENABLED);
		setupOfficeHoursColumn(OH_START_TIME_TABLE_COLUMN, officeHoursTable, CLASS_CSG_TIME_COLUMN, "startTime");
		setupOfficeHoursColumn(OH_END_TIME_TABLE_COLUMN, officeHoursTable, CLASS_CSG_TIME_COLUMN, "endTime");
		setupOfficeHoursColumn(OH_MONDAY_TABLE_COLUMN, officeHoursTable, CLASS_CSG_DAY_OF_WEEK_COLUMN, "monday");
		setupOfficeHoursColumn(OH_TUESDAY_TABLE_COLUMN, officeHoursTable, CLASS_CSG_DAY_OF_WEEK_COLUMN, "tuesday");
		setupOfficeHoursColumn(OH_WEDNESDAY_TABLE_COLUMN, officeHoursTable, CLASS_CSG_DAY_OF_WEEK_COLUMN, "wednesday");
		setupOfficeHoursColumn(OH_THURSDAY_TABLE_COLUMN, officeHoursTable, CLASS_CSG_DAY_OF_WEEK_COLUMN, "thursday");
		setupOfficeHoursColumn(OH_FRIDAY_TABLE_COLUMN, officeHoursTable, CLASS_CSG_DAY_OF_WEEK_COLUMN, "friday");

		// MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
		GridPane.setHgrow(officeHoursTable, Priority.ALWAYS);
	}

	private void setupOfficeHoursColumn(Object columnId, TableView tableView, String styleClass, String columnDataProperty) {
		AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
		TableColumn<TeachingAssistantPrototype, String> column = builder.buildTableColumn(columnId, tableView, styleClass);
		column.setCellValueFactory(new PropertyValueFactory<TeachingAssistantPrototype, String>(columnDataProperty));
		column.prefWidthProperty().bind(tableView.widthProperty().multiply(1.0 / 7.0));
		column.setCellFactory(col -> {
			return new TableCell<TeachingAssistantPrototype, String>() {
				@Override
				protected void updateItem(String text, boolean empty) {
					super.updateItem(text, empty);
					if (text == null || empty) {
						setText(null);
						setStyle("");
					} else {
						// CHECK TO SEE IF text CONTAINS THE NAME OF
						// THE CURRENTLY SELECTED TA
						setText(text);
						TableView<TeachingAssistantPrototype> tasTableView = (TableView) app.getGUIModule().getGUINode(OH_TAS_TABLE_VIEW);
						TeachingAssistantPrototype selectedTA = tasTableView.getSelectionModel().getSelectedItem();
						if (selectedTA == null) {
							setStyle("");
						} else if (text.contains(selectedTA.getName())) {
							setStyle("-fx-background-color: yellow");
						} else {
							setStyle("");
						}
					}
				}
			};
		});
	}

	public void initScheduleTab(AppNodesBuilder ohBuilder, Tab tab) {
		VBox scBox = ohBuilder.buildVBox(SC_VBOX, null, CLASS_CSG_PANE, ENABLED);
		tab.setContent(scBox);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(scBox);
		tab.setContent(scrollPane);
		GridPane calPane = ohBuilder.buildGridPane(SC_CALENDER_PANE, scBox, CLASS_GRID_PANE, ENABLED);
		ohBuilder.buildLabel(SC_CALENDER_LABEL, calPane, 0, 0, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		HBox calBox = ohBuilder.buildHBox(SC_CALENDER_HBOX, calPane, 0, 1, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildLabel(SC_START_DATE, calBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildDatePicker(SC_START_DATE_BOX, calBox, CLASS_DATE_PICKER, ENABLED).setValue(LocalDate.now());
		ohBuilder.buildLabel(SC_END_DATE, calBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildDatePicker(SC_END_DATE_BOX, calBox, CLASS_DATE_PICKER, ENABLED).setValue(LocalDate.now());
		GridPane scPane = ohBuilder.buildGridPane(SC_SCHEDULE_PANE, scBox, CLASS_GRID_PANE, ENABLED);
		HBox itBox = ohBuilder.buildHBox(SC_SCHEDULE_HBOX, scPane, 0, 0, 1, 1, CLASS_CSG_BOX, ENABLED);
		ohBuilder.buildTextButton(SC_SCHEDULE_REMOVE, itBox, CLASS_UI_BUTTON, ENABLED);
		ohBuilder.buildLabel(SC_SCHEDULE_LABEL, itBox, CLASS_CSG_HEADER_LABEL, ENABLED);
		TableView scTable = ohBuilder.buildTableView(SC_SCHEDULE_TABLE, scPane, 0, 1, 1, 1, CLASS_CSG_TABLE_VIEW, ENABLED);
		TableColumn typeColumn = ohBuilder.buildTableColumn(SC_TYPE_TABLE_COLUMN, scTable, CLASS_CSG_COLUMN);
		TableColumn dateColumn = ohBuilder.buildTableColumn(SC_DATE_TABLE_COLUMN, scTable, CLASS_CSG_COLUMN);
		TableColumn titleColumn = ohBuilder.buildTableColumn(SC_TITLE_TABLE_COLUMN, scTable, CLASS_CSG_CENTERED_COLUMN);
		TableColumn topicColumn = ohBuilder.buildTableColumn(SC_TOPIC_TABLE_COLUMN, scTable, CLASS_CSG_COLUMN);
		typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("type"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<String, String>("date"));
		titleColumn.setCellValueFactory(new PropertyValueFactory<String, String>("title"));
		topicColumn.setCellValueFactory(new PropertyValueFactory<String, String>("topic"));
		typeColumn.prefWidthProperty().bind(scTable.widthProperty().multiply(1.0 / 5.0));
		dateColumn.prefWidthProperty().bind(scTable.widthProperty().multiply(2.0 / 5.0));
		titleColumn.prefWidthProperty().bind(scTable.widthProperty().multiply(1.0 / 5.0));
		topicColumn.prefWidthProperty().bind(scTable.widthProperty().multiply(1.0 / 5.0));
		GridPane.setHgrow(scTable, Priority.ALWAYS);
		GridPane addPane = ohBuilder.buildGridPane(SC_ADD_PANE, scBox, CLASS_GRID_PANE, ENABLED);
		ohBuilder.buildLabel(SC_ADD_LABEL, addPane, 0, 0, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildLabel(SC_TYPE_LABEL, addPane, 0, 1, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildComboBox(SC_TYPE_BOX, addPane, 1, 1, 1, 1, CLASS_COMBO_BOX, ENABLED, TYPE_OPTIONS, DEFAULT_TYPE);
		ohBuilder.buildLabel(SC_DATE_LABEL, addPane, 0, 2, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildDatePicker(SC_DATE_BOX, addPane, 1, 2, 1, 1, CLASS_DATE_PICKER, ENABLED);
		ohBuilder.buildLabel(SC_TITLE_LABEL, addPane, 0, 3, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildTextField(SC_TITLE_BOX, addPane, 1, 3, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildLabel(SC_TOPIC_LABEL, addPane, 0, 4, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildTextField(SC_TOPIC_BOX, addPane, 1, 4, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildLabel(SC_LINK_LABEL, addPane, 0, 5, 1, 1, CLASS_CSG_HEADER_LABEL, ENABLED);
		ohBuilder.buildTextField(SC_LINK_BOX, addPane, 1, 5, 1, 1, CLASS_CSG_TEXT_FIELD, ENABLED);
		ohBuilder.buildTextButton(SC_ADD_BUTTON, addPane, 0, 6, 1, 1, CLASS_CSG_BUTTON, ENABLED);
		ohBuilder.buildTextButton(SC_CLEAR_BUTTON, addPane, 1, 6, 1, 1, CLASS_CSG_BUTTON, ENABLED);
	}

	public void initControllers() {
		initSiteControllers();
		initSyllabusControllers();
		initMeetingControllers();
		initScheduleControllers();

		CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
		AppGUIModule gui = app.getGUIModule();

		// FOOLPROOF DESIGN STUFF
		TextField nameTextField = ((TextField) gui.getGUINode(OH_NAME_TEXT_FIELD));
		TextField emailTextField = ((TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD));

		nameTextField.textProperty().addListener(e -> {
			controller.processTypeTA();
		});
		emailTextField.textProperty().addListener(e -> {
			controller.processTypeTA();
		});

		// FIRE THE ADD EVENT ACTION
		nameTextField.setOnAction(e -> {
			controller.processAddTA();
		});
		emailTextField.setOnAction(e -> {
			controller.processAddTA();
		});
		((Button) gui.getGUINode(OH_ADD_TA_BUTTON)).setOnAction(e -> {
			controller.processAddTA();
		});
		((Button) gui.getGUINode(OH_TAS_REMOVE)).setOnAction(e -> {
			controller.processRemoveTA();
		});

		TableView officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
		officeHoursTableView.getSelectionModel().setCellSelectionEnabled(true);
		officeHoursTableView.setOnMouseClicked(e -> {
			controller.processToggleOfficeHours();
		});

		ComboBox startBox = ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_START_BOX));
		ComboBox endBox = ((ComboBox) gui.getGUINode(OH_OFFICE_HOURS_END_BOX));
		//FIX ERROR WHILE LOADING VALUES INTO TIME RANGE COMBOBOX
		startBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (startBox.isFocused()) {
				controller.processSelectTime(startBox, endBox, (String) oldValue, (String) newValue, (String) endBox.getValue(), (String) endBox.getValue());
			}
		});

		endBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (endBox.isFocused()) {
				controller.processSelectTime(startBox, endBox, (String) startBox.getValue(), (String) startBox.getValue(), (String) oldValue, (String) newValue);
			}
		});

		// DON'T LET ANYONE SORT THE TABLES
		TableView tasTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
		for (int i = 0; i < officeHoursTableView.getColumns().size(); i++) {
			((TableColumn) officeHoursTableView.getColumns().get(i)).setSortable(false);
		}
		for (int i = 0; i < tasTableView.getColumns().size(); i++) {
			((TableColumn) tasTableView.getColumns().get(i)).setSortable(false);
		}

		tasTableView.setOnMouseClicked(e -> {
			app.getFoolproofModule().updateAll();
			if (e.getClickCount() == 2) {
				controller.processEditTA();
			}
			controller.processSelectTA();
		});

		RadioButton allRadio = (RadioButton) gui.getGUINode(OH_ALL_RADIO_BUTTON);
		allRadio.setOnAction(e -> {
			controller.processSelectAllTAs();
		});
		RadioButton gradRadio = (RadioButton) gui.getGUINode(OH_GRAD_RADIO_BUTTON);
		gradRadio.setOnAction(e -> {
			controller.processSelectGradTAs();
		});
		RadioButton undergradRadio = (RadioButton) gui.getGUINode(OH_UNDERGRAD_RADIO_BUTTON);
		undergradRadio.setOnAction(e -> {
			controller.processSelectUndergradTAs();
		});

	}

	public void initSiteControllers() {
		CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
		AppGUIModule gui = app.getGUIModule();
		//SITE TAB CONTROLLERS
		((CheckBox) gui.getGUINode(SI_HOME_BOX)).setOnMouseClicked(e -> {
			controller.processPageSelect(((CheckBox) gui.getGUINode(SI_HOME_BOX)));
		});
		((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)).setOnMouseClicked(e -> {
			controller.processPageSelect(((CheckBox) gui.getGUINode(SI_SYLLABUS_BOX)));
		});
		((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)).setOnMouseClicked(e -> {
			controller.processPageSelect(((CheckBox) gui.getGUINode(SI_SCHEDULE_BOX)));
		});
		((CheckBox) gui.getGUINode(SI_HWS_BOX)).setOnMouseClicked(e -> {
			controller.processPageSelect(((CheckBox) gui.getGUINode(SI_HWS_BOX)));
		});
		ComboBox subBox = ((ComboBox) gui.getGUINode(SI_SUBJECT_BOX));
		ComboBox numBox = ((ComboBox) gui.getGUINode(SI_NUMBER_BOX));
		ComboBox semBox = ((ComboBox) gui.getGUINode(SI_SEMESTER_BOX));
		ComboBox yearBox = ((ComboBox) gui.getGUINode(SI_YEAR_BOX));

		subBox.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				try {
					controller.addComboBox(subBox, 0);
				} catch (IOException ex) {
					System.out.println("Oof");
				}
			}
		});
		numBox.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				try {
					controller.addComboBox(numBox, 1);
				} catch (IOException ex) {
					System.out.println("Oof");
				}
			}
		});
		semBox.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				try {
					controller.addComboBox(semBox, 2);
				} catch (IOException ex) {
					System.out.println("Oof");
				}
			}
		});
		yearBox.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				try {
					controller.addComboBox(yearBox, 3);
				} catch (IOException ex) {
					System.out.println("Oof");
				}
			}
		});
		subBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (subBox.isFocused()) {
				controller.processSelectCombo(subBox, oldValue, newValue);
			}
		});
		numBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (numBox.isFocused()) {
				controller.processSelectCombo(numBox, oldValue, newValue);
			}
		});
		semBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (semBox.isFocused()) {
				controller.processSelectCombo(semBox, oldValue, newValue);
			}
		});
		yearBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (yearBox.isFocused()) {
				controller.processSelectCombo(yearBox, oldValue, newValue);
			}
		});
		TextField titleField = ((TextField) gui.getGUINode(SI_TITLE_BOX));

		titleField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (titleField.isFocused()) {
				controller.processEditTextField(titleField, oldValue, newValue);
			}
		});

		ComboBox styleBox = ((ComboBox) gui.getGUINode(SI_FONT_BOX));
		styleBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (styleBox.isFocused()) {
				controller.processSelectCombo(styleBox, oldValue, newValue);
			}
		});

		((Button) gui.getGUINode(SI_FAVICON_BUTTON)).setOnMouseClicked(e -> {
			controller.processOpenImages(0);
		});
		((Button) gui.getGUINode(SI_NAVBAR_BUTTON)).setOnMouseClicked(e -> {
			controller.processOpenImages(1);
		});
		((Button) gui.getGUINode(SI_LEFT_BUTTON)).setOnMouseClicked(e -> {
			controller.processOpenImages(2);
		});
		((Button) gui.getGUINode(SI_RIGHT_BUTTON)).setOnMouseClicked(e -> {
			controller.processOpenImages(3);
		});
		TextField nameField = ((TextField) gui.getGUINode(SI_NAME_BOX));
		TextField roomField = ((TextField) gui.getGUINode(SI_ROOM_BOX));
		TextField emailField = ((TextField) gui.getGUINode(SI_EMAIL_BOX));
		TextField homeField = ((TextField) gui.getGUINode(SI_HOME_PAGE_BOX));
		TextArea hourField = ((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX));
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (nameField.isFocused()) {
				controller.processEditTextField(nameField, oldValue, newValue);
			}
		});
		roomField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (roomField.isFocused()) {
				controller.processEditTextField(roomField, oldValue, newValue);
			}
		});
		emailField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (emailField.isFocused()) {
				controller.processEditTextField(emailField, oldValue, newValue);
			}
		});
		homeField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (homeField.isFocused()) {
				controller.processEditTextField(homeField, oldValue, newValue);
			}
		});
		hourField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (hourField.isFocused()) {
				controller.processEditTextArea(hourField, oldValue, newValue);
			}
		});

		((Button) gui.getGUINode(SI_OFFICE_HOURS_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SI_OFFICE_HOURS_BOX));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SI_INTERNAL_PANE));
			if (((Button) gui.getGUINode(SI_OFFICE_HOURS_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SI_OFFICE_HOURS_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SI_OFFICE_HOURS_EXPAND)).setText("-");
			}
		});

	}

	public void initSyllabusControllers() {
		CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
		AppGUIModule gui = app.getGUIModule();
		((Button) gui.getGUINode(SY_DESCRIPTION_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_DESCRIPTION_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_DESCRIPTION_PANE));
			if (((Button) gui.getGUINode(SY_DESCRIPTION_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_DESCRIPTION_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_DESCRIPTION_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_TOPICS_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_TOPICS_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_TOPICS_PANE));
			if (((Button) gui.getGUINode(SY_TOPICS_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_TOPICS_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_TOPICS_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_PREREQUISITES_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_PREREQUISITES_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_PREREQUISITES_PANE));
			if (((Button) gui.getGUINode(SY_PREREQUISITES_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_PREREQUISITES_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_PREREQUISITES_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_OUTCOMES_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_OUTCOMES_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_OUTCOMES_PANE));
			if (((Button) gui.getGUINode(SY_OUTCOMES_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_OUTCOMES_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_OUTCOMES_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_TEXTBOOKS_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_TEXTBOOKS_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_TEXTBOOKS_PANE));
			if (((Button) gui.getGUINode(SY_TEXTBOOKS_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_TEXTBOOKS_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_TEXTBOOKS_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_COMPONENTS_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_COMPONENTS_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_COMPONENTS_PANE));
			if (((Button) gui.getGUINode(SY_COMPONENTS_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_COMPONENTS_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_COMPONENTS_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_NOTE_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_NOTE_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_NOTE_PANE));
			if (((Button) gui.getGUINode(SY_NOTE_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_NOTE_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_NOTE_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_DISHONESTY_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_DISHONESTY_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_DISHONESTY_PANE));
			if (((Button) gui.getGUINode(SY_DISHONESTY_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_DISHONESTY_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_DISHONESTY_EXPAND)).setText("-");
			}
		});
		((Button) gui.getGUINode(SY_ASSISTANCE_EXPAND)).setOnAction(e -> {
			TextArea area = ((TextArea) gui.getGUINode(SY_ASSISTANCE_AREA));
			AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
			GridPane internalPane = ((GridPane) gui.getGUINode(SY_ASSISTANCE_PANE));
			if (((Button) gui.getGUINode(SY_ASSISTANCE_EXPAND)).getText().equals("-")) {
				internalPane.getChildren().remove(area);
				((Button) gui.getGUINode(SY_ASSISTANCE_EXPAND)).setText("+");
			} else {
				internalPane.getChildren().add(area);
				((Button) gui.getGUINode(SY_ASSISTANCE_EXPAND)).setText("-");
			}
		});
		TextArea desArea = ((TextArea) gui.getGUINode(SY_DESCRIPTION_AREA));
		TextArea topArea = ((TextArea) gui.getGUINode(SY_TOPICS_AREA));
		TextArea preArea = ((TextArea) gui.getGUINode(SY_PREREQUISITES_AREA));
		TextArea outArea = ((TextArea) gui.getGUINode(SY_OUTCOMES_AREA));
		TextArea textArea = ((TextArea) gui.getGUINode(SY_TEXTBOOKS_AREA));
		TextArea comArea = ((TextArea) gui.getGUINode(SY_COMPONENTS_AREA));
		TextArea noteArea = ((TextArea) gui.getGUINode(SY_NOTE_AREA));
		TextArea disArea = ((TextArea) gui.getGUINode(SY_DISHONESTY_AREA));
		TextArea assArea = ((TextArea) gui.getGUINode(SY_ASSISTANCE_AREA));
		desArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (desArea.isFocused()) {
				controller.processEditTextArea(desArea, oldValue, newValue);
			}
		});
		topArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (topArea.isFocused()) {
				controller.processEditTextArea(topArea, oldValue, newValue);
			}
		});
		preArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (preArea.isFocused()) {
				controller.processEditTextArea(preArea, oldValue, newValue);
			}
		});
		outArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (outArea.isFocused()) {
				controller.processEditTextArea(outArea, oldValue, newValue);
			}
		});
		textArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (textArea.isFocused()) {
				controller.processEditTextArea(textArea, oldValue, newValue);
			}
		});
		comArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (comArea.isFocused()) {
				controller.processEditTextArea(comArea, oldValue, newValue);
			}
		});
		noteArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (noteArea.isFocused()) {
				controller.processEditTextArea(noteArea, oldValue, newValue);
			}
		});
		disArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (disArea.isFocused()) {
				controller.processEditTextArea(disArea, oldValue, newValue);
			}
		});
		assArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (assArea.isFocused()) {
				controller.processEditTextArea(assArea, oldValue, newValue);
			}
		});
	}

	public void initMeetingControllers() {
		CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
		AppGUIModule gui = app.getGUIModule();
		((Button) gui.getGUINode(MT_LECTURES_ADD)).setOnAction(e -> {
			controller.processAddLecture();
		});
		((Button) gui.getGUINode(MT_LECTURES_REMOVE)).setOnAction(e -> {
			controller.processRemoveLecture();
		});
		((Button) gui.getGUINode(MT_RECITATION_ADD)).setOnAction(e -> {
			controller.processAddRecitation();
		});
		((Button) gui.getGUINode(MT_RECITATION_REMOVE)).setOnAction(e -> {
			controller.processRemoveRecitation();
		});
		((Button) gui.getGUINode(MT_LAB_ADD)).setOnAction(e -> {
			controller.processAddLab();
		});
		((Button) gui.getGUINode(MT_LAB_REMOVE)).setOnAction(e -> {
			controller.processRemoveLab();
		});
	}

	//CHECK FOOLPROOF MODULE FOR SCHEDULE
	public void initScheduleControllers() {
		CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
		AppGUIModule gui = app.getGUIModule();
		DatePicker startDate = ((DatePicker) gui.getGUINode(SC_START_DATE_BOX));
		DatePicker endDate = ((DatePicker) gui.getGUINode(SC_END_DATE_BOX));
		startDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (startDate.isFocused()) {
				controller.processSelectDate(startDate, oldValue, newValue);
			}
		});
		endDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (endDate.isFocused()) {
				controller.processSelectDate(endDate, oldValue, newValue);
			}
		});
		((Button) gui.getGUINode(SC_SCHEDULE_REMOVE)).setOnAction(e -> {
			controller.processRemoveSchedule();
		});
		TableView<Schedule> scheduleTable = (TableView) gui.getGUINode(SC_SCHEDULE_TABLE);
		scheduleTable.setOnMouseClicked(e -> {
			Schedule schedule = scheduleTable.getSelectionModel().getSelectedItem();
			controller.processClickAddEdit(schedule);
		});
		((Button) gui.getGUINode(SC_ADD_BUTTON)).setOnAction(e -> {
			if (((Button) gui.getGUINode(SC_ADD_BUTTON)).getText().equals("Add")) {
				controller.processAddSchedule();
			} else {
				controller.processEditSchedule(scheduleTable.getSelectionModel().getSelectedItem());
			}
		});
		((Button) gui.getGUINode(SC_CLEAR_BUTTON)).setOnAction(e -> {
			controller.processClearAddEdit();
		});
	}

	public void initFoolproofDesign() {
		AppGUIModule gui = app.getGUIModule();
		AppFoolproofModule foolproofSettings = app.getFoolproofModule();
		foolproofSettings.registerModeSettings(OH_FOOLPROOF_SETTINGS,
				new CourseSiteGeneratorFoolproofDesign((CourseSiteGeneratorApp) app));
	}

	@Override
	public void processWorkspaceKeyEvent(KeyEvent key) {
		// WE AREN'T USING THIS FOR THIS APPLICATION
	}

	@Override
	public void showNewDialog() {
		// WE AREN'T USING THIS FOR THIS APPLICATION
	}
}
