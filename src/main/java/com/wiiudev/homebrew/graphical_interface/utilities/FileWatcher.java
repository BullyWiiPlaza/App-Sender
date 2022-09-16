package com.wiiudev.homebrew.graphical_interface.utilities;

import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class FileWatcher
{
	private final Path folderPath;
	private final String watchFile;
	private WatchService service;

	public FileWatcher(String watchFile)
	{
		val filePath = Paths.get(watchFile);
		val isRegularFile = Files.isRegularFile(filePath);

		if (!isRegularFile)
		{
			// Do not allow this to be a folder since we want to watch files
			throw new IllegalArgumentException(watchFile + " is not a regular file");
		}

		// This is always a folder
		folderPath = filePath.getParent();

		// Keep this relative to the watched folder
		this.watchFile = watchFile.replace(folderPath.toString() + File.separator, "");
	}

	public void startWatching()
	{
		val watcherThread = new Thread(() ->
		{
			try
			{
				// We obtain the file system of the Path
				val fileSystem = folderPath.getFileSystem();
				service = fileSystem.newWatchService();

				// We watch for modification events
				folderPath.register(service, ENTRY_MODIFY);

				// Start the infinite polling loop
				while (true)
				{
					// Wait for the next event
					val watchKey = service.take();

					for (val watchEvent : watchKey.pollEvents())
					{
						// Get the type of the event
						val kind = watchEvent.kind();

						if (kind == ENTRY_MODIFY)
						{
							val watchEventPath = (Path) watchEvent.context();

							// Call this if the right file is involved
							if (watchEventPath.toString().equals(watchFile))
							{
								onModified();
							}
						}
					}

					if (!watchKey.reset())
					{
						// Exit if no longer valid
						break;
					}
				}
			} catch (final ClosedWatchServiceException ignored)
			{
				// This happens when the watch service has been closed so ignore
			} catch (final Exception exception)
			{
				exception.printStackTrace();
			}
		});

		watcherThread.start();
	}

	public void stopWatching()
	{
		try
		{
			service.close();
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public abstract void onModified();
}
