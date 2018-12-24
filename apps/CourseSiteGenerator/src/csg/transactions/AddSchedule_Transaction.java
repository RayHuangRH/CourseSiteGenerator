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
public class AddSchedule_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Schedule schedule;
    
    public AddSchedule_Transaction(CourseSiteGeneratorData initData, Schedule initSchedule) {
        data = initData;
        schedule = initSchedule;
    }

    @Override
    public void doTransaction() {
        data.addSchedule(schedule);  	
    }

    @Override
    public void undoTransaction() {
        data.removeSchedule(schedule);  	
    }
}
