package de.haw.battleship;

import java.math.BigInteger;

import de.uniba.wiai.lspi.chord.data.ID;

public class IdMath {
	//**************************HELPER*******************************//
		/**
		 * decrement by one
		 * @param id
		 * @return
		 */
		public static ID addOneToID(ID id){
			return new ID (id.toBigInteger().add(BigInteger.ONE).toByteArray());
		}
		
		/**
		 * decrement by one
		 * @param id
		 * @return
		 */
		public static ID subOneFromID(ID id){
			return new ID (id.toBigInteger().subtract(BigInteger.ONE).toByteArray());
		}
		
		/**
		 * check if id is in interval inclusive boarders
		 * @param testID
		 * @param minRange
		 * @param maxRange
		 * @return
		 */
		public static boolean isInIntervallInkulsive(ID testID, ID minRange, ID maxRange){
			return testID.isInInterval(subOneFromID(minRange), addOneToID(maxRange));
		}
		
		public static ID zeroID(){
			return new ID(BigInteger.ZERO.toByteArray());
		}
		
}
