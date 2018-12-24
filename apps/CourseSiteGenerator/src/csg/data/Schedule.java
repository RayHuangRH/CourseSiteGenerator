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
public class Schedule <E extends Comparable<E>> implements Comparable<E>{
	private final StringProperty type;
    private final StringProperty date;
    private final StringProperty title;
    private final StringProperty topic;
	private final StringProperty link;
	
	public Schedule(String initType, String initDate, String initTitle, String initTopic, String initLink){
		type = new SimpleStringProperty(initType);
		date = new SimpleStringProperty(initDate);
		title = new SimpleStringProperty(initTitle);
		topic = new SimpleStringProperty(initTopic);
		link = new SimpleStringProperty(initLink);
	}
	
	public String getType() {
        return type.get();
    }
    
    public void setType(String initType) {
        type.setValue(initType);
    }
	
	public String getDate() {
        return date.get();
    }
    
    public void setDate(String initDate) {
        date.setValue(initDate);
    }
	
	public String getTitle() {
        return title.get();
    }
    
    public void setTitle(String initTitle) {
        title.setValue(initTitle);
    }
	
	public String getTopic() {
        return topic.get();
    }
    
    public void setTopic(String initTopic) {
        topic.setValue(initTopic);
    }
	
	public String getLink(){
		return link.get();
	}
	
	public void setLink(String initLink){
		link.setValue(initLink);
	}

	@Override
	public int compareTo(E otherSchedule) {
		return getDate().compareTo(((Schedule)otherSchedule).getDate());
	}
}
