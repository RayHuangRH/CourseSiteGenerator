package csg.clipboard;

import djf.components.AppClipboardComponent;


public class CourseSiteGeneratorClipboard implements AppClipboardComponent {

    public CourseSiteGeneratorClipboard(String message) {
		//NOT NEEDED 
    }

    @Override
    public void cut() {
    }

    @Override
    public void copy() {
    }

    @Override
    public void paste() {
    }    

    @Override
    public boolean hasSomethingToCut() {
		return false;
    }

    @Override
    public boolean hasSomethingToCopy() {
		return false;
    }

    @Override
    public boolean hasSomethingToPaste() {
		return false;
	}
}