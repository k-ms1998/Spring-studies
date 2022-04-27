package study.datajpa.repository.dto;

public class UsernameOnlyDTO {

    private String username;

    public UsernameOnlyDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
