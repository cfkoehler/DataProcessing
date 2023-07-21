package DpFeeder.ProcessingFeeder.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = SessionMessage.class)
public class SessionMessage {
    private String filePath;
    private int offset;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setOffset(String offset) {
        this.offset = Integer.parseInt(offset);
    }

    @Override
    public String toString() {
        return "session [filePath=" + filePath + ", offset=" + offset + "]";
    }
}
