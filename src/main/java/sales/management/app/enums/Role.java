package sales.management.app.enums;

public enum Role {
    QUAN_LY("Quản lý"),
    BAN_HANG("Bán hàng"),
    KHO("Kho");

    private String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}