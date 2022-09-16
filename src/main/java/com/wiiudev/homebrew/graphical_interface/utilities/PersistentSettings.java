package com.wiiudev.homebrew.graphical_interface.utilities;

import lombok.val;

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
		} catch (final IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public void put(final String key, final String value)
	{
		properties.setProperty(key, value);
	}

	public void writeToFile()
	{
		try
		{
			try (val propertiesWriter = Files.newOutputStream(Paths.get(propertiesFileName)))
			{
				properties.store(propertiesWriter, null);
			}
		} catch (final IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public String get(final String key)
	{
		return (String) properties.get(key);
	}
}
