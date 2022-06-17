package richiesams.enderio.reforged.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class SimpleAlloySmelterScreen extends HandledScreen<BuiltScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(EnderIOReforgedBaseMod.MOD_ID, "textures/gui/simple_alloy_smelter.png");

    public SimpleAlloySmelterScreen(BuiltScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        // Draw the crafting progress
        if (handler.isCrafting()) {
            float progress = handler.getScaledProgress();
            int height = (int) (14 * progress);
            if (height > 0) {
                drawTexture(matrices, x + 61, y + 37 + (14 - height), 181, 14 - height, 14, height);
                drawTexture(matrices, x + 109, y + 37 + (14 - height), 181, 14 - height, 14, height);
            }
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
