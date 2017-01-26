package com.redhat.rule.ipc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class IPCServer {
	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public IPCServer() {
		try {
			serverSocket = new ServerSocket(2020);
			socket = serverSocket.accept();
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(Object obj){
		out.println(obj);
	}
	
	public void flush(){
		out.flush();
	}
	
	public Object recv(char[] cbuf) throws IOException{
		return in.read(cbuf);
	}

}
