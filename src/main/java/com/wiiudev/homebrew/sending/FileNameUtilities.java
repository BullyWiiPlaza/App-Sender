package com.wiiudev.homebrew.sending;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileNameUtilities
{
	public static String getRandomFilePath()
	{
		while (true)
		{
			String downloadFileName = RandomStringUtils.getRandomString();
			String targetDirectory = System.getProperty("java.io.tmpdir");
			Path downloadedFilePath = Paths.get(targetDirectory + downloadFileName);

			// Make sure the file name isn't taken (for the unlikely case)
			if (!Files.exists(downloadedFilePath))
			{
				return downloadedFilePath.toString();
			}
		}
	}

	private static class RandomStringUtils
	{
		/**
		 * Generates a random String
		 *
		 * @return The generated String
		 */
		static String getRandomString()
		{
			byte[] random = new byte[20];
			Random randomGenerator = new Random();
			StringBuilder buffer = new StringBuilder();

			randomGenerator.nextBytes(random);

			for (byte aRandom : random)
			{
				byte b1 = (byte) ((aRandom & 0xf0) >> 4);
				byte b2 = (byte) (aRandom & 0x0f);
				if (b1 < 10)
				{
					buffer.append((char) ('0' + b1));
				} else
				{
					buffer.append((char) ('A' + (b1 - 10)));
				}
				if (b2 < 10)
				{
					buffer.append((char) ('0' + b2));
				} else
				{
					buffer.append((char) ('A' + (b2 - 10)));
				}
			}

			return (buffer.toString());
		}
	}
}