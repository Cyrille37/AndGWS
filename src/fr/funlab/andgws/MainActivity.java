package fr.funlab.andgws;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import fr.funlab.andgws.R;
import fr.funlab.andgws.service.Data;
import fr.funlab.andgws.service.HttpServerService;
import fr.funlab.andgws.service.IHttpServerService;

public class MainActivity extends Activity {

	IHttpServerService httpServerService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Display network interface(s)

		//ArrayList<InetAddress> addresses = getInterfaces();
		TextView textviewIpAddr = (TextView) findViewById(R.id.textView_IpAddress);
		textviewIpAddr.setText("");
		for( InetAddress addr : getInterfaces() )
		{
			textviewIpAddr.append(addr.toString());			
		}

		final Button buttonBind = (Button) findViewById(R.id.buttonBind);
		final Button buttonStart = (Button) findViewById(R.id.buttonStart);

		if( ! isNetworkAvailable() )
		{
			buttonBind.setEnabled(false);
			buttonStart .setEnabled(false);
			return ;
		}

		// Button "Bind"

		buttonBind.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonBind click()");

				// Intent intent = new Intent();
				// intent.setClassName("fr.funlab.ttshttpserver.service",
				// "fr.funlab.ttshttpserver.service.HttpServerService");
				Intent intent = new Intent(MainActivity.this,
						HttpServerService.class);

				ServiceConnection remoteConnection = new ServiceConnection() {

					@Override
					public void onServiceDisconnected(ComponentName name) {
						Log.i(this.getClass().getName(),
								"onServiceDisconnected(), ComponentName = "
										+ name);
						httpServerService = null;
					}

					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						Log.i(this.getClass().getName(), "onServiceConnected()");

						httpServerService = IHttpServerService.Stub
								.asInterface(service);
						try {
							httpServerService.getPid();
							Data d = httpServerService.getData();
							Log.i(this.getClass().getName(),
									"onServiceConnected() d.getTemperature() = "
											+ d.getRequestsCount());

						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							Log.e("", e.getMessage());
						}
					}

				};

				// Context.BIND_AUTO_CREATE
				// https://developer.android.com/reference/android/content/Context.html#BIND_AUTO_CREATE
				bindService(intent, remoteConnection, Context.BIND_AUTO_CREATE);

			}
		});

		// Button "Start"

		buttonStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonStart click()");

				Intent intent = new Intent(MainActivity.this,
						HttpServerService.class);
				startService(intent);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// si aucun réseau n'est disponible, networkInfo sera null sinon,
		// vérifier si nous sommes connectés
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private ArrayList<InetAddress> getInterfaces() {
		ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()  && inetAddress instanceof Inet4Address) {
						addresses.add(inetAddress);						
					}
				}
			}

		} catch (SocketException e) {
			Log.e("getInterfaces", "Problem enumerating network interfaces");
		}
		return addresses ;
	}

}
