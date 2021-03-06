package edu.upenn.cis455.mapreduce.myUtil;

import java.util.Date;

public class WorkerStatus {
	private String ip;
	private int port;
	private String job;
	private String status;
	private long keysRead;
	private long keysWrite;
	private Date lastUpdated;
	private Object lock = new Object();
	public String getIPPort() {
		return getIp() + ":" + getPort();
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		if (port == 0) {
			return 80;
		}
		return port; 
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getStatus() {
		return status;
	}
	public void increaseKeysRead() {
		this.keysRead++;
	}
	public void increaseKeysWritten() {
		synchronized (lock) {
            this.keysWrite++;
        }
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getKeysRead() {
		return keysRead;
	}
	public void setKeysRead(long keyRead) {
		this.keysRead = keyRead;
	}
	public long getKeysWrite() {
		return keysWrite;
	}
	public void setKeysWrite(long keyWrite) {
		this.keysWrite = keyWrite;
	}
	public WorkerStatus() {
		
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
		this.lastUpdated = new Date();
	}
	public WorkerStatus(String ip, int port, String job, String status, long keysRead, long keysWrite) {
		this.ip = ip;
		this.port = port;
		this.job = job;
		this.status = status;
		this.keysRead = keysRead;
		this.keysWrite = keysWrite;
		this.lastUpdated = new Date();
	}
	public String toString() {
		return "IP/Port: " + this.ip + ":" + this.port +
				" status: " + this.status +
				" job: " + this.job +
				" keyRead: " + this.keysRead + 
				" keyWrite: " + this.keysWrite;
	}
}
