package com.wiiudev.homebrew.graphical_interface.utilities;

import lombok.val;

import java.util.regex.Pattern;

public class IPAddressValidator
{
	public static boolean validateIPv4Address(final String ipAddress)
	{
		val pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		return pattern.matcher(ipAddress).matches();
	}
}
