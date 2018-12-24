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
public class RemoveLecture_Transaction implements jTPS_Transaction {
	CourseSiteGeneratorData data;
	Lecture lec;

	public RemoveLecture_Transaction(CourseSiteGeneratorData initData,
			Lecture initLec) {
		data = initData;
		lec = initLec;
	}

	@Override
	public void doTransaction() {
		data.removeLecture(lec);
	}

	@Override
	public void undoTransaction() {
		data.addLecture(lec);
	}
}
