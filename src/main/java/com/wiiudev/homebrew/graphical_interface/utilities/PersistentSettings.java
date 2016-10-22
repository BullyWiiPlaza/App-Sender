package com.wiiudev.homebrew.graphical_interface.utilities;

import java.io.*;
import java.util.Properties;

public class PersistentSettings
{
	private Properties properties;
	private String propertiesFileName;

	public PersistentSettings()
	{
		propertiesFileName = "config.properties";
		properties = new Properties();

		try
		{
			if (new File(propertiesFileName).exists())
			{
				InputStream propertiesReader = new FileInputStream(propertiesFileName);
				properties.load(propertiesReader);
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
			OutputStream propertiesWriter = new FileOutputStream(propertiesFileName);
			properties.store(propertiesWriter, null);
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