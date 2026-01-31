package com.sca.smartcampusbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating Faculty
 * Used in POST/PUT requests to faculty endpoints
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRequestDTO {
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    private Boolean isActive = true;
}
