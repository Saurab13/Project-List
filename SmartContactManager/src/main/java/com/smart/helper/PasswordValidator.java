package com.smart.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PasswordValidator {
	public boolean isValidPassword(String password) {
		// Check if the password is at least 8 characters long
		if (password.length() < 8) {
			return false;
		}

		// Check if the password contains at least one uppercase letter
		if (!password.matches(".*[A-Z].*")) {
			return false;
		}

		// Check if the password contains at least one special character
		Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
		Matcher specialCharMatcher = specialCharPattern.matcher(password);
		if (!specialCharMatcher.find()) {
			return false;
		}

		// Check if the password contains alphanumeric characters
		if (!password.matches(".*[a-zA-Z0-9].*")) {
			return false;
		}

		// If all conditions are met, the password is valid
		return true;
	}

}
