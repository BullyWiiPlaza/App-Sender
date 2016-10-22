package com.wiiudev.homebrew.sending;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileNameUtilities
{
	private static final String FILENAME_EXTENSION_SEPARATOR = ".";

	public static String getFileName(String filePath)
	{
		return getBaseFileName(filePath) + FILENAME_EXTENSION_SEPARATOR + getFileExtension(filePath);
	}

	public static String appendToBaseFileName(String filePath, String addendum)
	{
		File file = new File(filePath);
		String fileName = file.getName();

		return file.getParent()  // Parent folder
				+ File.separator // Path separator
				+ FileNameUtilities.getBaseFileName(fileName) // Base file name
				+ addendum  // Addon
				+ FILENAME_EXTENSION_SEPARATOR // Extension dot
				+ FileNameUtilities.getFileExtension(fileName) // Extension
				;
	}

	public static String getBaseFileName(String filePath)
	{
		return getFileNameTokens(filePath)[0];
	}

	public static String getFileExtension(String filePath)
	{
		return getFileNameTokens(filePath)[1];
	}

	private static String[] getFileNameTokens(String filePath)
	{
		return filePath.split("\\.(?=[^.]+$)");
	}

	public static String getRandomFilePath()
	{
		while (true)
		{
			String downloadFileName = RandomStringUtils.getRandomString(20);
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
		 * @param charactersCount The desired String length
		 * @return The generated String
		 */
		static String getRandomString(int charactersCount)
		{
			byte random[] = new byte[charactersCount];
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