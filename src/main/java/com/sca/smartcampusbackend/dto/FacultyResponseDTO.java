package com.sca.smartcampusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Faculty responses
 * Used in GET requests to return faculty data
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyResponseDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String phone;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
