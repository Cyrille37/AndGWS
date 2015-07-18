package fr.funlab.andgws.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServer.WebSocketRequestCallback;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import fr.funlab.andgws.service.wsrc.WSRCAction;
import fr.funlab.andgws.service.wsrc.WSRCPing;
import fr.funlab.andgws.service.wsrc.action.ActionTTS;

public class HttpServerService extends Service {

	final String LOG_TAG = this.getClass().getSimpleName();
	final int DEFAULT_LISTEN_PORT = 5000;

	private HttpServerServiceBinder binder;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(LOG_TAG, "HttpServerService onCreate()");

		startWebSocketServer();
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(LOG_TAG, "HttpServerService onStartCommand()");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG, "HttpServerService onBind()");
		return this.binder;
	}

	public Data getData() {
		Log.i(LOG_TAG, "HttpServerService getData()");
		return new Data(this.requestsCount, this._sockets.size());
	}

	AsyncHttpServer server = null;

	List<WebSocket> _sockets = new ArrayList<WebSocket>();

	long requestsCount = 0;

	protected void startWebSocketServer() {
		if (server != null)
			return;

		Log.i(LOG_TAG, "startWebSocketServer()");

		server = new AsyncHttpServer();
		server.websocket("/ping", new WSRCPing(this));
		server.websocket("/action", new WSRCAction(this, this.getApplicationContext()));
		server.listen(DEFAULT_LISTEN_PORT);

	}

	public abstract class WebSocketRequestCallbackBase implements
			WebSocketRequestCallback {

		HttpServerService httpServerService;

		public WebSocketRequestCallbackBase(HttpServerService httpServerService) {
			this.httpServerService = httpServerService;
		}

		@Override
		public final void onConnected(final WebSocket webSocket,
				AsyncHttpServerRequest request) {

			Log.i(LOG_TAG, "HttpServerService server.onConnected()");

			this.httpServerService._sockets.add(webSocket);

			// Use this to clean up any references to your websocket
			webSocket.setClosedCallback(new CompletedCallback() {
				@Override
				public void onCompleted(Exception ex) {
					try {
						if (ex != null)
							Log.e("WebSocket", "Error");
					} finally {
						_sockets.remove(webSocket);
					}
				}
			});

			webSocket.setStringCallback(new StringCallback() {
				@Override
				public void onStringAvailable(String s) {

					Log.d(this.getClass().getName(), "onStringAvailable() ");

					onString(webSocket, s);
					requestsCount++;
				}
			});
			
		}

		public abstract void onString(final WebSocket webSocket, String s);
	}

}
