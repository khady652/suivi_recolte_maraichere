package sn.agriculture.auth_service.dto;





import lombok.Data;

@Data
public class CreateAccountRequest {
    private String email;
    private String telephone;
    private String role;
    private String password;
}


