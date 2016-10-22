package com.wiiudev.homebrew.sending;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTransmission
{
	private Socket clientSocket;
	private DataOutputStream dataTransmitter;
	private static final int PORT = 4299;

	private AppTransmission(String ipAddress) throws IOException
	{
		clientSocket = new Socket(ipAddress, PORT);
		dataTransmitter = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
	}

	/**
	 * Sends and executes the compressed app file on a Wii console using the <code>clientSocket</code>.
	 */
	private void transmit(File rawFile) throws Exception
	{
		File compressedFile = AppBytesCompressor.compress(rawFile);

		dataTransmitter.writeBytes("HAXX");
		dataTransmitter.writeByte(0);
		dataTransmitter.writeByte(5);
		dataTransmitter.writeShort((short) (rawFile.getName().length()
				+ clientSocket.getInetAddress().toString().length() + 1
				+ rawFile.getCanonicalPath().length() + 1));
		dataTransmitter.writeInt((int) compressedFile.length());
		dataTransmitter.writeInt((int) rawFile.length());
		dataTransmitter.write(Files.readAllBytes(compressedFile.toPath()));
		dataTransmitter.writeBytes(rawFile.getName() + "\u0000");
		dataTransmitter.writeBytes(clientSocket.getInetAddress().toString()
				+ "\u0000");
		dataTransmitter.writeBytes(rawFile.getCanonicalPath() + "\u0000");

		// Force the request to be sent
		dataTransmitter.flush();

		// Clean up the compressed file
		Files.delete(compressedFile.toPath());
	}

	private void close() throws IOException
	{
		dataTransmitter.close();
		clientSocket.close();
	}

	public static void send(String sourceFile, String ipAddress) throws Exception
	{
		AppTransmission appTransmission = new AppTransmission(ipAddress);
		appTransmission.transmit(new File(sourceFile));
		appTransmission.close();
	}
}