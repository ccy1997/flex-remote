package fc.flexremote;

import java.io.Serializable;

public class RemoteControlConfig implements Serializable {
    private String name;
    private int orientation;

    public RemoteControlConfig(String name, int orientation) {
        this.name = name;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrientation() {
        return orientation;
    }

}
