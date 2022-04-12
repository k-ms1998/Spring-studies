package jpql;

public class MemberDTO {
    private Long id;
    private String username;
    private int age;
    private MemberType memberType; //Member에서 memberType을 MemberType으로 지정했으므로, MebmerDTO에서도 MemberTpye으로 지정해줘야 됨

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

    public MemberDTO(Long id, MemberType memberType) {
        this.id = id;
        this.memberType = memberType;
    }

    public MemberDTO(Long id, int age, MemberType memberType) {
        this.id = id;
        this.age = age;
        this.memberType = memberType;
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

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }
}
