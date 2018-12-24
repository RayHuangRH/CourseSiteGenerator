/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.CourseSiteGeneratorApp;
import csg.data.CourseSiteGeneratorData;
import static djf.AppPropertyType.LOAD_ERROR_CONTENT;
import static djf.AppPropertyType.LOAD_ERROR_TITLE;
import djf.ui.dialogs.AppDialogsFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import jtps.jTPS_Transaction;
import properties_manager.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;

/**
 *
 * @author rhuan
 */
public class SelectImage_Transaction implements jTPS_Transaction {

	int button;
	CourseSiteGeneratorData data;
	CourseSiteGeneratorApp app;
	String oldFile;
	String newFile;

	public SelectImage_Transaction(int button, CourseSiteGeneratorData data, CourseSiteGeneratorApp app, String oldFile, String newFile) {
		this.button = button;
		this.data = data;
		this.app = app;
		this.oldFile = oldFile;
		this.newFile = newFile;
	}

	@Override
	public void doTransaction() {
		try {
			data.addImage(button, newFile);
		} catch (Exception e) {
			AppDialogsFacade.showMessageDialog(app.getGUIModule().getWindow(), LOAD_ERROR_TITLE, LOAD_ERROR_CONTENT);
		}
	}

	@Override
	public void undoTransaction() {
		try {
			data.addImage(button, oldFile);
		} catch (Exception e) {
			AppDialogsFacade.showMessageDialog(app.getGUIModule().getWindow(), LOAD_ERROR_TITLE, LOAD_ERROR_CONTENT);
		}
	}

}
