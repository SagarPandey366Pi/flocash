package wallettemplate;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public final class TooltipUtil {
	
	private TooltipUtil() {}

    public static void setTooltip(Node node, Tooltip tooltip) {
        Tooltip.install(node, tooltip);
    }

    // only here for the FXMLLoader
    @Deprecated
    public static Tooltip getTooltip(Node node) {
        throw new UnsupportedOperationException();
    }
}
