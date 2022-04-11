package jpql;

public class MemberDTO {
    private Long id;
    private String username;
    private int age;

    public MemberDTO() {
    }

    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public MemberDTO(Long id, String username, int age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

}
