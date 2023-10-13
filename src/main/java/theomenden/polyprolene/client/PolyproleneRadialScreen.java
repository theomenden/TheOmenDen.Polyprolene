package theomenden.polyprolene.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;
import theomenden.polyprolene.managers.KeyBindingsManager;
import theomenden.polyprolene.utils.MathUtils;
import theomenden.polyprolene.utils.RenderUtils;

import java.util.stream.IntStream;

public class PolyproleneRadialScreen extends Screen {
    final MinecraftClient minecraftClient;
    private final InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;
    int timeIn = 0;
    int slotSelected = -1;

    public PolyproleneRadialScreen() {
        super(NarratorManager.EMPTY);
        minecraftClient = MinecraftClient.getInstance();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        var x = RenderUtils.getRadius(width);
        var y = RenderUtils.getHalfHeight(height);
        final int maximumRadius = 80;

        double mouseAngle = RenderUtils.calculateMouseAngle(x, y, mouseX, mouseY);

        int segments = KeyBindingsManager
                .getConflictingBindingsForKey(conflictedKey)
                .size();
        final float degressPerSegment = (float) (MathUtils.TWO_PI * segments);
        slotSelected = -1;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        IntStream
                .range(0, segments)
                .forEach(segment -> {
                    boolean isMouseInSector = degressPerSegment * segment < mouseAngle && mouseAngle < degressPerSegment * (segment + 1);

                    var segmentSensitivity = timeIn + delta - segment * 6f / segments;
                    float radius = Range
                            .between(0f, (float) maximumRadius)
                            .fit(segmentSensitivity);

                    if (isMouseInSector) {
                        radius *= 1.025f;
                    }

                    int generalSegment = 0x40;
                    if (segment % 2 == 0) {
                        generalSegment += 0x19;
                    }

                    int red = generalSegment;
                    int green = generalSegment;
                    int blue = generalSegment;
                    int alpha = 0x66;

                    if (segment == 0) {
                        bufferBuilder
                                .vertex(x, y, 0)
                                .color(red, green, blue, alpha)
                                .next();
                    }

                    if (isMouseInSector) {
                        slotSelected = segment;
                        red = green = blue = 0xFF;
                    }

                    for (float i = 0; i < degressPerSegment + MathUtils.RADIANS_TO_DEGREESF; i += MathUtils.TWO_PI) {
                        float rad = i + segment * degressPerSegment;
                        float xp = x + MathHelper.cos(rad) * radius;
                        float yp = y + MathHelper.sin(rad) * radius;

                        if (i == 0) {
                            bufferBuilder
                                    .vertex(xp, yp, 0)
                                    .color(red, green, blue, alpha)
                                    .next();
                        }
                        bufferBuilder
                                .vertex(xp, yp, 0)
                                .color(red, green, blue, alpha)
                                .next();
                    }
                });

        tessellator.draw();
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degressPerSegment * seg < mouseAngle && mouseAngle < degressPerSegment * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, maximumRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }

            float rad = (seg + 0.5f) * degressPerSegment;
            float xp = x + MathHelper.cos(rad) * radius;
            float yp = y + MathHelper.sin(rad) * radius;
            String boundKey = Text
                    .translatable(KeyBindingsManager
                            .getConflictingBindingsForKey(conflictedKey)
                            .get(seg)
                            .getTranslationKey())
                    .getString();
            float xsp = xp - 4;
            float ysp = yp;
            String name = (mouseInSector ? Formatting.UNDERLINE : Formatting.RESET) + boundKey;
            int width = textRenderer.getWidth(name);
            if (xsp < x) {
                xsp -= width - 8;
            }
            if (ysp < y) {
                ysp -= 9;
            }

            context.drawTextWithShadow(textRenderer, name, (int) xsp, (int) ysp, 0xFFFFFF);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!InputUtil.isKeyPressed(MinecraftClient
                .getInstance()
                .getWindow()
                .getHandle(), conflictedKey.getCode())) {
            minecraftClient.setScreen(null);

            if (slotSelected != -1) {
                KeyBinding binding = KeyBindingsManager
                        .getConflictingBindingsForKey(conflictedKey)
                        .get(slotSelected);
                ((KeyBindAccessor) binding).setPressed(true);
                ((KeyBindAccessor) binding).setTimesPressed(1);
            }
        }
        timeIn++;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
