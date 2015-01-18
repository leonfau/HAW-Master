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
		
		/**
		 * Create new zero ID
		 * @return
		 */
		public static ID zeroID(){
			return new ID(BigInteger.ZERO.toByteArray());
		}
		
		
		/**
		 * calculate size for single field
		 * @param min
		 * @param max
		 * @param fieldcount
		 * @return
		 */
		public static BigInteger calculateFieldSize(ID min, ID max, int fieldcount){
			if (min.toBigInteger().min(max.toBigInteger()).equals(min.toBigInteger())) {
				return max.toBigInteger().subtract(min.toBigInteger()).divide(BigInteger.valueOf(fieldcount));
			} else {
				return min.toBigInteger().subtract(max.toBigInteger()).divide(BigInteger.valueOf(fieldcount));
			}
		}
		
		/**
		 * add given value to id
		 * @param id
		 * @param value
		 * @return
		 */
		public static ID addToID(ID id, BigInteger value){
			return new ID(id.toBigInteger().add(value).toByteArray());
		}
		
		/**
		 * subtract given value from id
		 * @param id
		 * @param value
		 * @return
		 */
		public static ID subtractFromID(ID id, BigInteger value){
			return new ID(id.toBigInteger().subtract(value).toByteArray());
		}
		
		/**
		 * caluculate ID for field
		 * @param startID
		 * @param intervall
		 * @param field
		 * @return
		 */
		public static ID calcIDforField(ID startID, BigInteger intervall, int field){
			return new ID(intervall.multiply(BigInteger.valueOf(field)).add(startID.toBigInteger()).toByteArray());
		}
		


		public static int idCompare(ID a, ID b){
			if(a.getLength() == b.getLength()){
				return a.compareTo(b);
			}else{
				return a.getLength() < b.getLength() ? -1 : 1;
			}
		}
		
}
