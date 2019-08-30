package top.jalva.jalvafx.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clipboard {
	
	private static final Logger log = LoggerFactory.getLogger(Clipboard.class.getName());

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
			log.error("Error happenned during clipboard content receiving ", e1);
		}
		return Optional.ofNullable(data);
	}
}
