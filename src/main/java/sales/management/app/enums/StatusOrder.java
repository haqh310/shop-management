package sales.management.app.enums;

public enum StatusOrder {
    UU_TIEN("Ưu tiên làm gấp", "bg-amber-700 text-white"),
    CO_TRACK("Có track", "bg-blue-100 text-blue-800"),
    WAITING("Waiting", "bg-lime-700 text-white"),
    PED("ped", "bg-green-600 text-white"),
    DA_LAM_LAI("Đã Làm Lại", "bg-purple-600 text-white"),
    CHO_TRACK("Chờ track", "bg-blue-500 text-white"),
    THUE("Thuê", "bg-violet-400 text-white"),
    DA_NHAP_TRACK("Đã nhập track", "bg-gray-500 text-white"),
    REFUND("Refund", "bg-yellow-700 text-white"),
    CANCEL("Cancel", "bg-red-500 text-white"),
    NONE("", "bg-white text-white");

    private String label;
    private String cssClass;

    StatusOrder(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}
