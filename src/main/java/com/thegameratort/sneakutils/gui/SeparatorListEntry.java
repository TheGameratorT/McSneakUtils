package com.thegameratort.sneakutils.gui;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class SeparatorListEntry extends AbstractConfigListEntry<Boolean> {
	private final List<ClickableWidget> widgets;

	// I know what I am doing, kthx
	@SuppressWarnings("all")
	public SeparatorListEntry(Text fieldName) {
		super(fieldName, false);
		this.widgets = Lists.newArrayList(new ClickableWidget[]{});
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
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.textRenderer;
		Text fieldName = this.getFieldName();
		int textWidth = textRenderer.getWidth(fieldName);
		int halfEntryWidth = (entryWidth / 2);
		int halfTextWidth = (textWidth / 2);
		ctx.drawTextWithShadow(textRenderer, fieldName, x + (halfEntryWidth - halfTextWidth), (y + 6), 0xFFFFFF);
		int marginLinePad = 2;
		int textLinePad = 8;
		int firstLineEnd = (x + halfEntryWidth) - halfTextWidth;
		ctx.drawHorizontalLine(x + marginLinePad, firstLineEnd - textLinePad, y + 9, 0xFFFFFFFF);
		ctx.drawHorizontalLine(firstLineEnd + textWidth + textLinePad - 2, x + entryWidth - marginLinePad - 2, y + 9, 0xFFFFFFFF);
	}

	public List<? extends Element> children() {
		return this.widgets;
	}

	public List<? extends Selectable> narratables() {
		return this.widgets;
	}
}
