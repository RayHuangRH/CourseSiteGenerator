package csg.transactions;

import csg.data.CourseSiteGeneratorData;
import java.time.LocalDate;
import jtps.jTPS_Transaction;
import csg.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class EditTA_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
	TeachingAssistantPrototype taToEdit;
    String oldName, newName;
    String oldEmail, newEmail;
    String oldType, newType;
    
    public EditTA_Transaction(CourseSiteGeneratorData initData,
			TeachingAssistantPrototype initTAToEdit, 
            String name, String email, String type) {
		data = initData;
        taToEdit = initTAToEdit;
        oldName = initTAToEdit.getName();
        oldEmail = initTAToEdit.getEmail();
        oldType = initTAToEdit.getType();
        newName = name;
        newEmail = email;
        newType = type;
    }


    @Override
    public void doTransaction() {
        taToEdit.setName(newName);
        taToEdit.setEmail(newEmail);
        taToEdit.setType(newType);
		data.updateHoursTableView();
    }

    @Override
    public void undoTransaction() {
        taToEdit.setName(oldName);
        taToEdit.setEmail(oldEmail);
        taToEdit.setType(oldType);
		data.updateHoursTableView();
    }
}