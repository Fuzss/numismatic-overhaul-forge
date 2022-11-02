package com.glisco.numismaticoverhaul.client.gui.purse;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.client.gui.CurrencyTooltipRenderer;
import com.glisco.numismaticoverhaul.currency.Currency;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;

public class PurseButton extends ImageButton {

    private final CurrencyComponent currencyStorage;
    private final Screen parent;
    private final Component TOOLTIP_TITLE;

    public PurseButton(int x, int y, OnPress pressAction, Player player, Screen parent) {
        super(x, y, 11, 13, 62, 0, 13, PurseWidget.TEXTURE, pressAction);
        this.currencyStorage = ModComponents.CURRENCY.get(player);
        this.parent = parent;
        this.TOOLTIP_TITLE = Component.translatable("gui.numismaticoverhaul.purse_title").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Currency.GOLD.getNameColor())));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Minecraft.getInstance().player.isSpectator()) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderToolTip(PoseStack matrices, int mouseX, int mouseY) {
        CurrencyTooltipRenderer.renderTooltip(
                currencyStorage.getValue(),
                matrices, parent,
                TOOLTIP_TITLE,
                x + 14, y + 5);
    }
}
