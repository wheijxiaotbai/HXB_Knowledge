package demo.demo1.User.model;

public enum  UserRole {
    ROLE_LOWER("ROLE_LOWER"),
    ROLE_INTERMEDIATE("ROLE_INTERMEDIATE"),
    ROLE_SENIOR("ROLE_SENIOR");

    private String value;

    UserRole(String value) {this.value = value; }

    public String getValue() {
        return value;
    }
}
