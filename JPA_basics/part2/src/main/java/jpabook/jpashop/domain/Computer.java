package jpabook.jpashop.domain;

import javax.persistence.Entity;

@Entity
public class Computer extends Item {
    private String cpu;

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
}
