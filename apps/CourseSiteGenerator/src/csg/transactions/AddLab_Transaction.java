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
public class AddLab_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Lab lab;
    
    public AddLab_Transaction(CourseSiteGeneratorData initData, Lab initLab) {
        data = initData;
        lab = initLab;
    }

    @Override
    public void doTransaction() {
        data.addLab(lab);  	
    }

    @Override
    public void undoTransaction() {
        data.removeLab(lab);
    }
}
