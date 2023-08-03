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
import org.jetbrains.annotations.NotNull;
import theomenden.polyprolene.manager.KeyBindingsManager;
import theomenden.polyprolene.mixin.KeyBindAccessorMixin;
import theomenden.polyprolene.utils.MathUtils;

import java.util.stream.IntStream;

public class PolyproleneScreen extends Screen {
    int timeIn = 0;
    int slotSelected = -1;

    private InputUtil.Key conflictedKeyBinding = InputUtil.UNKNOWN_KEY;

    final MinecraftClient minecraftClient;

    public PolyproleneScreen() {
        super(NarratorManager.EMPTY);

        minecraftClient = MinecraftClient.getInstance();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int xCoord = width / 2;
        int yCoord = height / 2;
        int maxRadius = 80;

        double mouseAngleAt = calculateMouseAngle(xCoord, yCoord, mouseX, mouseY);

        int conflictingSegments = KeyBindingsManager
                .getConflictingBindingsForKey(conflictedKeyBinding).size();

        final float conflictingSegmentInv = 1.0f / conflictingSegments;

        float degreesPerSegment = (float) MathUtils.TWO_PI / conflictingSegments;

        slotSelected = -1;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        IntStream.range(0, conflictingSegments)
                .forEach(segment -> {
                    boolean mouseIsInSector = isMouseInSector(degreesPerSegment,mouseAngleAt,segment);
                    float radius = calculateMaximumRadius(delta, segment, conflictingSegmentInv, maxRadius);
                    if (mouseIsInSector) {
                        radius *= 1.025f;
                    }

                    int gs = 0x40;
                    if (segment % 2 == 0) {
                        gs += 0x19;
                    }
                    int r = gs;
                    int g = gs;
                    int b = gs;
                    int a = 0x66;

                    if (segment == 0) {
                        bufferBuilder.vertex(xCoord, yCoord, 0).color(r, g, b, a).next();
                    }

                    if (mouseIsInSector) {
                        slotSelected = segment;
                        r = g = b = 0xFF;
                    }

                    fillBufferBuilder(segment, degreesPerSegment, xCoord, radius, yCoord, bufferBuilder, r, g, b, a);
                });
        tessellator.draw();

        IntStream.range(0, conflictingSegments)
                .forEach(segment -> {
                    float radius = calculateMaximumRadius(delta, segment, conflictingSegmentInv, maxRadius);

                    if (isMouseInSector(degreesPerSegment, mouseAngleAt, segment)) {
                        radius *= 1.025f;
                    }

                    float rad = (segment + 0.5f) * degreesPerSegment;

                    float xp = xCoord + MathHelper.cos(rad) * radius;

                    float yp = yCoord + MathHelper.sin(rad) * radius;

                    String boundKey = Text.translatable(
                            KeyBindingsManager
                                    .getConflictingBindingsForKey(conflictedKeyBinding)
                                                                          .get(segment)
                                                                          .getTranslationKey())
                                          .getString();
                    float xsp = xp - 4;
                    float ysp = yp;
                    String name = getSectorName(segment, degreesPerSegment, mouseAngleAt, boundKey);
                    int width = textRenderer.getWidth(name);
                    if (xsp < xCoord) {
                        xsp -= width - 8;
                    }
                    if (ysp < yCoord) {
                        ysp -= 9;
                    }
                    context.drawCenteredTextWithShadow(textRenderer, name, (int) xsp, (int) ysp, 0xFFFFFF);
                });
    }

    @Override
    public void tick() {
        super.tick();
        var mcClientHandle = MinecraftClient
                .getInstance()
                .getWindow()
                .getHandle();

        if(!InputUtil.isKeyPressed(mcClientHandle, conflictedKeyBinding.getCode())) {
            this.minecraftClient.setScreen(null);
            createKeyPressedEvent();
        }
        timeIn++;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void setConflictedKeyBinding(InputUtil.Key key) {
        this.conflictedKeyBinding = key;
    }

    private static void fillBufferBuilder(int segment, float degreesPerSegment, int xCoord, float radius, int yCoord, BufferBuilder bufferBuilder, int r, int g, int b, int a) {
        var incremental = degreesPerSegment + MathUtils.RADIANS_TO_DEGREES * 0.5;

        for (float i = 0; i < incremental; i += (float) MathUtils.RADIANS_TO_DEGREES) {
            float rad = i + segment * degreesPerSegment;
            float xp = xCoord + MathHelper.cos(rad) * radius;
            float yp = yCoord + MathHelper.sin(rad) * radius;

            if (i == 0) {
                bufferBuilder
                        .vertex(xp, yp, 0).color(r, g, b, a).next();
            }

            bufferBuilder
                    .vertex(xp, yp, 0).color(r, g, b, a).next();
        }
    }

    private void createKeyPressedEvent() {
        if(slotSelected == -1) {
            return;
        }

        KeyBinding keyBinding = KeyBindingsManager.getConflictingBindingsForKey(conflictedKeyBinding).get(slotSelected);
        ((KeyBindAccessorMixin) keyBinding).setPressed(true);
        ((KeyBindAccessorMixin) keyBinding).setTimesPressed(1);
    }

    private static double calculateMouseAngle(int x, int y, int mouseX, int mouseY) {
        double deltaX = mouseX - x;
        double deltaY = mouseY - y;
        return Math.atan2(deltaY,deltaX);
    }

    @NotNull
    private static String getSectorName(int segment, float degreesPerSegment, double mouseAngleAt, String boundKey) {
        return (isMouseInSector(degreesPerSegment, mouseAngleAt, segment)
                ? Formatting.UNDERLINE
                : Formatting.RESET) + boundKey;
    }

    private float calculateMaximumRadius(float delta, int segment, float conflictingSegmentInv, int maxRadius) {
        return Math.max(0F, Math.min(((timeIn + delta) - (segment * 6F * conflictingSegmentInv)) * 40F, maxRadius));
    }

    private static boolean isMouseInSector(float degreesPerSegment, double mouseAngleAt, int segment) {
        return degreesPerSegment * segment < mouseAngleAt && mouseAngleAt < mouseAngleAt * (segment + 1);
    }
}
