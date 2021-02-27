package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovesSet {

	private List<Move> moves = new ArrayList<Move>();

	private Map<String, List<Move>> movesByTarget = new HashMap<String, List<Move>>();
	private Map<String, List<Move>> movesBySource = new HashMap<String, List<Move>>();

	public void addMove(Move move) {
		this.moves.add(move);
		if (!movesByTarget.containsKey(move.getTarget()))
			movesByTarget.put(move.getTarget(), new ArrayList<Move>());
		movesByTarget.get(move.getTarget()).add(move);
		if (!movesBySource.containsKey(move.getSource()))
			movesBySource.put(move.getSource(), new ArrayList<Move>());
		movesBySource.get(move.getSource()).add(move);
	}

	public List<Move> getMovesByTarget(String target) {
		return this.movesByTarget.get(target);
	}

	public List<Move> getMovesBySource(String source) {
		return this.movesBySource.get(source);
	}

}
