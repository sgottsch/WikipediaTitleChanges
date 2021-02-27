package reader;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import model.LogType;
import model.Move;
import model.MovesSet;

public class CSVLogReader {

	private String logFile;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private MovesSet movesSet = new MovesSet();

	public CSVLogReader(String logFile) {
		super();
		this.logFile = logFile;
	}

	public MovesSet run() {

		System.out.println("Read log from " + this.logFile + ".");

		LineIterator it;
		try {
			it = FileUtils.lineIterator(new File(this.logFile), "UTF-8");
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					processLine(line);
				}
			} finally {
				it.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return this.movesSet;
	}

	private void processLine(String line) {

		String[] parts = line.split("\t");

		if (parts[0].equals(LogType.MOVE.toString())) {
			processMoveLine(parts);
		} else {
			// processDeleteLine(parts);
		}

	}

	private void processMoveLine(String[] parts) {
		String source = parts[1];
		String target = parts[2];
		String timeStampString = parts[3];

		Date timeStamp = transformTime(timeStampString);

		if (timeStamp != null) {
			Move move = new Move(source, target, timeStamp);
			movesSet.addMove(move);
		}
	}

	// private void processDeleteLine(String[] parts) {
	// String source = parts[1];
	// String timeStampString = parts[2];
	//
	// Date timeStamp = transformTime(timeStampString);
	//
	// if (timeStamp != null) {
	// Deletion deletion = new Deletion(source, timeStamp);
	// }
	// }

	private Date transformTime(String timeStampString) {
		try {
			return dateFormat.parse(timeStampString.replaceAll("Z$", "+0000"));
		} catch (ParseException e) {
			System.out.println("Wrong date: " + timeStampString);
			return null;
		}
	}

}