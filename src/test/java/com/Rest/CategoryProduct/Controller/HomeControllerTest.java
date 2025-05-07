package com.Rest.CategoryProduct.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class HomeControllerTest {
    @InjectMocks
    private HomeController homeController;

    @Test
    void testGetLoggedInUser() {
        Principal mockPrinciple = mock(Principal.class);
        when(homeController.getLoggedInUser(mockPrinciple)).thenReturn("Mock User");

        String result = homeController.getLoggedInUser(mockPrinciple);
        assertEquals("Mock User",result);
    }
}