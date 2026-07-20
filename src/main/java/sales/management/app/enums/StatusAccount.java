package sales.management.app.enums;

public enum StatusAccount {
    LIVE("Live", "#16a34a", "text-amber-900 bg-orange-100"),
    DIE("Die", "#ef4444", "text-white bg-slate-700"),
    DIE_TRANG("Die trắng", "#facc15", "text-sky-900 bg-sky-100"),
    NONE("", "#f2994a", "");

    private String label;
    private String backgroudColor;
    private String cssClass;

    StatusAccount(String label, String backgroudColor, String cssClass) {
        this.label = label;
        this.backgroudColor = backgroudColor;
        this.cssClass = cssClass;

    }

    public String getLabel() {
        return label;
    }

    public String getBackgroudColor() {
        return backgroudColor;
    }

    public String getCssClass() {
        return cssClass;
    }
}
