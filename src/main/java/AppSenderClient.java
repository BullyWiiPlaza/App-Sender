import com.wiiudev.homebrew.graphical_interface.AppSenderGUI;
import com.wiiudev.homebrew.sending.AppTransmission;
import lombok.val;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.wiiudev.homebrew.graphical_interface.utilities.IPAddressValidator.validateIPv4Address;

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

	private static void startGUI() throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() ->
		{
			val appSenderGUI = AppSenderGUI.getInstance();
			appSenderGUI.setVisible(true);
		});
	}

	private static void useCommandLine(final String[] consoleArguments) throws Exception
	{
		val ipAddress = consoleArguments[1];
		val isValidIPAddress = validateIPv4Address(ipAddress);

		if (!isValidIPAddress)
		{
			System.err.println("Invalid IP address: " + ipAddress);
			System.exit(1);
		}

		val sourceFile = consoleArguments[0];
		val sourceFilePath = Paths.get(sourceFile);
		val isValidSourceFile = Files.isRegularFile(sourceFilePath);

		if (!isValidSourceFile)
		{
			System.err.println("Invalid file path: \"" + sourceFile + "\"");
			System.exit(1);
		}

		AppTransmission.send(sourceFile, ipAddress);
	}
}
