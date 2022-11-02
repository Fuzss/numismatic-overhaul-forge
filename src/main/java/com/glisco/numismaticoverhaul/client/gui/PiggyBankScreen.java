package com.glisco.numismaticoverhaul.client.gui;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.block.PiggyBankScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PiggyBankScreen extends AbstractContainerScreen<PiggyBankScreenHandler> {
    private static final ResourceLocation PIGGY_BANK_LOCATION = new ResourceLocation(NumismaticOverhaul.MOD_ID, "textures/gui/piggy_bank.png");

//    private TextureComponent bronzeHint, silverHint, goldHint;

    public PiggyBankScreen(PiggyBankScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageHeight = 145;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = (this.imageWidth - Minecraft.getInstance().font.width(title)) / 2;
    }

//    @Override
//    protected void build(FlowLayout rootComponent) {
//        this.bronzeHint = this.uiAdapter.rootComponent.childById(TextureComponent.class, "bronze-hint");
//        this.silverHint = this.uiAdapter.rootComponent.childById(TextureComponent.class, "silver-hint");
//        this.goldHint = this.uiAdapter.rootComponent.childById(TextureComponent.class, "gold-hint");
//    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.renderTooltip(matrices, mouseX, mouseY);
//        this.bronzeHint.sizing(this.menu.getSlot(0).hasItem() ? Sizing.fixed(0) : Sizing.fixed(16));
//        this.silverHint.sizing(this.menu.getSlot(1).hasItem() ? Sizing.fixed(0) : Sizing.fixed(16));
//        this.goldHint.sizing(this.menu.getSlot(2).hasItem() ? Sizing.fixed(0) : Sizing.fixed(16));
    }

    @Override
    protected void renderBg(PoseStack matrices, float p_98803_, int p_98804_, int p_98805_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, PIGGY_BANK_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrices, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (!this.menu.getSlot(0).hasItem()) {
            this.blit(matrices, i + 62, j + 26, 0, 145, 16, 16);
        }
        if (!this.menu.getSlot(1).hasItem()) {
            this.blit(matrices, i + 80, j + 26, 16, 145, 16, 16);
        }
        if (!this.menu.getSlot(2).hasItem()) {
            this.blit(matrices, i + 98, j + 26, 32, 145, 16, 16);
        }
    }
}
