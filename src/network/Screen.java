package network;

import javafx.geometry.Rectangle2D;

public class Screen {

    public double screenWidth, screenHeight;
    private String screenPosition = "LEFT";
    private boolean isActivated;

    public Screen() {
        Rectangle2D primaryScreenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        this.screenWidth = primaryScreenBounds.getWidth();
        this.screenHeight = primaryScreenBounds.getHeight();
    }

    public Screen(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setScreenPosition(String pos) {
        this.screenPosition = pos;
    }

    public String getScreenPosition() {
        return screenPosition;
    }

    public boolean isSelected() {
        return isActivated;
    }

    public void setSelected(boolean activated) {
        isActivated = activated;
    }

}
