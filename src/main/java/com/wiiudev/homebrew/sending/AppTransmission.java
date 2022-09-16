package com.wiiudev.homebrew.sending;

import lombok.val;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTransmission implements Closeable
{
	private final Socket clientSocket;
	private final DataOutputStream dataTransmitter;
	private static final int PORT = 4299;

	private AppTransmission(final String ipAddress) throws IOException
	{
		clientSocket = new Socket(ipAddress, PORT);
		dataTransmitter = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
	}

	/**
	 * Sends and executes the compressed app file on a Wii console using the <code>clientSocket</code>.
	 */
	private void transmit(final Path rawFile) throws Exception
	{
		val compressedFile = AppBytesCompressor.compress(rawFile);

		try
		{
			dataTransmitter.writeBytes("HAXX");
			dataTransmitter.writeByte(0);
			dataTransmitter.writeByte(5);
			dataTransmitter.writeShort((short) (rawFile.getFileName().toString().length()
					+ clientSocket.getInetAddress().toString().length() + 1
					+ rawFile.toString().length() + 1));
			dataTransmitter.writeInt((int) Files.size(compressedFile));
			dataTransmitter.writeInt((int) Files.size(rawFile));
			dataTransmitter.write(Files.readAllBytes(compressedFile));
			dataTransmitter.writeBytes(rawFile.getFileName().toString() + "\u0000");
			dataTransmitter.writeBytes(clientSocket.getInetAddress().toString()
					+ "\u0000");
			dataTransmitter.writeBytes(rawFile + "\u0000");

			// Force the request to be sent
			dataTransmitter.flush();
		} finally
		{
			// Clean up the compressed file
			Files.delete(compressedFile);
		}
	}

	@Override
	public void close() throws IOException
	{
		dataTransmitter.close();
		clientSocket.close();
	}

	public static void send(final String sourceFile, final String ipAddress) throws Exception
	{
		try (val appTransmission = new AppTransmission(ipAddress))
		{
			appTransmission.transmit(Paths.get(sourceFile));
		}
	}
}
