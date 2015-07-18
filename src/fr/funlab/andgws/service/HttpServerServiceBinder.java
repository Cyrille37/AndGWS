package fr.funlab.andgws.service;

import android.os.RemoteException;
import android.util.Log;

public class HttpServerServiceBinder extends fr.funlab.andgws.service.IHttpServerService.Stub {

    private HttpServerService service = null;
    
    public HttpServerServiceBinder(HttpServerService service) {
        super();
    	Log.i("", "HttpServerServiceBinder()");
        this.service = service;
    }

	@Override
	public int getPid() throws RemoteException {
    	Log.i("", "HttpServerServiceBinder getPid()");
		return android.os.Process.myPid();
	}

	@Override
	public Data getData() throws RemoteException {
    	Log.i("", "HttpServerServiceBinder getData()");
		return service.getData();
	}

}
