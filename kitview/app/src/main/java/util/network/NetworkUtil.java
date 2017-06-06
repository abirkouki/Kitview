package util.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import jcifs.netbios.NbtAddress;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {
	//AG-VAIO (AG): 192.168.1.79 V ; KITVIEWEN-PC (AH): 192.168.235.1 X marche pas quand une VMWare d'installee
	//hp-28(xp) : 192.168.1.6 : V	HYPERV2012R2 (fujitsu): 192.168.1.253 V pcdemo1-pc (ordi noir) : 192.168.1.83 V

	static String ipAddress = null;
	public static String resolveIpFromNetbios(final String hostname){
		ipAddress = null;

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					NbtAddress nbtAddress = NbtAddress.getByName(hostname);
					InetAddress address = nbtAddress.getInetAddress();
					ipAddress = address.getHostAddress();
				}catch (UnknownHostException e){
					e.printStackTrace();
					ipAddress = null;
				}
			}});
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return ipAddress;
	}

	public static void sendData(Context context, byte[] mybytearray, String ip, int port){
		try {
			Socket client = new Socket(ip,port);
			OutputStream outputStream = client.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputStream);

			dos.writeUTF("");
			dos.write(mybytearray);

			outputStream.close();
			client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static boolean isStreamingServerAvailable(ServerSocket serverSocket,Context context, int androidCommunicationPort, String streamingServerIp, int streamingServerPort, String androidDeviceIp){
		String response = null;
		OutputStream outputStream = null;
		Socket client = null;
		DataOutputStream dos= null;
		Socket androidSocket= null;

		try {
			client = new Socket(streamingServerIp,streamingServerPort);
			outputStream = client.getOutputStream();
			dos = new DataOutputStream(outputStream);
			dos.writeUTF("testConnection;"+androidDeviceIp+";"+androidCommunicationPort);
			androidSocket = serverSocket.accept();

			DataInputStream dis = new DataInputStream(androidSocket.getInputStream());
			response = dis.readUTF();
		}catch(Exception e){
			e.printStackTrace();
			response = null;
		}finally{
			try{
				if(outputStream != null)outputStream.close();
				if(client != null)client.close();

				if(dos != null){
					dos.flush();
					dos.close();
				}

				if(androidSocket != null)androidSocket.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return (response != null)?response.equals("OK"):false;
	}

	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected());	
	}

	public static String getIP(Context context){
		WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		if(wifiMan != null){
			WifiInfo wifiInf = wifiMan.getConnectionInfo();

			if(wifiInf != null){
				int ipAddress = wifiInf.getIpAddress();

				if(ipAddress != 0){
					return String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
				}
			}
		}
		return "";
	}
}