/**
 * 
 */
package fr.funlab.andgws.service.wsrc;


import com.koushikdutta.async.http.WebSocket;

import fr.funlab.andgws.service.HttpServerService;

/**
 * @author cyrille
 *
 */

public class WSRCPing extends HttpServerService.WebSocketRequestCallbackBase {

	public WSRCPing( HttpServerService httpServerService) {
		httpServerService.super(httpServerService);
	}

	@Override
	public void onString(WebSocket webSocket, String s) {

		webSocket.send("Pong: " + s);		
	}
	
}