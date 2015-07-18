/**
 * 
 */
package fr.funlab.andgws.service.wsrc;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.http.WebSocket;

import fr.funlab.andgws.service.HttpServerService;
import fr.funlab.andgws.service.wsrc.action.ActionTTS;
import fr.funlab.andgws.service.wsrc.action.ActionTTS.ActionTTSListener;
import fr.funlab.util.JsonMessage;

/**
 * @author cyrille
 *
 */
public class WSRCAction extends HttpServerService.WebSocketRequestCallbackBase {

	Context context;

	public WSRCAction(HttpServerService httpServerService, Context context) {
		httpServerService.super(httpServerService);
		this.context = context;
	}

	@Override
	public void onString(final WebSocket webSocket, String str) {

		// final String LOG_TAG = "WSRCAction.onString()";

		try {
			action(webSocket, str);
			webSocket.send(JsonMessage.Recevied().toString());

		} catch (JSONException e) {
			try {
				webSocket.send(JsonMessage
						.Error("Invalid JSON", e.getMessage()).toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	ActionTTS actionTTS = null;

	protected void action(final WebSocket webSocket, String str)
			throws JSONException {

		final String LOG_TAG = "WSRCAction.action()";

		JSONObject jsonObj = null;
		jsonObj = new JSONObject(str);

		String action = jsonObj.getString("action").toLowerCase(
				Locale.getDefault());

		Log.d(LOG_TAG, "action = '" + (action == null ? "null" : action) + "'");

		if (action == null) {
			throw new JSONException("Entry 'action' not found");

		} else if (action.equals("speak")) {
			String message = jsonObj.getString("message");
			if (message == null) {
				throw new JSONException(
						"Entry 'message' for action 'speak' not found");
			}

			if (actionTTS == null) {
				actionTTS = new ActionTTS(this.context);
			}
			actionTTS.speak(message, new ActionTTSListener() {

				@Override
				public void onInit(String utteranceId) {
					try {
						webSocket.send(JsonMessage.Init(utteranceId).toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onStart(String utteranceId) {
					try {
						webSocket.send(JsonMessage.Start(utteranceId).toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onDone(String utteranceId) {

					try {
						webSocket.send(JsonMessage.Done(utteranceId).toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String utteranceId) {
					try {
						webSocket.send(JsonMessage.Error("TTS Error", utteranceId).toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

		} else {
			throw new JSONException("Action '" + action + "' unknow");
		}

	}
}
