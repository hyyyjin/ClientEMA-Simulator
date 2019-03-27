/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.http.snoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLException;

import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.global.InitParser;
import com.mir.smartgrid.simulator.ven.ID_Manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpSnoopServer}.
 */
public class HttpSnoopClient {

	private int startNum, endNum;
	private String emaID;

	public HttpSnoopClient(int startNum, int endNum) {
		setStartNum(startNum);
		setEndNum(endNum);
	}

	public HttpSnoopClient(String emaID) {
		// setStartNum(startNum);
		// setEndNum(endNum);
		setEmaID(emaID);

	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("start ex_1/10");

		String temp = br.readLine();

		Global.startNum = Integer.parseInt(temp.split("/")[0]);
		Global.endNum = Integer.parseInt(temp.split("/")[1]);

		new HttpSnoopClient(Global.startNum, Global.endNum).start();
	}

	public static final String URL = System.getProperty("url", "http://166.104.28.51:8080/OpenADR2/Simple/2.0b/");
	public static String host = "166.104.28.51";
	public static int port = 8080;

	public void start() throws SSLException, URISyntaxException {
		// public static void main(String[] args) throws Exception {
		URI uri = new URI(URL);
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		// Configure SSL context if necessary.
		final boolean ssl = "https".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} else {
			sslCtx = null;
		}

		new InitParser();

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup(8);

		try {

			for (int i = Global.startNum; i <= Global.endNum; i++) {

				Bootstrap b = new Bootstrap();

				ID_Manager idManager = new ID_Manager();

				idManager.setEmaID("CLIENT_EMA" + i);

				Thread.sleep(10);

				b.group(group).channel(NioSocketChannel.class)
						.handler(new HttpSnoopClientInitializer(sslCtx, idManager));

				b.connect(host, port);

			}

			// Original
			// for (int i = getStartNum(); i <= endNum; i++) {
			//
			// Bootstrap b = new Bootstrap();
			//
			// ID_Manager idManager = new ID_Manager();
			// idManager.setEmaID(getEmaID());
			//
			// Thread.sleep(10);
			//
			// b.group(group).channel(NioSocketChannel.class)
			// .handler(new HttpSnoopClientInitializer(sslCtx, idManager));
			//
			// b.connect(host, port);
			//
			// }
			// Original End
			// Bootstrap b = new Bootstrap();
			// b.group(group).channel(NioSocketChannel.class).handler(new
			// HttpSnoopClientInitializer(sslCtx));
			//
			// // Make the connection attempt.
			// Channel ch = b.connect(host, port).sync().channel();

			// // Set some example cookies.
			// request.headers().set(HttpHeaderNames.COOKIE,
			// ClientCookieEncoder.STRICT
			// .encode(new DefaultCookie("my-cookie", "foo"), new
			// DefaultCookie("another-cookie", "bar")));

			// request.content()

			// Send the HTTP request.
			// ch.writeAndFlush(request);

			// Wait for the server to close the connection.
			// ch.closeFuture().sync();
		} catch (Exception e) {

		}
		// finally {
		// // Shut down executor threads to exit.
		// group.shutdownGracefully();
		// }
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getEndNum() {
		return endNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}

	public String getEmaID() {
		return emaID;
	}

	public void setEmaID(String emaID) {
		this.emaID = emaID;
	}

}
