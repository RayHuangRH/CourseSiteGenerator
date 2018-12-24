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
public class AddRecitation_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Recitation rec;
    
    public AddRecitation_Transaction(CourseSiteGeneratorData initData, Recitation initRec) {
        data = initData;
        rec = initRec;
    }

    @Override
    public void doTransaction() {
        data.addRecitation(rec);  	
    }

    @Override
    public void undoTransaction() {
        data.removeRecitation(rec);
    }
}
