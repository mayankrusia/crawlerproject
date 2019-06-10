package com.ge.crawlerproject.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.ge.crawlerproject.jsonhelper.JsonReaderAndValidator;
import com.ge.util.Constants;
import com.ge.util.FileChooser;

public class AppWithMultithread {
	public static List<String> successList = new CopyOnWriteArrayList<>();
	public static List<String> skippedList = new CopyOnWriteArrayList<>();
	public static List<String> errorList = new CopyOnWriteArrayList<>();
	public static BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>();

	@SuppressWarnings({"unchecked", "resource"})
	public static void main(String[] args) throws Throwable {

		// It will allow user to choose the JSON File.
		FileChooser fileChooser = new FileChooser();
		String fileName = fileChooser.getFileName();

		// It will allow user to give Starting Address.
		Scanner userInput = new Scanner(System.in);
		System.out.println(Constants.MSG_USER_INPUT_STARTING_PAGE);
		String startingPage = userInput.nextLine();
		sharedQueue.add(startingPage);
		
		JsonReaderAndValidator jsonReader = new JsonReaderAndValidator();
		JSONArray jsonArray = jsonReader.getJsonArray(fileName);

		// To get all the available Addresses.
		List<String> allAddressList = new ArrayList<>();
		if (null != jsonArray && !jsonArray.isEmpty())
			jsonArray.forEach(nodedata -> allAddressList.add(jsonReader.getNodeAddress((JSONObject) nodedata)));

		if (StringUtils.isNotBlank(startingPage) && allAddressList.contains(startingPage)) {
			TaskWorker newTask = new TaskWorker(sharedQueue, allAddressList, jsonArray, successList, skippedList, errorList);
			Thread worker = new Thread(newTask);
			worker.start();
			while (worker.isAlive()) {}
		} else {
			errorList.add(startingPage);
			System.out.println(Constants.MSG_STARTING_PAGE_NOT_AVAILABLE);
		}

		// To Print Final output in Console.
		System.out.println(Constants.SUCCESS_LIST + successList);
		System.out.println(Constants.SKIPPED_LIST + skippedList);
		System.out.println(Constants.ERROR_LIST + errorList);
	}
}