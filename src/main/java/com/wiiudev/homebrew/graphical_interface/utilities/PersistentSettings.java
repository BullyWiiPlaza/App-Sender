package com.wiiudev.homebrew.graphical_interface.utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PersistentSettings
{
	private final Properties properties;
	private final String propertiesFileName;

	public PersistentSettings()
	{
		propertiesFileName = "config.properties";
		properties = new Properties();

		try
		{
			if (new File(propertiesFileName).exists())
			{
				try (InputStream propertiesReader = Files.newInputStream(Paths.get(propertiesFileName)))
				{
					properties.load(propertiesReader);
				}
			}
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public void put(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public void writeToFile()
	{
		try
		{
			try (OutputStream propertiesWriter = Files.newOutputStream(Paths.get(propertiesFileName)))
			{
				properties.store(propertiesWriter, null);
			}
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public String get(String key)
	{
		return (String) properties.get(key);
	}
}
