package fr.funlab.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonMessage {

	static public JSONObject Error(String message, String data)
			throws JSONException {
		JSONObject o = new JSONObject();
		o.put("result", "error");
		o.put("message", message);
		o.put("data", data);
		return o;
	}

	static public JSONObject Recevied() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("result", "received");
		o.put("data", null);
		return o;
	}

	static public JSONObject Init(String data) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("result", "received");
		o.put("data", data);
		return o;
	}

	static public JSONObject Start(String data) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("result", "start");
		o.put("data", data);
		return o;
	}

	static public JSONObject Done(String data) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("result", "done");
		o.put("data", data);
		return o;
	}
}
