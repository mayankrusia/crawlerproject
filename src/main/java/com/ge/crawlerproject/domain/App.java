package com.ge.crawlerproject.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ge.crawlerproject.jsonhelper.JsonReaderAndValidator;
import com.ge.util.Constants;
import com.ge.util.FileChooser;

public class App {
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) {
		List<String> successList = new ArrayList<>();
		List<String> skippedList = new ArrayList<>();
		List<String> errorList = new ArrayList<>();
		Queue<String> queue = new LinkedList<>();

		FileChooser fileChooser = new FileChooser();
		String fileName = fileChooser.getFileName();

		Scanner userInput = new Scanner(System.in);
		System.out.println(Constants.MSG_USER_INPUT_STARTING_PAGE);
		String startingPage = userInput.nextLine();
		queue.add(startingPage);

		JsonReaderAndValidator jsonReader = new JsonReaderAndValidator();
		JSONArray jsonArray = jsonReader.getJsonArray(fileName);
		List<String> allAddressList = new ArrayList<>();

		// List of All Addresses
		if (null != jsonArray && !jsonArray.isEmpty())
			jsonArray.forEach(nodedata -> allAddressList.add(jsonReader.getNodeAddress((JSONObject) nodedata)));

		if (StringUtils.isNotBlank(startingPage) && allAddressList.contains(startingPage)) {
			while (!queue.isEmpty()) {
				String currentAddress = queue.remove();
				if (null != currentAddress && allAddressList.contains(currentAddress)) {
					if (!successList.contains(currentAddress))
						successList.add(currentAddress);
					JSONObject tempObj = (JSONObject) jsonArray.get(allAddressList.indexOf(currentAddress));
					JSONArray tempLinksArray = (JSONArray) tempObj.get(Constants.JSON_KEY_LINKS);
					for (int i = 0; i < tempLinksArray.size(); i++) {
						String link = (String) tempLinksArray.get(i);
						if (StringUtils.isNotBlank(link) && !successList.contains(link) && !errorList.contains(link))
							queue.add(link);
						else if (!skippedList.contains(link))
							skippedList.add(link);
					}
				} else if (!errorList.contains(currentAddress))
					errorList.add(currentAddress);
			}
		} else {
			System.out.println(Constants.MSG_STARTING_PAGE_NOT_AVAILABLE);
		}
		System.out.println(Constants.SUCCESS_LIST + successList);
		System.out.println(Constants.ERROR_LIST + errorList);
		System.out.println(Constants.SKIPPED_LIST + skippedList);
	}
}
