package query;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import model.Move;
import model.MovesSet;
import reader.CSVLogReader;

public class TitleChangeApp {

	private String logFileName;

	private MovesSet movesSet;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");

	public static void main(String[] args) throws ParseException, FileNotFoundException {
		String logFileName = args[0];

		TitleChangeApp titleChangeApp = new TitleChangeApp(logFileName);
		titleChangeApp.showExamples();
	}

	public TitleChangeApp(String logFileName) {
		this.logFileName = logFileName;
	}

	private void init() {
		CSVLogReader reader = new CSVLogReader(this.logFileName);
		this.movesSet = reader.run();
	}

	private void showExamples() {

		init();

		try {
			System.out.println("=> " + search(new TitleChangeQuery("Earth (band)",
					dateFormat.parse("2006-01-01T02:35:20Z"), dateFormat.parse("2016-12-12T12:00:00")), movesSet));

			System.out.println("");

			System.out.println("=> " + search(new TitleChangeQuery("James Stewart",
					dateFormat.parse("2006-01-01T02:35:20Z"), dateFormat.parse("2016-12-12T12:00:00")), movesSet));

			System.out.println("");

			System.out.println("=> " + search(new TitleChangeQuery("James Stewart",
					dateFormat.parse("2010-12-12T12:00:00"), dateFormat.parse("2007-01-01T02:35:20Z")), movesSet));

			System.out.println("");

			System.out.println("=> " + search(new TitleChangeQuery("James Stewart (actor)",
					dateFormat.parse("2007-01-01T02:35:20Z"), dateFormat.parse("2010-12-12T12:00:00")), movesSet));

			System.out.println("");

			System.out
					.println("=> " + search(
							new TitleChangeQuery("James Stewart (disambiguation)",
									dateFormat.parse("2010-12-12T02:35:20Z"), dateFormat.parse("2007-12-12T12:00:00")),
							movesSet));

			System.out.println("");

			System.out
					.println("=> " + search(
							new TitleChangeQuery("James Stewart (disambiguation)",
									dateFormat.parse("2007-12-12T02:35:20Z"), dateFormat.parse("2010-12-12T12:00:00")),
							movesSet));

			System.out.println("");

			System.out.println("=> " + search(new TitleChangeQuery("James Stewart",
					dateFormat.parse("2007-01-01T02:35:20Z"), dateFormat.parse("2010-12-12T12:00:00")), movesSet));

			System.out.println("");

			System.out
					.println("=> " + search(
							new TitleChangeQuery("Camilla, Duchess of Cornwall",
									dateFormat.parse("2017-01-01T02:35:20Z"), dateFormat.parse("2008-12-12T12:00:00")),
							movesSet));

			System.out.println("");

			System.out
					.println("=> " + search(
							new TitleChangeQuery("Catherine, Duchess of Cambridge",
									dateFormat.parse("2017-01-01T02:35:20Z"), dateFormat.parse("2008-12-12T12:00:00")),
							movesSet));

			System.out.println("");

			System.out.println("=> " + search(new TitleChangeQuery("Gabriela Soukalová",
					dateFormat.parse("2014-01-01T02:35:20Z"), dateFormat.parse("2017-01-01T12:00:00")), movesSet));
			System.out.println("");

			System.out.println("");

			System.out.println("=> " + search(new TitleChangeQuery("Gabriela Koukalová",
					dateFormat.parse("2017-01-01T02:35:20Z"), dateFormat.parse("2014-01-01T12:00:00")), movesSet));
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static String search(TitleChangeQuery query, MovesSet movesSet) {

		System.out.println("Query: \"" + query.getCurrentName() + "\" in " + query.getCurrentDate() + " -> "
				+ query.getTargetDate() + "?");

		if (query.getTargetDate().before(query.getCurrentDate())) {
			return searchBack(query, movesSet);
		} else if (query.getTargetDate().after(query.getCurrentDate())) {
			return searchForward(query, movesSet);
		} else {
			return query.getCurrentName();
		}

	}

	private static String searchBack(TitleChangeQuery query, MovesSet movesSet) {

		Move goal = null;
		// search the last entry with query name as target before query's time
		for (Move move : movesSet.getMovesByTarget(query.getCurrentName())) {
			if (move.getTimeStamp().after(query.getCurrentDate())) {
				break;
			}
			goal = move;
		}

		if (goal == null)
			return query.getCurrentName();

		String result = null;

		Date targetDate = query.getTargetDate();

		List<String> names = new ArrayList<String>();
		names.add(query.getCurrentName());

		while (goal != null && goal.getTimeStamp().after(targetDate)) {
			result = goal.getSource();
			names.add(result + " - " + dateFormatYear.format(goal.getTimeStamp()));
			goal = findPreviousMove(targetDate, goal, movesSet);
		}

		System.out.println("\t" + StringUtils.join(names, " <- "));

		if (result == null)
			return query.getCurrentName();

		return result;
	}

	private static Move findPreviousMove(Date targetDate, Move move, MovesSet movesSet) {

		String target = move.getSource();

		Move goal = null;

		if (movesSet.getMovesByTarget(target) == null) {
			return null;
		}

		// search the last entry with query name as target before query's time
		for (Move move2 : movesSet.getMovesByTarget(target)) {
			if (move2.getTimeStamp().before(targetDate)) {
				break;
			}
			if (move2.getTimeStamp().after(move.getTimeStamp())) {
				break;
			}
			goal = move2;
		}

		return goal;
	}

	private static String searchForward(TitleChangeQuery query, MovesSet movesSet) {

		Move goal = null;
		// search the last entry with query name as target before query's time
		for (Move move : movesSet.getMovesBySource(query.getCurrentName())) {
			goal = move;
			if (move.getTimeStamp().after(query.getCurrentDate())) {
				break;
			}
		}

		if (goal == null)
			return query.getCurrentName();

		Date targetDate = query.getTargetDate();

		String result = null;

		List<String> names = new ArrayList<String>();
		names.add(query.getCurrentName());
		while (goal != null && goal.getTimeStamp().before(targetDate)) {
			result = goal.getTarget();
			names.add(result + " - " + dateFormatYear.format(goal.getTimeStamp()));
			goal = findNextMove(targetDate, goal, movesSet);
		}

		System.out.println("\t" + StringUtils.join(names, " -> "));

		return result;
	}

	private static Move findNextMove(Date targetDate, Move move, MovesSet movesSet) {

		String target = move.getTarget();

		Move goal = null;

		if (movesSet.getMovesBySource(target) == null) {
			return null;
		}

		for (Move move2 : movesSet.getMovesBySource(target)) {
			if (move2.getTimeStamp().after(targetDate)) {
				break;
			}
			if (move2.getTimeStamp().before(move.getTimeStamp())) {
				continue;
			}
			goal = move2;
			break;
		}

		return goal;
	}

}
