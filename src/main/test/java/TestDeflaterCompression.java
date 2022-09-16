import com.wiiudev.homebrew.sending.AppBytesCompressor;
import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.nio.file.Files.createTempFile;
import static org.junit.Assert.assertTrue;

public class TestDeflaterCompression
{
	@Test
	public void testDeflater() throws IOException
	{
		val temporaryFile = createTempFile("prefix", "suffix");
		try
		{
			Files.write(temporaryFile, "Hello, world!".getBytes(StandardCharsets.UTF_8));
			val compressedFilePath = AppBytesCompressor.compress(temporaryFile);
			assertTrue(Files.size(compressedFilePath) != Files.size(temporaryFile));
		} finally
		{
			Files.delete(temporaryFile);
		}
	}
}
