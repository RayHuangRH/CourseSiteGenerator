/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import javafx.scene.control.TextField;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditTextField_Transaction implements jTPS_Transaction{
	
	TextField field;
	String oldText, newText;
	
	public EditTextField_Transaction(TextField initField, String oldText, String newText){
		field = initField;
		this.oldText = oldText;
		this.newText = newText;
	}
	
	@Override
	public void doTransaction() {
		field.setText(newText);
	}

	@Override
	public void undoTransaction() {
		field.setText(oldText);
	}
	
}
