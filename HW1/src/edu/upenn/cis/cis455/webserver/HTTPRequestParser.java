package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class HTTPRequestParser {
	
	private String protocol;
	private String reqUrl;
	private String method;
	private List<String> headers;
//	private String body;				do not deal with body
	private CODE code;			
		
	public String getProtocol() {
		return this.protocol;
	}
	
	public String getUrl() {
		return this.reqUrl;
	}
	
	public HTTPRequestParser() {
		code = CODE.NORMAL;
		headers = new ArrayList<String>();
	}
	
	public CODE getCode() {
		return code;
	}

	public enum CODE {
		BADREQ, BADDIR, SHUTDOWN, CONTROL, NOFOUND, HEAD, 
		LISTDIR, FILE, NORMAL
	}
	
	public void parseHttpRequest(Socket socket) throws IOException{
		
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//parse the initial line
		String line = in.readLine();
		parseInitialLine(line);
		//get the headers
		while ((line = in.readLine()) != null) {
			if (line.length() == 0){
				break;
			}
			headers.add(line);
		}	
		filterRequest();	
	}
	

	// filter out invalid request
	private void filterRequest(){
		
		// not GET or HEAD request
		if (!"GET".equalsIgnoreCase(this.method) && !"HEAD".equalsIgnoreCase(this.method) ){
			this.code = CODE.BADREQ;
			return;
		}
		this.reqUrl = parseURL(this.reqUrl);
		// security check
		if (reqUrl == null){
			this.code = CODE.BADDIR;
			return;
		}
		System.out.println("url: " + reqUrl);
		// shutdown url
		if ("/shutdown".equalsIgnoreCase(this.reqUrl)){
			this.code = CODE.SHUTDOWN;
			return;
		}
		// control url
		if ("/control".equalsIgnoreCase(this.reqUrl)){
			this.code = CODE.CONTROL;
			return;
		}
		reqUrl = HttpServer.rootDir + reqUrl;
		File file = new File(reqUrl);
		if (!file.exists()) {
			this.code = CODE.BADDIR;
			return;
		}
		if (file.isDirectory()) {
			this.code = CODE.LISTDIR;
		}
		if (file.isFile()) {
			this.code = CODE.FILE;
		}
	}
	
	//parse the request url
	private String simplifyPath(String path) {
        if (path == null)   return null;
        int level = 0;
        Deque<String> st = new ArrayDeque<String>();
        String[] folder = path.split("/");
        for (int i = 0; i < folder.length; i++) {
             if (".".equals(folder[i]) || "".equals(folder[i])) continue;
             if ("..".equals(folder[i])){
                 level--;
                 st.pollLast();         //return null if empty
             }else{
                 if (level >= 0)
                    st.offer(folder[i]);
                 level++;
             }    
        }
        if (level < 0)	return null;
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        while (st.size() > 0){
            sb.append(st.pollFirst());
            sb.append("/");
        }
        if (sb.length() > 1)    sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
	
	// parse the request url
	private String parseURL(String dir) {
		if (dir == null)	return null;
		String prefix = HttpServer.rootDir;
		dir = prefix + dir;
		String newDir = simplifyPath(dir);
//		System.out.println(newDir);
		if (newDir == null || newDir.length() < prefix.length())	//should at least equals to prefix
			return null;
		String newPrefix = newDir.substring(0, prefix.length());
		if (!newPrefix.equals(prefix)) {
			return null;
		}
		newDir = newDir.substring(prefix.length());
//		System.out.println(newDir);
		return newDir;
	}
	
	private void parseInitialLine(String line){	
		if (line == null) this.code = CODE.BADDIR;
		else {
			String[] tmp = line.split(" ");
			if (tmp.length != 3){
				this.code = CODE.BADREQ;
			}else{
				method = tmp[0];
				reqUrl = tmp[1];
				protocol = tmp[2];
			}
		}
	}
	
}
