/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author rhuan
 */
public class Lecture <E extends Comparable<E>> implements Comparable<E>{

	private final StringProperty section;
	private final StringProperty day;
	private final StringProperty time;
	private final StringProperty room;

	public Lecture(String initSection, String initDays, String initTime, String initRoom) {
		section = new SimpleStringProperty(initSection);
		day = new SimpleStringProperty(initDays);
		time = new SimpleStringProperty(initTime);
		room = new SimpleStringProperty(initRoom);
	}

	public String getSection() {
		return section.get();
	}

	public void setSection(String initSection) {
		section.set(initSection);
	}

	public String getDay() {
		return day.get();
	}

	public void setDay(String initDays) {
		day.set(initDays);
	}

	public String getTime() {
		return time.get();
	}

	public void setTime(String initTime) {
		time.set(initTime);
	}

	public String getRoom() {
		return room.get();
	}

	public void setRoom(String initRoom) {
		room.set(initRoom);
	}
	
	public int compareTo(E otherLecture) {
        return getSection().compareTo(((Lecture)otherLecture).getSection());
    }
}
