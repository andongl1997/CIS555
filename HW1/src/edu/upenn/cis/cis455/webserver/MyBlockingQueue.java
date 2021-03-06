package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

public class MyBlockingQueue<T> {
	
	private Queue<T> bq;
	private int capacity;
//	private final Logger logger = HttpServer.logger;
	
	public MyBlockingQueue(int capacity){
		this.capacity = capacity;
		bq = new LinkedList<T>();
	}
	
	// only one thread could operate this queue at a time
	public synchronized void add(T task) throws InterruptedException{
		
		while (isFull()){		//queue is full
			wait();
		}
//		logger.info("task added to blockingQueue");
		//why not notifyAll() ?
		if (isEmpty())
			notifyAll();
		else
			notify();
		bq.add(task);			
	}
	
	public synchronized T get() throws InterruptedException{
		 
		while (isEmpty()){
			wait();
		}
//		logger.info("task fetched from blockingQueue");
		if (isFull())
			notifyAll();
		return bq.poll();
	}

	private boolean isEmpty(){
		return bq.size() == 0;
	}
	private boolean isFull(){
		return bq.size() == capacity;
	}
}
