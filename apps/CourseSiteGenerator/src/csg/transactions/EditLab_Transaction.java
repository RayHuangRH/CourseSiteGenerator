/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.data.CourseSiteGeneratorData;
import csg.data.Lab;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditLab_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
	Lab lab;
	String oldSection, newSection;
	String oldDay, newDay;
	String oldRoom, newRoom;
	String oldTA1, newTA1;
	String oldTA2, newTA2;
	
	public EditLab_Transaction(CourseSiteGeneratorData initData,
			Lab initLab, String section, String day, String room,
			String ta1, String ta2){
		data = initData;
		lab = initLab;
		oldSection = lab.getSection();
		oldDay = lab.getDay();
		oldRoom = lab.getRoom();
		oldTA1 = lab.getTa1();
		oldTA2 = lab.getTa2();
		newSection = section;
		newDay = day;
		newRoom = room;
		newTA1 = ta1;
		newTA2 = ta2;
	}
	
	public void doTransaction(){
		lab.setSection(newSection);
		lab.setDay(newDay);
		lab.setRoom(newRoom);
		lab.setTa1(newTA1);
		lab.setTa2(newTA2);
		data.updateLabTable();
	}
	
	public void undoTransaction(){
		lab.setSection(oldSection);
		lab.setDay(oldDay);
		lab.setRoom(oldRoom);
		lab.setTa1(oldTA1);
		lab.setTa2(oldTA2);
		data.updateLabTable();
	}
}
