package theomenden.polyprolene.utils;

import net.minecraft.util.math.MathHelper;

public final class RenderUtils {
    public static int getRadius(int diameter) {
        return diameter / 2;
    }

    public static int getHalfHeight(int height) {
        return height / 2;
    }

    public static double calculateMouseAngle(int x, int y, int mouseX, int mouseY) {
        return (MathHelper.atan2(mouseY - y, mouseX - x) + MathUtils.TWO_PI) % MathUtils.TWO_PI;
    }
}
