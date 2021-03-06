package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class RequestReceiver extends Thread{
	
	private int portNumber;
	private String rootDir;
	private Boolean acceptRequest = true;
	private ServerSocket serverSocket;
	private final int serverSocketSize = 300;
	//shared blockingQueue
	private MyBlockingQueue<Socket> taskQueue;
	
	static final Logger logger = HttpServer.logger;
	
	public int getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(int portNum) {
		portNumber = portNum;
	}
	public String getRootDir() {
		return rootDir;
	}
	public void setRootDir(String Dir) {
		rootDir = Dir;
	}
	
	public RequestReceiver(int port, String dir, MyBlockingQueue<Socket> taskQueue) throws IOException{
		super("Deamon thread");
		portNumber = port;
		rootDir = new String(dir);
		this.taskQueue = taskQueue;
		acceptRequest = true;
		serverSocket = new ServerSocket(portNumber, serverSocketSize);
	}
	
	public void run() {	
		//while accepting requests
		while (acceptRequest){
//				System.out.print(acceptRequest);
			try{
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(3000);			//prevent hanging
				taskQueue.add(socket);
			}catch (IOException e) {
				logger.info("Server socket closed");
			} catch (InterruptedException e) {
				logger.error("Could not add/receive task");
		//		e.printStackTrace();
			} 

		}
    }
	
	public void shutdown(){
		//stop accepting requests		
		this.acceptRequest = false;
		try {
			//Closes this socket. Any thread currently blocked in accept() will throw a SocketException. 
			//no longer accept new requests
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Can not close serverSocket");
	//		e.printStackTrace();
		}
	}
}
