package com.conduent.ts.ops.restapi.util;

public class Validator {
	public static boolean isAlphanumeric(String input, String extraChar) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[A-Za-z0-9_"+ extraChar + "]+$");
		}
	}
	
	public static boolean isNumeric(String input, String extraChar) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[0-9"+ extraChar + "]+$");
		}
	}
	
	public static boolean isCharacter(String input, String extraChar) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[A-Za-z_"+ extraChar + "]+$");
		}
	}
	
	public static boolean isNumber(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^\\-?[0-9]+(\\.[0-9]+)?$");
		}
	}
	
	public static boolean isEmail(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[A-Za-z0-9_\\-]+(\\.[A-Za-z0-9_\\-]+)*@[A-Za-z0-9_\\-]+(\\.[A-Za-z0-9_\\-]+)*(\\.[A-Za-z]{2,})$");
		}
	}
	
	public static boolean isName(String input) {
		return isCharacter(input, "\\s\\-\\.\\'\\,");
	}
	
	public static boolean isAddress(String input) {
		return isAlphanumeric(input, "\\s\\,\\.\\'\\-\\#");
	}
	
	public static boolean isCity(String input) {
		return isCharacter(input, "\\s\\.\\-");
	}
	
	public static boolean isZip(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[0-9]{5}(\\-[0-9]{4})?$");
		}
	}
	
	public static boolean isPhone(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[0-9]{10}$");
		}
	}
	
	public static boolean isUsername(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[A-Za-z0-9_\\-]{4,16}$");
		}
	}
	
	public static boolean isPwd(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})$");
		}
	}
	
	public static boolean isSecurityA(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("^[a-zA-Z0-9_\\-\\.\\s\\,\\(\\)]{1,30}$");
		}
	}
	
	public static boolean isYN(String input) {
		if (input == null || input.equals("")) {
			return true;
		} else {
			return input.matches("[YN]{1}");
		}
	}
	
}