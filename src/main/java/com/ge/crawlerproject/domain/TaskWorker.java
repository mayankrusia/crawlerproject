package com.ge.crawlerproject.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ge.util.Constants;

public class TaskWorker implements Runnable {
	private final BlockingQueue<String> sharedQueue;
	private final List<String> allAddressList;
	private final JSONArray jsonArray;
	private final List<String> successList;
	private final List<String> skippedList;
	private final List<String> errorList;

	public TaskWorker(BlockingQueue<String> sharedQueue, List<String> allAddressList, JSONArray jsonArray,
			List<String> successList, List<String> skippedList, List<String> errorList) {
		this.sharedQueue = sharedQueue;
		this.allAddressList = allAddressList;
		this.jsonArray = jsonArray;
		this.successList = successList;
		this.skippedList = skippedList;
		this.errorList = errorList;
	}

	@Override
	public void run() {
		String newNodeAddress = sharedQueue.remove();
		if (null != newNodeAddress && allAddressList.contains(newNodeAddress)) {
			if (!successList.contains(newNodeAddress))
				successList.add(newNodeAddress);
			
			List<Thread> childWorkers = new ArrayList<>();
			JSONObject node = (JSONObject) jsonArray.get(allAddressList.indexOf(newNodeAddress));
			JSONArray nodeLinksArray = (JSONArray) node.get(Constants.JSON_KEY_LINKS);
			
			for (int i = 0; i < nodeLinksArray.size(); i++) {
				String link = (String) nodeLinksArray.get(i);
				if (StringUtils.isNotBlank(link) && !successList.contains(link) && !errorList.contains(link)) {
					sharedQueue.add(link);
					TaskWorker newTask = new TaskWorker(sharedQueue, allAddressList, jsonArray, successList, skippedList, errorList);
					Thread childWorker = new Thread(newTask);
					childWorker.start();
					childWorkers.add(childWorker);
				} else if (!skippedList.contains(link))
					skippedList.add(link);
			}
			for (Thread childWorker : childWorkers) {
				while (childWorker.isAlive()) {}
			}
		} else if (!errorList.contains(newNodeAddress))
			errorList.add(newNodeAddress);
	}
}