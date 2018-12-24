/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.data.CourseSiteGeneratorData;
import csg.data.Recitation;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditRecitation_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
	Recitation recitation;
	String oldSection, newSection;
	String oldDay, newDay;
	String oldRoom, newRoom;
	String oldTA1, newTA1;
	String oldTA2, newTA2;
	
	public EditRecitation_Transaction(CourseSiteGeneratorData initData,
			Recitation initRecitation, String section, String day, String room,
			String ta1, String ta2){
		data = initData;
		recitation = initRecitation;
		oldSection = recitation.getSection();
		oldDay = recitation.getDay();
		oldRoom = recitation.getRoom();
		oldTA1 = recitation.getTa1();
		oldTA2 = recitation.getTa2();
		newSection = section;
		newDay = day;
		newRoom = room;
		newTA1 = ta1;
		newTA2 = ta2;
	}
	
	public void doTransaction(){
		recitation.setSection(newSection);
		recitation.setDay(newDay);
		recitation.setRoom(newRoom);
		recitation.setTa1(newTA1);
		recitation.setTa2(newTA2);
		data.updateRecitationTable();
	}
	
	public void undoTransaction(){
		recitation.setSection(oldSection);
		recitation.setDay(oldDay);
		recitation.setRoom(oldRoom);
		recitation.setTa1(oldTA1);
		recitation.setTa2(oldTA2);
		data.updateRecitationTable();
	}
}
