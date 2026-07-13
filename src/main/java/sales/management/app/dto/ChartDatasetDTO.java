package sales.management.app.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
public class ChartDatasetDTO {
    private String label;
    private List<?> data;
    private String backgroundColor;

    public ChartDatasetDTO(String label, List<?> data, String backgroundColor) {
        this.label = label;
        this.data = data;
        this.backgroundColor = backgroundColor;
    }

}
