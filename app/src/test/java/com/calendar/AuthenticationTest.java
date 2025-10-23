package com.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uni.space.finder.Account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Authentication System Tests")
public class AuthenticationTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private PrintWriter writer;
    
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    @DisplayName("Should authenticate valid user credentials")
    void testValidUserAuthentication() {
        // Test data from accounts.txt
        String validEmail = "testuser@example.com";
        String validPassword = "pass123";
        
        // Test the Account.login method
        boolean result = Account.login(validEmail, validPassword);
        
        assertTrue(result, "Valid user credentials should authenticate successfully");
    }

    @Test
    @DisplayName("Should reject invalid user credentials")
    void testInvalidUserAuthentication() {
        String invalidEmail = "nonexistent@example.com";
        String invalidPassword = "wrongpassword";
        
        boolean result = Account.login(invalidEmail, invalidPassword);
        
        assertFalse(result, "Invalid user credentials should be rejected");
    }

    @Test
    @DisplayName("Should reject empty credentials")
    void testEmptyCredentials() {
        boolean resultEmptyEmail = Account.login("", "password");
        boolean resultEmptyPassword = Account.login("test@example.com", "");
        boolean resultBothEmpty = Account.login("", "");
        
        assertFalse(resultEmptyEmail, "Empty email should be rejected");
        assertFalse(resultEmptyPassword, "Empty password should be rejected");
        assertFalse(resultBothEmpty, "Empty credentials should be rejected");
    }

    @Test
    @DisplayName("Should handle null credentials gracefully")
    void testNullCredentials() {
        boolean resultNullEmail = Account.login(null, "password");
        boolean resultNullPassword = Account.login("test@example.com", null);
        boolean resultBothNull = Account.login(null, null);
        
        assertFalse(resultNullEmail, "Null email should be rejected");
        assertFalse(resultNullPassword, "Null password should be rejected");
        assertFalse(resultBothNull, "Null credentials should be rejected");
    }

    @Test
    @DisplayName("Should authenticate admin users")
    void testAdminAuthentication() {
        String adminEmail = "admins@student.uts.edu.au";
        String adminPassword = "@dMin1234";
        
        boolean result = Account.login(adminEmail, adminPassword);
        
        assertTrue(result, "Admin credentials should authenticate successfully");
    }

    @Test
    @DisplayName("Should be case sensitive for passwords")
    void testPasswordCaseSensitivity() {
        String email = "testuser@example.com";
        String correctPassword = "pass123";
        String wrongCasePassword = "PASS123";
        
        boolean correctResult = Account.login(email, correctPassword);
        boolean wrongCaseResult = Account.login(email, wrongCasePassword);
        
        assertTrue(correctResult, "Correct password should authenticate");
        assertFalse(wrongCaseResult, "Wrong case password should be rejected");
    }

    @Test
    @DisplayName("Should handle special characters in passwords")
    void testSpecialCharacterPasswords() {
        String email = "admin1@student.uts.edu.au";
        String passwordWithSpecialChars = "@dMin1234";
        
        boolean result = Account.login(email, passwordWithSpecialChars);
        
        assertTrue(result, "Password with special characters should work");
    }
}