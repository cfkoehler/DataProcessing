package DpFeeder.ProcessingFeeder.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class objectModel {
    private UUID uuid;
    private Date feedingDate;
    private String ObjectURL;
    private String ObjectBucket;
}
