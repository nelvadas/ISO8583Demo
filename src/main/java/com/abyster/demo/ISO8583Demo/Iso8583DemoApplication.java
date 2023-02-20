package com.abyster.demo.ISO8583Demo;

import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.GenericValidatingPackager;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.Random;
import java.util.logging.Logger;

@SpringBootApplication
public class Iso8583DemoApplication {
static final Logger logger = Logger.getLogger(Iso8583DemoApplication.class.getName());
	public static void main(String[] args)  {
		SpringApplication.run(Iso8583DemoApplication.class, args);

		String serverIp = "10.100.0.6";
		int serverPort = 9044;

		Socket socket = null;
		try {

			logger.info(String.format("Connecting to server: %s:%s", serverIp,serverPort));
			socket = new Socket(serverIp, serverPort);



		// create a new ISO8583 message object
		InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = socket.getOutputStream();
			Random rnd = new Random();
			int number = rnd.nextInt(999999);
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setMTI("1804");
		isoMsg.set(11, number+"");
		isoMsg.set(12, "230210154149");
		isoMsg.set(24, "831");

			logger.info(String.format(" Field[11] %s", number));

			//ISOPackager packager = new GenericPackager("iso8583packager.xml");
			ISOPackager packager = new GenericValidatingPackager();
			isoMsg.setPackager(packager);
		byte[] isoMsgBytes = isoMsg.pack();
			System.out.println(String.format(" Message %s", isoMsg.toString()));
			System.out.println(String.format("Sending Message Bytes %s", isoMsgBytes));

		outputStream.write(isoMsgBytes);
		outputStream.flush();

		byte[] responseBytes = new byte[4096];
		int bytesRead = inputStream.read(responseBytes);
			System.out.println(String.format("Received Message Bytes %s", bytesRead));

		ISOMsg responseMsg = new ISOMsg();
		responseMsg.setPackager(packager);
		responseMsg.unpack(responseBytes);
			System.out.println(String.format("Received Message  %s", responseMsg));

		// Process the response message
		System.out.println("Response MTI: " + responseMsg.getMTI());
		System.out.println("Response Header: " + responseMsg.getISOHeader());

			// ... process other fields as needed

		socket.close();
		} catch (IOException | ISOException e) {
			e.printStackTrace();
		}
	}


}
