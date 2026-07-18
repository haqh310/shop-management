package sales.management.app.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDetail {
    private String platformName;
    private List<Integer> countList;
    private int totalOfPlatform;
}