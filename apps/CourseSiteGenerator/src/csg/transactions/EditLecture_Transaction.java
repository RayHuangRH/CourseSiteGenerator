/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.data.CourseSiteGeneratorData;
import csg.data.Lecture;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditLecture_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
	Lecture lecture;
	String oldSection, newSection;
	String oldDay, newDay;
	String oldTime, newTime;
	String oldRoom, newRoom;
	
	public EditLecture_Transaction(CourseSiteGeneratorData initData,
			Lecture initLecture, String section, String day, String time,
			String room){
		data = initData;
		lecture = initLecture;
		oldSection = lecture.getSection();
		oldDay = lecture.getDay();
		oldTime = lecture.getTime();
		oldRoom = lecture.getRoom();
		newSection = section;
		newDay = day;
		newTime = time;
		newRoom = room;
	}
	
	public void doTransaction(){
		lecture.setSection(newSection);
		lecture.setDay(newDay);
		lecture.setTime(newTime);
		lecture.setRoom(newRoom);
		data.updateLectureTable();
	}
	
	public void undoTransaction(){
		lecture.setSection(oldSection);
		lecture.setDay(oldDay);
		lecture.setTime(oldTime);
		lecture.setRoom(oldRoom);
		data.updateLectureTable();
	}
}
