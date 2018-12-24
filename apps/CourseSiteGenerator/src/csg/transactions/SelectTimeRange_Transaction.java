/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.data.CourseSiteGeneratorData;
import javafx.scene.control.ComboBox;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class SelectTimeRange_Transaction implements jTPS_Transaction{
	
	CourseSiteGeneratorData data;
	ComboBox startBox, endBox;
	String oldStartText, newStartText;
	String oldEndText, newEndText;
	
	public SelectTimeRange_Transaction(CourseSiteGeneratorData data, ComboBox initStartBox, ComboBox initEndBox, String oldStartText, String newStartText, String oldEndText, String newEndText){
		this.data = data;
		startBox = initStartBox;
		endBox = initEndBox;
		this.oldStartText = oldStartText;
		this.newStartText = newStartText;
		this.oldEndText = oldEndText;
		this.newEndText = newEndText;
	}
	
	@Override
	public void doTransaction() {
		startBox.setValue(newStartText);
		endBox.setValue(newEndText);
		data.setTimeRange(newStartText, newEndText);
	}

	@Override
	public void undoTransaction() {
		startBox.setValue(oldStartText);
		endBox.setValue(oldEndText);
		data.setTimeRange(oldStartText, oldEndText);
	}
	
}

