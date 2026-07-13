package sales.management.app.enums;

public enum StatusAccount {
    LIVE("Live", "#16a34a"),
    DIE("Die", "#ef4444"),
    DIE_TRANG("Die trắng", "#facc15"),
    NONE("", "#f2994a");

    private String label;
    private String backgroudColor;

    StatusAccount(String label, String backgroudColor) {
        this.label = label;
        this.backgroudColor = backgroudColor;
    }

    public String getLabel() {
        return label;
    }

    public String getBackgroudColor() {
        return backgroudColor;
    }
}
