package com.wiiudev.homebrew.graphical_interface;

import com.wiiudev.homebrew.graphical_interface.utilities.FileWatcher;
import com.wiiudev.homebrew.graphical_interface.utilities.IPAddressValidator;
import com.wiiudev.homebrew.graphical_interface.utilities.PersistentSettings;
import com.wiiudev.homebrew.graphical_interface.utilities.SingleFileChooser;
import com.wiiudev.homebrew.sending.AppTransmission;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppSenderGUI extends JFrame
{
	private JPanel rootPane;
	private JTextField appFilePathField;
	private JButton browseAppFileButton;
	private JTextField ipAddressField;
	private JButton sendAppButton;
	private JButton creditsButton;
	private JButton informationButton;
	private JCheckBox automaticallySendCheckBox;

	private PersistentSettings persistentSettings;
	private boolean sendingApp;
	private FileWatcher fileWatcher;

	private static AppSenderGUI instance;

	private AppSenderGUI()
	{
		setFrameProperties();
		addAppFileDocumentListener();
		addIPAddressValidation();
		addBrowseAppFileButtonListener();
		sendAppButton.addActionListener(actionEvent -> sendApp());
		addCreditsButtonListener();
		addInformationButtonListener();
		automaticallySendCheckBox.addItemListener(itemEvent -> considerRunningAppFileWatcher());
		setSendAppButtonAvailability();
		startAppFilePathValidationAsynchronously();
		restorePersistentSettings();
		addSettingsBackupShutdownHook();
	}

	private void addSettingsBackupShutdownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			persistentSettings.put("IP_ADDRESS", ipAddressField.getText());
			persistentSettings.put("APP_FILE_PATH", appFilePathField.getText());
			persistentSettings.put("AUTOMATICALLY_SEND", String.valueOf(automaticallySendCheckBox.isSelected()));
			persistentSettings.writeToFile();
		}));
	}

	private void restorePersistentSettings()
	{
		persistentSettings = new PersistentSettings();

		String ipAddress = persistentSettings.get("IP_ADDRESS");
		if (ipAddress != null)
		{
			ipAddressField.setText(ipAddress);
		}

		String appFilePath = persistentSettings.get("APP_FILE_PATH");
		if (appFilePath != null)
		{
			appFilePathField.setText(appFilePath);
		}

		String automaticallySend = persistentSettings.get("AUTOMATICALLY_SEND");
		if(automaticallySend != null)
		{
			automaticallySendCheckBox.setSelected(Boolean.parseBoolean(automaticallySend));
		}
	}

	private void addAppFileDocumentListener()
	{
		appFilePathField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				considerRunningAppFileWatcher();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				considerRunningAppFileWatcher();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				considerRunningAppFileWatcher();
			}
		});
	}

	private void considerRunningAppFileWatcher()
	{
		if (automaticallySendCheckBox.isSelected())
		{
			startAppFileWatcher();
		} else
		{
			stopAppFileWatcher();
		}
	}

	private void startAppFileWatcher()
	{
		stopAppFileWatcher();

		String appFilePath = appFilePathField.getText();
		fileWatcher = new AppSenderFileWatcher(appFilePath);
		fileWatcher.startWatching();
	}

	private void stopAppFileWatcher()
	{
		if (fileWatcher != null)
		{
			fileWatcher.stopWatching();
		}
	}

	private void startAppFilePathValidationAsynchronously()
	{
		Thread appFilePathValidator = new Thread(() ->
		{
			while (true)
			{
				validateAppFilePath();

				try
				{
					Thread.sleep(50);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
					break;
				}
			}
		});

		appFilePathValidator.start();
	}

	private void addCreditsButtonListener()
	{
		creditsButton.addActionListener(actionEvent ->
				JOptionPane.showMessageDialog(rootPane,
						"App Sender by BullyWiiPlaza\nSpecial thanks to VGMoose for JWiiLoad",
						creditsButton.getText(),
						JOptionPane.INFORMATION_MESSAGE,
						null));
	}

	private void addInformationButtonListener()
	{
		informationButton.addActionListener(actionEvent ->
				JOptionPane.showMessageDialog(rootPane,
						"This application let's you send apps from your computer to your Wii or Wii U and automatically start them!" +
								"\nYou need to be in the Homebrew Channel or Homebrew Launcher respectively for this to work.",
						informationButton.getText(),
						JOptionPane.INFORMATION_MESSAGE,
						null));
	}

	private void setFrameProperties()
	{
		add(rootPane);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("App Sender");
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage(AppSenderGUI.class.getResource("/Icon.jpg")));
		pack();
	}

	void sendApp()
	{
		String sendAppButtonText = sendAppButton.getText();
		sendAppButton.setText("Sending...");
		sendingApp = true;
		setSendAppButtonAvailability();
		String ipAddress = ipAddressField.getText();
		String appFilePath = appFilePathField.getText();

		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				try
				{
					AppTransmission.send(appFilePath, ipAddress);
				} catch (Exception exception)
				{
					exception.printStackTrace();
				}

				return null;
			}

			@Override
			protected void done()
			{
				sendingApp = false;
				sendAppButton.setText(sendAppButtonText);
				setSendAppButtonAvailability();
			}
		}.execute();
	}

	private void setSendAppButtonAvailability()
	{
		boolean canSend = isAppFilePathValid()
				&& isEnteredIPAddressValid()
				&& !sendingApp;
		sendAppButton.setEnabled(canSend);
		automaticallySendCheckBox.setEnabled(isAppFilePathValid());
	}

	private void addBrowseAppFileButtonListener()
	{
		browseAppFileButton.addActionListener(actionEvent ->
		{
			SingleFileChooser singleFileChooser = new SingleFileChooser(appFilePathField);
			singleFileChooser.allowFileSelection(getRootPane());
		});
	}

	private void validateAppFilePath()
	{
		boolean exists = isAppFilePathValid();
		appFilePathField.setBackground(exists ? Color.GREEN : Color.RED);
		setSendAppButtonAvailability();
	}

	private boolean isAppFilePathValid()
	{
		String appFilePath = appFilePathField.getText();
		Path filePath = Paths.get(appFilePath);

		return Files.isRegularFile(filePath);
	}

	private void addIPAddressValidation()
	{
		ipAddressField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				validateIPAddressField();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				validateIPAddressField();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				validateIPAddressField();
			}
		});

		validateIPAddressField();
	}

	private void validateIPAddressField()
	{
		boolean isValid = isEnteredIPAddressValid();
		ipAddressField.setBackground(isValid ? Color.GREEN : Color.RED);
		setSendAppButtonAvailability();
	}

	private boolean isEnteredIPAddressValid()
	{
		String ipAddress = ipAddressField.getText();
		return IPAddressValidator.validateIPv4Address(ipAddress);
	}

	public static AppSenderGUI getInstance()
	{
		if (instance == null)
		{
			instance = new AppSenderGUI();
		}

		return instance;
	}
}