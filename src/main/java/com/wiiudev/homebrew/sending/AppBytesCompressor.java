package com.wiiudev.homebrew.sending;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.DeflaterOutputStream;

public class AppBytesCompressor
{
	/**
	 * Compresses a file using the deflate algorithm
	 *
	 * @param rawFile A raw file from the file system
	 */
	public static File compress(File rawFile) throws IOException
	{
		String compressedFilePath = FileNameUtilities.getRandomFilePath();
		File compressed = new File(compressedFilePath);

		try (OutputStream compressedWriter = new DeflaterOutputStream(new FileOutputStream(compressed)))
		{
			Files.copy(rawFile.toPath(), compressedWriter);
			compressedWriter.flush();
		}

		return compressed;
	}
}