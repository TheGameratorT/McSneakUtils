package com.thegameratort.sneakutils.gui;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ButtonListEntry extends TooltipListEntry<Boolean> {
	private final ButtonWidget buttonWidget;
	private final List<ClickableWidget> widgets;

	// I know what I am doing, kthx
	@SuppressWarnings("all")
	public ButtonListEntry(Text fieldName, Text buttonText, Supplier<Optional<Text[]>> tooltipSupplier, Runnable onClick) {
		super(fieldName, tooltipSupplier, false);
		this.buttonWidget = ButtonWidget.builder(buttonText, (widget) -> {
			onClick.run();
		}).dimensions(0, 0, 150, 20).build();
		this.widgets = Lists.newArrayList(new ClickableWidget[]{this.buttonWidget});
	}

	public boolean isEdited() {
		return false;
	}

	public void save() {
	}

	public Boolean getValue() {
		return false;
	}

	public Optional<Boolean> getDefaultValue() {
		return Optional.of(Boolean.FALSE);
	}

	public void render(DrawContext ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
		super.render(ctx, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
		Window window = MinecraftClient.getInstance().getWindow();
		this.buttonWidget.active = this.isEditable();
		this.buttonWidget.setY(y);
		Text displayedFieldName = this.getDisplayedFieldName();
		if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
			ctx.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, 16777215);
			this.buttonWidget.setX(x);
		} else {
			ctx.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), x, y + 6, this.getPreferredTextColor());
			this.buttonWidget.setX(x + entryWidth - 150);
		}

		this.buttonWidget.setWidth(150);
		this.buttonWidget.render(ctx, mouseX, mouseY, delta);
	}

	public List<? extends Element> children() {
		return this.widgets;
	}

	public List<? extends Selectable> narratables() {
		return this.widgets;
	}
}
