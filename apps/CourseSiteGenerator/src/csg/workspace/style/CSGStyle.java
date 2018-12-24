package csg.workspace.style;

/**
 * This class lists all CSS style types for this application. These
 * are used by JavaFX to apply style properties to controls like
 * buttons, labels, and panes.

 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class CSGStyle {
    public static final String EMPTY_TEXT = "";
    public static final int BUTTON_TAG_WIDTH = 75;

    // THESE CONSTANTS ARE FOR TYING THE PRESENTATION STYLE OF
    // THIS M3Workspace'S COMPONENTS TO A STYLE SHEET THAT IT USES
    // NOTE THAT FOUR CLASS STYLES ALREADY EXIST:
    // top_toolbar, toolbar, toolbar_text_button, toolbar_icon_button
	
	//FOR GENERAL PURPOSE 
	public static final String CLASS_TAB_PANE	      = "csg_tab_pane";
	public static final String CLASS_TAB			  = "csg_tab";
	public static final String CLASS_GRID_PANE		  = "grid_pane";
	public static final String CLASS_COMBO_BOX        = "csg_combo_box";
	public static final String CLASS_CHECK_BOX		  = "check_box";
	public static final String CLASS_LABEL			  = "csg_label";
	public static final String CLASS_UI_BUTTON		  = "csg_ui_button";
	public static final String CLASS_DATE_PICKER      = "csg_date_picker";
	public static final String CLASS_ICON             = "csg_icon";
	
	//FOR OFFICE HOURS
    public static final String CLASS_CSG_PANE          = "csg_pane";
    public static final String CLASS_CSG_BOX           = "csg_box";            
    public static final String CLASS_CSG_HEADER_LABEL  = "csg_header_label";
    public static final String CLASS_CSG_PROMPT        = "csg_prompt";
    public static final String CLASS_CSG_TEXT_FIELD    = "csg_text_field";
    public static final String CLASS_CSG_TEXT_AREA    = "csg_text_area";
	public static final String CLASS_CSG_TEXT_FIELD_ERROR = "csg_text_field_error";
    public static final String CLASS_CSG_BUTTON        = "csg_button";
    public static final String CLASS_CSG_RADIO_BOX     = "csg_radio_box";
    public static final String CLASS_CSG_RADIO_BUTTON  = "csg_radio_button";
    public static final String CLASS_CSG_TAB_PANE      = "csg_tab_pane";
    public static final String CLASS_CSG_TABLE_VIEW    = "csg_table_view";
    public static final String CLASS_CSG_COLUMN        = "csg_column";
    public static final String CLASS_CSG_CENTERED_COLUMN = "csg_centered_column";
    public static final String CLASS_CSG_OFFICE_HOURS_TABLE_VIEW = "csg_office_hours_table_view";
    public static final String CLASS_CSG_TIME_COLUMN = "csg_time_column";
    public static final String CLASS_CSG_DAY_OF_WEEK_COLUMN = "csg_day_of_week_column";
    
    // FOR THE DIALOG
    public static final String CLASS_CSG_DIALOG_GRID_PANE = "csg_dialog_grid_pane";
    public static final String CLASS_CSG_DIALOG_HEADER = "csg_dialog_header"; 
    public static final String CLASS_CSG_DIALOG_PROMPT = "csg_dialog_prompt"; 
    public static final String CLASS_CSG_DIALOG_TEXT_FIELD = "csg_dialog_text_field";
    public static final String CLASS_CSG_DIALOG_RADIO_BUTTON = "csg_dialog_radio_button";
    public static final String CLASS_CSG_DIALOG_BOX = "csg_dialog_box";
    public static final String CLASS_CSG_DIALOG_BUTTON = "csg_dialog_button";
    
}