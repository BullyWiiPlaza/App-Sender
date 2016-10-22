import com.wiiudev.homebrew.graphical_interface.utilities.IPAddressValidator;
import com.wiiudev.homebrew.graphical_interface.AppSenderGUI;
import com.wiiudev.homebrew.sending.AppTransmission;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppSenderClient
{
	public static void main(String[] consoleArguments) throws Exception
	{
		if (consoleArguments.length == 2)
		{
			useCommandLine(consoleArguments);
		} else
		{
			startGUI();
		}
	}

	private static void startGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() ->
		{
			AppSenderGUI appSenderGUI = AppSenderGUI.getInstance();
			appSenderGUI.setVisible(true);
		});
	}

	private static void useCommandLine(String[] consoleArguments) throws Exception
	{
		String ipAddress = consoleArguments[1];
		boolean isValidIPAddress = IPAddressValidator.validateIPv4Address(ipAddress);

		if (!isValidIPAddress)
		{
			System.err.println("Invalid IP address: " + ipAddress);
			System.exit(1);
		}

		String sourceFile = consoleArguments[0];
		Path sourceFilePath = Paths.get(sourceFile);
		boolean isValidSourceFile = Files.isRegularFile(sourceFilePath);

		if (!isValidSourceFile)
		{
			System.err.println("Invalid file path: \"" + sourceFile + "\"");
			System.exit(1);
		}

		AppTransmission.send(sourceFile, ipAddress);
	}
}