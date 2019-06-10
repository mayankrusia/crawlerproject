package com.ge.util;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser {
	public String getFileName() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(Constants.DIALOG_TITEL);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.FILE_EXTENTION_TEXTFILES, Constants.FILE_EXTENTION_JSON);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		String selectedFile = "";
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile().getName();
			if (selectedFile.contains(".json")) {
				selectedFile = chooser.getSelectedFile().toString().replace(Constants.BACK_SLASH, Constants.FOWARD_SLASH);
			} else {
				System.out.println(Constants.MSG_NOT_JSON_FILE);
				selectedFile = "";
			}
		}
		return selectedFile;
	}
}