package reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import model.LogType;

public class XMLLogReader {

	private PrintWriter resultWriter = null;

	public static void main(String[] args) throws FileNotFoundException {

		String wikipediaLogFileName = args[0];
		String outputFileName = args[1];

		XMLLogReader reader = new XMLLogReader();
		reader.run(wikipediaLogFileName, outputFileName);
	}

	private void run(String wikipediaLogFileName, String outputFileName) throws FileNotFoundException {

		BufferedReader br = new BufferedReader(new FileReader(wikipediaLogFileName));

		try {
			resultWriter = new PrintWriter(outputFileName);

			String line;
			String usedLine = null;
			String logItem = "";

			boolean inLogItem = false;
			boolean inLine = false;

			try {
				while ((line = br.readLine()) != null) {

					if (inLine)
						usedLine += line.trim();
					else
						usedLine = line.trim();

					if (!line.endsWith(">")) {
						inLine = true;
						continue;
					} else {
						inLine = false;
					}

					if (!inLogItem && usedLine.equals("<logitem>")) {
						inLogItem = true;
					}

					if (inLogItem)
						logItem += usedLine + "\n";

					if (inLogItem && usedLine.equals("<logitem>")) {
						// when record ends early and next <logitem> is already
						// found
						processLogItem(logItem);
						logItem = "";
						inLogItem = true;
					}

					if (usedLine.equals("</logitem>")) {
						if (inLogItem)
							processLogItem(logItem);
						logItem = "";
						inLogItem = false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			resultWriter.close();
		}

	}

	private void processLogItem(String logItem) {

		if (logItem.split("\n").length < 12)
			return;

		LogType type = null;
		for (String line : logItem.split("\n")) {
			if (line.trim().equals("<type>delete</type>")) {
				type = LogType.DELETE;
				break;
			} else if (line.trim().equals("<type>move</type>")) {
				type = LogType.MOVE;
				break;
			}
		}
		if (type == null)
			return;

		if (type == LogType.DELETE)
			processDeleteLogItem(logItem);
		else
			processMoveLogItem(logItem);

	}

	private void processMoveLogItem(String logItem) {

		Integer id = null;
		String timeStampString = null;
		String source = null;
		String target = null;

		boolean inUser = false;

		for (String line : logItem.split("\n")) {

			if (line.equals("</contributor>")) {
				inUser = false;
			}

			if (inUser)
				continue;

			if (line.equals("<contributor>")) {
				inUser = true;
				continue;
			}

			if (line.startsWith("<id>")) {
				id = Integer.valueOf(line.substring(4, line.lastIndexOf("<")));
			} else if (line.startsWith("<timestamp>")) {
				timeStampString = line.substring(11, line.lastIndexOf("<"));
			} else if (line.startsWith("<logtitle>")) {
				source = line.substring(10, line.lastIndexOf("<"));
				source = StringEscapeUtils.unescapeHtml4(source);
			} else if (line.startsWith("<params xml:space")) {

				try {
					target = line.substring(29, line.lastIndexOf("<"));
				} catch (StringIndexOutOfBoundsException e) {
					return;
				}

				if (target.contains("target&quot;")) {
					target = target.substring(target.indexOf("target&quot;;") + "target&quot;;".length());
					target = target.substring(target.indexOf("&quot;") + "&quot;".length());
					target = target.substring(0, target.indexOf("&quot;"));
				}

				target = StringEscapeUtils.unescapeHtml4(target);
			}
		}

		if (id == null || source == null || target == null || timeStampString == null)
			return;

		timeStampString = timeStampString.replaceAll("\t", "   ");
		source = source.replaceAll("\t", "   ");
		target = target.replaceAll("\t", "   ");

		if (source.startsWith("User:") || source.startsWith("Talk:") || source.startsWith("Template:")
				|| source.startsWith("File:") || source.startsWith("Draft:") || source.startsWith("Portal:")
				|| source.startsWith("MediaWiki:") || source.startsWith("User talk:")
				|| source.startsWith("Category talk:"))
			return;

		resultWriter.write(LogType.MOVE + "\t" + source + "\t" + target + "\t" + timeStampString + "\n");
	}

	private void processDeleteLogItem(String logItem) {

		String timeStampString = null;
		String source = null;

		for (String line : logItem.split("\n")) {
			if (line.startsWith("<timestamp>")) {
				timeStampString = line.substring(11, line.lastIndexOf("<"));
			} else if (line.startsWith("<logtitle>")) {
				source = line.substring(10, line.lastIndexOf("<"));
			}
		}

		if (timeStampString == null || source == null)
			return;

		if (source.startsWith("User:") || source.startsWith("Talk:") || source.startsWith("Template:")
				|| source.startsWith("File:") || source.startsWith("Draft:") || source.startsWith("Portal:")
				|| source.startsWith("MediaWiki:") || source.startsWith("User talk:")
				|| source.startsWith("Category talk:"))
			return;

		resultWriter.write(LogType.DELETE + "\t" + source + "\t" + timeStampString + "\n");
	}

}
