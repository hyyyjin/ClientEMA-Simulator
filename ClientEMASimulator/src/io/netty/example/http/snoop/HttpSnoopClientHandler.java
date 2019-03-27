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

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.global.InitParser;
import com.mir.smartgrid.simulator.ven.ID_Manager;
import com.mir.smartgrid.simulator.ven.SendGet_EventPush;
import com.mir.smartgrid.simulator.ven.TimeFormat;
import com.mir.smartgrid.simulator.ven.VENImpl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class HttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

	URI uri;
	boolean flag = false;
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	String httpResponse, requestID;
	String temp = "";
	ID_Manager idManager;

	public HttpSnoopClientHandler(ID_Manager idManager) {
		// TODO Auto-generated constructor stub
		this.idManager = idManager;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		uri = new URI(HttpSnoopClient.URL + "EiRegisterParty");

		// Prepare the HTTP request.
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
		request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
		request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
		request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
		
		ByteBuf bbuf = Unpooled.copiedBuffer(new VENImpl().QueryRegistration("ba4d8f5c0a"), StandardCharsets.UTF_8);

		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
		request.content().clear().writeBytes(bbuf);

		
		ctx.writeAndFlush(request);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws URISyntaxException, ParserConfigurationException, SAXException, IOException, TransformerException {

		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;

			temp += content.content().toString(CharsetUtil.UTF_8);

			if (content instanceof LastHttpContent) {

				setHttpResponse(temp);
				temp = "";

				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new InputSource(new StringReader(getHttpResponse().toString())));
				NodeList nodes = doc.getDocumentElement().getElementsByTagNameNS("*", "*");

				if (content.content().toString(CharsetUtil.UTF_8).contains("oadrCreatedPartyRegistration")) {

					if (!flag) {
						uri = new URI(HttpSnoopClient.URL + "EiRegisterParty");

						FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
								uri.getRawPath());
						request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
						request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
						request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
						
						ByteBuf bbuf = Unpooled.copiedBuffer(new VENImpl().CreatePartyRegistration(this.idManager.getEmaID(),
								"2.0b", "simpleHttp", null, false, false, Global.pullModel, requestID),
								StandardCharsets.UTF_8);

						request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
						request.content().clear().writeBytes(bbuf);

						ctx.channel().writeAndFlush(request);
						flag = true;

					} else {

						for (int i = 0; i < nodes.getLength(); i++) {
							Node node = nodes.item(i);
							if (node.getNodeName().contains("requestID"))
								requestID = node.getTextContent();
							if (node.getNodeName().contains("venID")) {
								this.idManager.setHASHED_VEN_NAME(node.getTextContent());
//								System.out.println(node.getTextContent());
							}
						}

						
						
						uri = new URI(HttpSnoopClient.URL + "EiReport");

						FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
								uri.getRawPath());
						request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
						request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
						request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
						ByteBuf bbuf = Unpooled.copiedBuffer(new VENImpl()
								.RegisterReport(new TimeFormat().getCurrentTime(), this.idManager.getHASHED_VEN_NAME()),
								StandardCharsets.UTF_8);

						request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
						request.content().clear().writeBytes(bbuf);

						ctx.channel().writeAndFlush(request);
					}

				}
				if (content.content().toString(CharsetUtil.UTF_8).contains("oadrRegisteredReport")) {

					uri = new URI(HttpSnoopClient.URL + "OadrPoll");

					FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
							uri.getRawPath());
					request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
					request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
					request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
					ByteBuf bbuf = Unpooled.copiedBuffer(new VENImpl().Poll(this.idManager.getHASHED_VEN_NAME()),
							StandardCharsets.UTF_8);

					request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
					request.content().clear().writeBytes(bbuf);

					ctx.channel().writeAndFlush(request);

				}

				if (content.content().toString(CharsetUtil.UTF_8).contains("oadrRegisterReport")) {

					for (int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						if (node.getNodeName().contains("requestID"))
							requestID = node.getTextContent();

					}

					uri = new URI(HttpSnoopClient.URL + "EiReport");

					FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
							uri.getRawPath());
					request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
					request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
					request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
					ByteBuf bbuf = Unpooled.copiedBuffer(
							new VENImpl().RegisteredReport(this.idManager.getHASHED_VEN_NAME(), requestID, "200", "OK"),
							StandardCharsets.UTF_8);

					request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
					request.content().clear().writeBytes(bbuf);

					ctx.channel().writeAndFlush(request);

				}

				if (content.content().toString(CharsetUtil.UTF_8).contains("oadrResponse")) {

					if (!this.idManager.isRegistrationFlag()) {

						uri = new URI(HttpSnoopClient.URL + "EiEvent");

						for (int i = 0; i < nodes.getLength(); i++) {
							Node node = nodes.item(i);
							if (node.getNodeName().contains("requestID"))
								requestID = node.getTextContent();

						}

						FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
								uri.getRawPath());
						request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
						request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
						request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
						ByteBuf bbuf = Unpooled.copiedBuffer(
								new VENImpl().RequestEvent(this.idManager.getHASHED_VEN_NAME()),
								StandardCharsets.UTF_8);

						request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
						request.content().clear().writeBytes(bbuf);

						ctx.channel().writeAndFlush(request);

					} else if (this.idManager.isRegistrationFlag()) {

						try {
							Thread.sleep(Global.pollinginterval);

							uri = new URI(HttpSnoopClient.URL + "OadrPoll");

							for (int i = 0; i < nodes.getLength(); i++) {
								Node node = nodes.item(i);
								if (node.getNodeName().contains("requestID"))
									requestID = node.getTextContent();

							}

							FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
									uri.getRawPath());
							request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
							request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
							request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
							ByteBuf bbuf = Unpooled.copiedBuffer(
									new VENImpl().Poll(this.idManager.getHASHED_VEN_NAME()), StandardCharsets.UTF_8);

							request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
							request.content().clear().writeBytes(bbuf);

							ctx.channel().writeAndFlush(request);

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

				if (getHttpResponse().contains("oadrUpdatedReport")) {

					uri = new URI(HttpSnoopClient.URL + "EiReport");

					for (int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						if (node.getNodeName().contains("requestID"))
							requestID = node.getTextContent();

					}

					try {
						Thread.sleep(Global.reportinterval);

						FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
								uri.getRawPath());
						request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
						request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
						request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
						ByteBuf bbuf = Unpooled.copiedBuffer(
								new VENImpl().UpdateReport(this.idManager.getHASHED_VEN_NAME()),
								StandardCharsets.UTF_8);

						request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
						request.content().clear().writeBytes(bbuf);

						ctx.channel().writeAndFlush(request);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (getHttpResponse().contains("oadrDistributeEvent")) {

//					System.out.println(this.idManager.isRegistrationFlag());
					if (!this.idManager.isRegistrationFlag()) {
						
						System.out.println("===================================");
						System.out.println("Session Setup");
						System.out.println("ID:\t\t\t"+this.idManager.getEmaID());
						System.out.println("ID:\t\t\t"+this.idManager.getHASHED_VEN_NAME());
						System.out.println("===================================");
						
						if (Global.pullModel) {

							uri = new URI(HttpSnoopClient.URL + "OadrPoll");

							for (int i = 0; i < nodes.getLength(); i++) {
								Node node = nodes.item(i);
								if (node.getNodeName().contains("requestID"))
									requestID = node.getTextContent();

							}

							FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
									uri.getRawPath());
							request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
							request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
							request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
							ByteBuf bbuf = Unpooled.copiedBuffer(
									new VENImpl().Poll(this.idManager.getHASHED_VEN_NAME()), StandardCharsets.UTF_8);

							request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
							request.content().clear().writeBytes(bbuf);

							ctx.channel().writeAndFlush(request);

						}

						this.idManager.setRegistrationFlag(true);

						if (!Global.pullModel) {
							Controller controller = new Controller(this.idManager.getEmaID());
							controller.setProtocol("HTTP");
							controller.setProfileType("OpenADR2.0b");
							controller.setHASHED_VEN_NAME(this.idManager.getHASHED_VEN_NAME());
							controller.setEmaID(this.idManager.getEmaID());

							new SendGet_EventPush(controller).start();

						}

						uri = new URI(HttpSnoopClient.URL + "EiReport");

						FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
								uri.getRawPath());
						request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
						request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
						request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
						ByteBuf bbuf = Unpooled.copiedBuffer(
								new VENImpl().UpdateReport(this.idManager.getHASHED_VEN_NAME()),
								StandardCharsets.UTF_8);

						request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
						request.content().clear().writeBytes(bbuf);

						ctx.channel().writeAndFlush(request);
						
					}

					else if (this.idManager.isRegistrationFlag()) {

						DocumentBuilderFactory dbFactory_event = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder_event = dbFactory_event.newDocumentBuilder();
						Document doc_event = dBuilder_event
								.parse(new InputSource(new StringReader(getHttpResponse().toString())));
						NodeList nodes_event = doc_event.getDocumentElement().getElementsByTagNameNS("*", "*");

						double value = 0;
						String modificationNumber = "", eventID = "";
						for (int i = 0; i < nodes_event.getLength(); i++) {
							Node node = nodes_event.item(i);
							if (node.getNodeName().contains("value"))
								value = Double.parseDouble(node.getTextContent());
							if (node.getNodeName().contains("modificationNumber"))
								modificationNumber = node.getTextContent();
							if (node.getNodeName().contains("requestID"))
								requestID = node.getTextContent();
							if (node.getNodeName().contains("eventID")) {
								eventID = node.getTextContent();
							}
						}

						System.out.println("=============");
						System.out.println("Threshold" + value);
						System.out.println("EMA ID" + this.idManager.getEmaID());
						System.out.println("=============");

						uri = new URI(HttpSnoopClient.URL + "EiEvent");

						FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
								uri.getRawPath());
						request.headers().set(HttpHeaderNames.HOST, HttpSnoopClient.host);
						request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
						request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
						ByteBuf bbuf = Unpooled
								.copiedBuffer(new VENImpl().CreatedEvent(this.idManager.getHASHED_VEN_NAME(), "200",
										"OK", eventID, modificationNumber, requestID), StandardCharsets.UTF_8);

						request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
						request.content().clear().writeBytes(bbuf);

						ctx.channel().writeAndFlush(request);

					}

				}
				

			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("여기발생");
		cause.printStackTrace();
		ctx.close();
	}

	public String getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(String httpResponse) {
		this.httpResponse = httpResponse;
	}

}

