/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import java.time.LocalDate;
import javafx.scene.control.DatePicker;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditDatePicker_Transaction implements jTPS_Transaction{
	
	DatePicker picker;
	LocalDate oldText, newText;
	
	public EditDatePicker_Transaction(DatePicker initDate, LocalDate oldText, LocalDate newText){
		picker = initDate;
		this.oldText = oldText;
		this.newText = newText;
	}
	
	@Override
	public void doTransaction() {
		picker.setValue(newText);
	}

	@Override
	public void undoTransaction() {
		picker.setValue(oldText);
	}
	
}
