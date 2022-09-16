package com.wiiudev.homebrew.graphical_interface;

import com.wiiudev.homebrew.graphical_interface.utilities.FileWatcher;
import lombok.val;

import javax.swing.*;

public class AppSenderFileWatcher extends FileWatcher
{
	public AppSenderFileWatcher(String watchFile)
	{
		super(watchFile);
	}

	@Override
	public void onModified()
	{
		SwingUtilities.invokeLater(() ->
		{
			val appSenderGUI = AppSenderGUI.getInstance();
			appSenderGUI.sendApp(false);
		});
	}
}
