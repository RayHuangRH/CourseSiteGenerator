/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import javafx.scene.control.CheckBox;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class CheckBox_Transaction implements jTPS_Transaction{
	CheckBox box;
	boolean selected;
	
	public CheckBox_Transaction(CheckBox box, boolean selected){
		this.box = box;
		this.selected = selected;
	}
	
	public void doTransaction(){
		box.setSelected(selected);
	}
	
	public void undoTransaction(){
		box.setSelected(!selected);
	}
}
