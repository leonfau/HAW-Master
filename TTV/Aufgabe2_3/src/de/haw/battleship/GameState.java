/**
 * Technik & Technologie vernetzter Systeme
 * Teil 2: P2P-Kommunikation: Chord mit Broadcast (3. & 4. Praktikum)
 * Projekt: Implementierung eines verteilten Spiels "Schiffe Versenken" (ohne Churn).
 * 
 * @author Erwin Lang, Leon Fausten
 *
 */
package de.haw.battleship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniba.wiai.lspi.chord.data.ID;

public class GameState {
	/**
	 * Map with all ids and state
	 */
	private Map<ID, FieldState> boardState;
	/**
	 * Map with max id (first) and min id (second)
	 */
	private Map<ID, ID> player;

	private ID myPlayerMin;
	private ID myPlayerMax;
	private int I;
	private int S;

	public GameState(ID myPlayerMin, ID myPlayerMax, int I, int S) {
		this.myPlayerMax = myPlayerMax;
		this.myPlayerMin = myPlayerMin;
		boardState = new HashMap<ID, FieldState>();
		player = new HashMap<ID, ID>();
		player.put(myPlayerMax, myPlayerMin);
		this.I = I;
		this.S = S;

	}

	/**
	 * Returns own player min id
	 */
	public ID getMyPlayerMin() {
		return myPlayerMin;
	}

	/**
	 * Returns own player max id
	 */
	public ID getMyPlayerMax() {
		return myPlayerMax;
	}

	/**
	 * Return opponents map
	 */
	public List<ID> getOpponents() {
		List<ID> opponents = new ArrayList<ID>(player.keySet());
		opponents.remove(this.getMyPlayerMax());
		return opponents;
	}

	/**
	 * Find min id for given player
	 * 
	 * @param maxID
	 * @return minID
	 */
	public ID getPlayerMinID(ID maxID) {
		return player.get(maxID);
	}

	/**
	 * Add new player and update min ranges if necessary
	 * 
	 * @param playerMin
	 * @param playerMax
	 */
	public void addPlayer(ID playerMin, ID playerMax) {
		// update existing player
		ID[] oldRange = this.findPlayerForIDinRange(playerMax);
		// existing player must be updated
		if (oldRange != null) {
			// Max ID of existing player is bigger or equal then new player
			if (IdMath.isInIntervallInkulsive(playerMax, oldRange[0],
					oldRange[1])) {
				// Min ID of existing node vary
				player.put(oldRange[1], IdMath.addOneToID(playerMax));

				// Min ID of existing player is smaller then new player --> min
				// range of new player must be resized
				if (IdMath.idCompare(oldRange[1], playerMin) <= 0) {
					playerMin = oldRange[0];
				}
			} else if (IdMath.isInIntervallInkulsive(playerMin, oldRange[0],
					oldRange[1])) {
				// only min new min range is in existing range --> min range of
				// new player must be resized
				playerMin = IdMath.addOneToID(oldRange[1]);
			}
		}
		this.player.put(playerMax, playerMin);
	}

	/**
	 * find possible minID for new player
	 * 
	 * @param maxID
	 * @return
	 */
	public ID findPossibleMinId(ID maxID) {
		BigInteger max = maxID.toBigInteger();
		BigInteger newMin = BigInteger.ZERO;
		BigInteger firstFieldStart = BigInteger.ZERO;

		for (Entry<ID, ID> entry : player.entrySet()) {
			if (firstFieldStart.equals(BigInteger.ZERO)
					|| firstFieldStart.compareTo(entry.getKey().toBigInteger()) < 0) {
				firstFieldStart = firstFieldStart.max(entry.getKey()
						.toBigInteger());
			}

			BigInteger entryInt = entry.getKey().toBigInteger();
			if (entryInt.compareTo(max) < 0 && entryInt.compareTo(newMin) > 0) {
				newMin = IdMath.addOneToID(entry.getValue()).toBigInteger();
			}
		}

		// Range in circle over Zero boarder
		if (newMin.equals(BigInteger.ZERO)) {
			newMin = firstFieldStart.subtract(BigInteger.ONE);
		}
		return new ID(newMin.toByteArray());
	}

	/**
	 * add player if not exists
	 * 
	 * @param playerMin
	 * @param playerMax
	 */
	public void addPlayerIfNotExists(ID playerMin, ID playerMax) {
		if (this.findPlayerForID(playerMax) == null) {
			this.addPlayer(playerMin, playerMax);
		}
	}

	/**
	 * add player if not exists and calculate possible min id
	 * 
	 * @param playerMin
	 * @param playerMax
	 */
	public void addPlayerIfNotExists(ID playerMax) {
		if (this.findPlayerForID(playerMax) == null) {
			this.addPlayer(findPossibleMinId(playerMax), playerMax);
		}
	}

	/**
	 * find player with given id range
	 * 
	 * @param id
	 * @return array first entry is min value, second is max value, null if none
	 *         found
	 */
	public ID[] findPlayerForIDinRange(ID id) {
		for (Entry<ID, ID> entry : player.entrySet()) {
			if (IdMath.idCompare(entry.getKey(), id) <= 0
					&& IdMath.idCompare(entry.getValue(), id) >= 0) {
				return new ID[] { entry.getValue(), entry.getKey() };
			}
		}
		return null;
	}

	/**
	 * find player for given max id
	 * 
	 * @param id
	 * @return
	 */
	public ID findPlayerForID(ID id) {
		return player.get(id);
	}

	/**
	 * Set state of specific field
	 * 
	 * @param id
	 * @param state
	 */
	public void setState(ID id, FieldState state) {
		boardState.put(id, state);
	}

	/**
	 * Get state of specific field
	 * 
	 * @param id
	 * @return
	 */
	public FieldState getFieldState(ID id) {
		if (boardState.get(id) != null) {
			return boardState.get(id);
		}
		return FieldState.UNKNOWN;
	}

	/**
	 * check if given id is in range from ship
	 * 
	 * @param id
	 * @return
	 */
	public boolean checkForShip(ID id) {
		ID lastID = null;
		List<ID> idList = new ArrayList<ID>();
		idList.addAll(boardState.keySet());
		Collections.sort(idList);

		for (ID e : idList) {
			if (lastID != null) {

				if (id.isInInterval(
						new ID(lastID.toBigInteger().add(BigInteger.ONE)
								.toByteArray()), e)) {
					return boardState.get(lastID) == FieldState.SHIP;
				}
			}
			lastID = e;
		}

		return boardState.get(id) == FieldState.SHIP;
	}

	/**
	 * find all dead player
	 * 
	 * @return
	 */
	public Map<ID, ID> findDeadPlayer() {
		Map<ID, ID> deadPlayer = new HashMap<ID, ID>();

		for (Entry<ID, ID> pl : player.entrySet()) {
			if (isDead(pl)) {
				deadPlayer.put(pl.getKey(), pl.getValue());
			}
		}
		return deadPlayer;
	}

	/**
	 * check if single player is dead
	 * 
	 * @param player
	 * @return
	 */
	public boolean isDead(Entry<ID, ID> player) {
		Map<ID, FieldState> playerField = this.getPlayerField(player);
		int hitCount = 0;
		for (Entry<ID, FieldState> field : playerField.entrySet()) {
			if (field.getValue() == FieldState.HIT)
				hitCount++;
		}
		return hitCount >= S;
	}

	/**
	 * return field of single player
	 * @param player
	 * @return
	 */
	public Map<ID, FieldState> getPlayerField(Entry<ID, ID> player) {
		Map<ID, FieldState> playerField = new HashMap<ID, FieldState>();
		for (Entry<ID, FieldState> field : boardState.entrySet()) {
			if (IdMath.isInIntervallInkulsive(field.getKey(),
					player.getValue(), player.getKey())) {
				playerField.put(field.getKey(), field.getValue());
			}
		}
		return playerField;
	}

}
