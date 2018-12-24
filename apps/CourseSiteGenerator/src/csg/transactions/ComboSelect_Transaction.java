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
public class ComboSelect_Transaction implements jTPS_Transaction{
	
	CourseSiteGeneratorData data;
	ComboBox box;
	Object oldText, newText;
	
	public ComboSelect_Transaction(CourseSiteGeneratorData data, ComboBox initBox, Object oldText, Object newText){
		this.data = data;
		box = initBox;
		this.oldText = oldText;
		this.newText = newText;
	}
	
	@Override
	public void doTransaction() {
		box.setValue(newText);
		data.setDirectory();
	}

	@Override
	public void undoTransaction() {
		box.setValue(oldText);
		data.setDirectory();
	}
	
}

