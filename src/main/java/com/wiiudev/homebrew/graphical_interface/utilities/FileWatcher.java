package com.wiiudev.homebrew.graphical_interface.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class FileWatcher
{
	private final Path folderPath;
	private final String watchFile;
	private WatchService service;

	public FileWatcher(String watchFile)
	{
		Path filePath = Paths.get(watchFile);

		boolean isRegularFile = Files.isRegularFile(filePath);

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
		Thread watcherThread = new Thread(() ->
		{
			try
			{
				// We obtain the file system of the Path
				FileSystem fileSystem = folderPath.getFileSystem();
				service = fileSystem.newWatchService();

				// We watch for modification events
				folderPath.register(service, ENTRY_MODIFY);

				// Start the infinite polling loop
				while (true)
				{
					// Wait for the next event
					WatchKey watchKey = service.take();

					for (WatchEvent<?> watchEvent : watchKey.pollEvents())
					{
						// Get the type of the event
						Kind<?> kind = watchEvent.kind();

						if (kind == ENTRY_MODIFY)
						{
							Path watchEventPath = (Path) watchEvent.context();

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
			} catch (ClosedWatchServiceException ignored)
			{
				// This happens when the watch service has been closed so ignore
			} catch (Exception exception)
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