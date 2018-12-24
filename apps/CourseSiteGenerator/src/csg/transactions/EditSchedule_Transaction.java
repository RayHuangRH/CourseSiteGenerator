/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.data.CourseSiteGeneratorData;
import csg.data.Schedule;
import jtps.jTPS_Transaction;

/**
 *
 * @author rhuan
 */
public class EditSchedule_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
	Schedule schedule;
	String oldType, newType;
	String oldDate, newDate;
	String oldTitle, newTitle;
	String oldTopic, newTopic;
	String oldLink, newLink;
	
	public EditSchedule_Transaction(CourseSiteGeneratorData initData,
			Schedule initSchedule, String type, String date, String title,
			String topic, String link){
		data = initData;
		schedule = initSchedule;
		oldType = schedule.getType();
		oldDate = schedule.getDate();
		oldTitle = schedule.getTitle();
		oldTopic = schedule.getTopic();
		oldLink = schedule.getLink();
		newType = type;
		newDate = date;
		newTitle = title;
		newTopic = topic;
		newLink = link;
	}
	
	public void doTransaction(){
		schedule.setType(newType);
		schedule.setDate(newDate);
		schedule.setTitle(newTitle);
		schedule.setTopic(newTopic);
		schedule.setLink(newLink);
		data.updateScheduleTable();
	}
	
	public void undoTransaction(){
		schedule.setType(oldType);
		schedule.setDate(oldDate);
		schedule.setTitle(oldTitle);
		schedule.setTopic(oldTopic);
		schedule.setLink(oldLink);
		data.updateScheduleTable();
	}
}
