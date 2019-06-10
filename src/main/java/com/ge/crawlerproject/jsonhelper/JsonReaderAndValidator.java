package com.ge.crawlerproject.jsonhelper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ge.util.Constants;

public class JsonReaderAndValidator {

	public JSONArray getJsonArray(String filePath) {
		JSONParser jsonParser = new JSONParser();
		JSONArray jsonArray = new JSONArray();
		try (FileReader reader = new FileReader(filePath)) {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			jsonArray = (JSONArray) jsonObject.get(Constants.JSON_KEY_PAGES);
		} catch (FileNotFoundException e) {
			System.out.println(Constants.MSG_FILE_NOT_AVAILABLE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(Constants.MSG_NOT_VALID_JSON);
		}
		return jsonArray;
	}

	public String getNodeAddress(JSONObject nodeDetails) {
		return nodeDetails.get(Constants.JSON_KEY_ADDRESS).toString();
	}
}