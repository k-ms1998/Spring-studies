package study.datajpa.repository.dto;

public class UsernameAndAgeDTO {
    private String username;
    private int age;

    public UsernameAndAgeDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }
}
