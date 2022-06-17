package richiesams.enderio.reforged.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class AlloySmelterScreen extends HandledScreen<BuiltScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(EnderIOReforgedBaseMod.MOD_ID, "textures/gui/alloy_smelter_auto.png");

    public AlloySmelterScreen(BuiltScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        // Do nothing
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draw the crafting progress
        if (handler.isCrafting()) {
            float progress = handler.getScaledProgress();
            int height = (int) (14 * progress);
            if (height > 0) {
                drawTexture(matrices, x + 56, y + 36 + (14 - height), 176, 14 - height, 14, height);
                drawTexture(matrices, x + 104, y + 36 + (14 - height), 176, 14 - height, 14, height);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
