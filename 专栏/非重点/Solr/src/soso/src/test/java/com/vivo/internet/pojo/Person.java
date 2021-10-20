package com.vivo.internet.pojo;

import org.apache.solr.client.solrj.beans.Field;

public class Person {

    @Field
    private String nameCN;

    @Field
    private String hobby;

    public Person() {
    }

    public Person(String nameCN, String hobby) {
        this.nameCN = nameCN;
        this.hobby = hobby;
    }

    public String getNameCN() {
        return nameCN;
    }

    public void setNameCN(String nameCN) {
        this.nameCN = nameCN;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return "Person{" +
                "nameCN='" + nameCN + '\'' +
                ", hobby='" + hobby + '\'' +
                '}';
    }
}
