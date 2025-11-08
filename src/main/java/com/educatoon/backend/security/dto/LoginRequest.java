
package com.educatoon.backend.security.dto;

import lombok.Data;

/**
 *
 * @author Diego
 */

@Data
public class LoginRequest {
    private String email;
    private String password;
}
