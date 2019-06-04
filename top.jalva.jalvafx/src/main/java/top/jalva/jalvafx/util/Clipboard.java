package top.jalva.jalvafx.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;

public class Clipboard {

	public static void set(String str) {
		StringSelection ss = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}

	public static void setEmptyString() {
		StringSelection ss = new StringSelection("");
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}

	public static Optional<String> getTextContent() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

		String data = null;
		try {
			data = (String) t.getTransferData(DataFlavor.stringFlavor);
			if (StringUtils.isBlank(data))
				data = null;
		} catch (UnsupportedFlavorException | IOException e1) {
			e1.printStackTrace();
		}
		return Optional.ofNullable(data);
	}
}
