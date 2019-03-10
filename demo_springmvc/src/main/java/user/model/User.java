package user.model;

public class User {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    private String sex;

    public User() {}

    private User(Builder builder) {
        name = builder.name;
        sex = builder.sex;
    }

    public static final class Builder {
        private String name;
        private String sex;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
