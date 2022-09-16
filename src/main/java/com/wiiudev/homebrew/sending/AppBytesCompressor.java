package com.wiiudev.homebrew.sending;

import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;

import static java.nio.file.Files.createTempFile;

public class AppBytesCompressor
{
	/**
	 * Compresses a file using the deflate algorithm
	 *
	 * @param rawFile A raw file from the file system
	 */
	public static Path compress(final Path rawFile) throws IOException
	{
		// https://stackoverflow.com/a/9806694/3764804
		val compressedFile = createTempFile("Compressed-ELF", ".zz");

		try (val compressedWriter = new DeflaterOutputStream(Files.newOutputStream(compressedFile)))
		{
			Files.copy(rawFile, compressedWriter);
		}

		return compressedFile;
	}
}
