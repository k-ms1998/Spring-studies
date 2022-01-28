package hello.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;

@Getter
@Setter
@ToString
public class HelloLombok {
    public String name;
    public int age;

    public static void main(String[] args) {
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setAge(10);
        helloLombok.setName("myLombok");

        System.out.println(helloLombok.getAge());
        System.out.println(helloLombok.getName());
        System.out.println(helloLombok);
    }
}
