package user.model;

import org.springframework.context.annotation.Bean;

public class User {

    private int id;
    private String name;
    private int age;

    public User() {}

    private User(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setAge(builder.age);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static final class Builder {
        private int id;
        private String name;
        private int age;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder age(int val) {
            age = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
