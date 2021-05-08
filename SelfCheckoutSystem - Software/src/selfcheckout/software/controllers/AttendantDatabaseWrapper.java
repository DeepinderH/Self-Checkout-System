package selfcheckout.software.controllers;

import java.util.HashMap;

import selfcheckout.software.controllers.exceptions.AttendantAccountAlreadyExistsException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;

public class AttendantDatabaseWrapper {
	private final AttendantDatabase attendantDatabase;

	/**
	 * Constructor
	 */
	public AttendantDatabaseWrapper(AttendantDatabase attendantDatabase) {
		this.attendantDatabase = attendantDatabase;
	}

	/**
	 * Populates the database of all the current login information
	 */
	public void initializeAccountLoginInformationDatabase() {
		this.attendantDatabase.ATTENDANT_DATABASE.put("123456", "Password");
	}
	
	/**
	 * Populates the database of existing card-bearing members
	 * 
	 * @param attendantID
	 * 		the ID of the attendant who is attempting to log in
	 * @param attendantPassword
	 * 		the password of the attendant who is attempting to log in
	 * @throws AttendantAccountAlreadyExistsException 
	 * 		thrown if the ID is already taken
	 */
	public void initializeAttendantLoginInfo(int attendantID, String attendantPassword) throws AttendantAccountAlreadyExistsException {
		if (this.hasAnAccount(attendantID)) {
			throw new AttendantAccountAlreadyExistsException("ID is already taken, please try a different ID");
		}
		this.attendantDatabase.ATTENDANT_DATABASE.put(String.valueOf(attendantID) , attendantPassword);
	}

	/**
	 * Reports both the ID and password are correct
	 * @param attendantID
	 * 		the ID of the attendant
	 * @param attendantPassword
	 * 		the password used to log in for the attendantID
	 * @throws IncorrectAttendantLoginInformationException
	 * 		when either the ID or password does not match the database
	 */
	public void validateAttendantCredentials(int attendantID, String attendantPassword) throws IncorrectAttendantLoginInformationException {
		if (!this.hasAnAccount(attendantID) ||
				!this.validateAttendantPassword(attendantID, attendantPassword)) {
			throw new IncorrectAttendantLoginInformationException("Invalid login credentials!");
		}
	}
	
	/**
	 * Reports whether or not a attendant has an account in the database
	 * @param attendantID
	 * 		the ID of the attendant
	 * @return
	 * 		true if the attendant has an account in the database, false otherwise
	 */
	private Boolean hasAnAccount(int attendantID) {
		return this.attendantDatabase.ATTENDANT_DATABASE.containsKey(String.valueOf(attendantID));	
	}

	/**
	 *Check if the password given is correct for the user name
	 * @param attendantID
	 * 		the ID of the Attendant0
	 * @param attendantPassword
	 * 		the password being used to login to the system for the user name
	 * @return
	 * 		true if the password is correct for that user name, false otherwise
	 */
	private Boolean validateAttendantPassword(int attendantID, String attendantPassword) {
		return attendantPassword.equals(this.attendantDatabase.ATTENDANT_DATABASE.get(String.valueOf(attendantID)));
	}

	/**
	 * Clears the current database
	 */
	public void clearAttendantDatabase() {
		this.attendantDatabase.ATTENDANT_DATABASE = new HashMap<>();
	}
}
