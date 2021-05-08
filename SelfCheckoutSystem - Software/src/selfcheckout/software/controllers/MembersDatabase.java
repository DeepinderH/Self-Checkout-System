package selfcheckout.software.controllers;

import java.util.HashMap;
import java.util.Map;


/**
 * Database holding a list of all cards linked to members
 *
 */
public class MembersDatabase {
	public static Map<String, MembershipAccount> MEMBERSHIP_DATABASE = new HashMap<>();
}
