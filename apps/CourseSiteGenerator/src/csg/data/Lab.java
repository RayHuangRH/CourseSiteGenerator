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
public class Lab <E extends Comparable<E>> implements Comparable<E>{
	
	private final StringProperty section;
	private final StringProperty day;
	private final StringProperty room;
	private final StringProperty ta1;
	private final StringProperty ta2;

	public Lab(String initSection, String initDays, String initRoom, String initTA1, String initTA2) {
		section = new SimpleStringProperty(initSection);
		day = new SimpleStringProperty(initDays);
		room = new SimpleStringProperty(initRoom);
		ta1 = new SimpleStringProperty(initTA1);
		ta2 = new SimpleStringProperty(initTA2);
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

	public String getRoom() {
		return room.get();
	}

	public void setRoom(String initRoom) {
		room.set(initRoom);
	}
	
	public String getTa1() {
		return ta1.get();
	}

	public void setTa1(String initTA1) {
		ta1.set(initTA1);
	}
	
	public String getTa2() {
		return ta2.get();
	}

	public void setTa2(String initTA2) {
		ta2.set(initTA2);
	}

	@Override
	public int compareTo(E otherLab) {
        return getSection().compareTo(((Lab)otherLab).getSection());
    }
}
