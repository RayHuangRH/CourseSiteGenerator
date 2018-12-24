/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import javafx.scene.control.TextArea;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditTextArea_Transaction implements jTPS_Transaction{
	
	TextArea area;
	String oldText, newText;
	
	public EditTextArea_Transaction(TextArea initArea, String oldText, String newText){
		area = initArea;
		this.oldText = oldText;
		this.newText = newText;
	}
	
	@Override
	public void doTransaction() {
		area.setText(newText);
	}

	@Override
	public void undoTransaction() {
		area.setText(oldText);
	}
	
}
