package jpabook.jpashop.domain;

import javax.persistence.Entity;

@Entity
public class Phone extends Item{
    private String size;
    private String etc;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }
}
