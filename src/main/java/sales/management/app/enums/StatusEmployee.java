package sales.management.app.enums;

public enum StatusEmployee {
    NGHI_VIEC("Nghỉ việc"),
    DANG_LAM_VIEC("Đang làm việc");

    private String label;

    StatusEmployee(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}