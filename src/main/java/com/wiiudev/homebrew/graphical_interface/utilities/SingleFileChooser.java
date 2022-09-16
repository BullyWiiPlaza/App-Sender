package com.wiiudev.homebrew.graphical_interface.utilities;

import lombok.val;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.File;
import java.nio.file.Files;

public class SingleFileChooser extends JFileChooser
{
	private final JTextComponent pathTextComponent;

	public SingleFileChooser(JTextComponent pathTextComponent)
	{
		this.pathTextComponent = pathTextComponent;

		val currentFilePath = pathTextComponent.getText();
		val currentFile = new File(currentFilePath);

		if (Files.isRegularFile(currentFile.toPath()))
		{
			setCurrentDirectory(currentFile);
		} else
		{
			val workingDirectory = System.getProperty("user.dir");
			val workingDirectoryFile = new File(workingDirectory);
			setCurrentDirectory(workingDirectoryFile);
		}

		setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	public void allowFileSelection(JRootPane rootPane)
	{
		val selectedAnswer = showOpenDialog(rootPane);
		if (selectedAnswer == JFileChooser.APPROVE_OPTION)
		{
			val selectedFile = getSelectedFile().getAbsolutePath();
			pathTextComponent.setText(selectedFile);
		}
	}
}
