package io.anuke.pixeleditor.scene2D;

import io.anuke.gdxutils.graphics.Hue;
import io.anuke.gdxutils.graphics.PixmapUtils;
import io.anuke.gdxutils.graphics.Textures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class HueBar extends BarActor {
	
	public HueBar() {
		super(false);
		if (!Textures.has("huebar"))
			Textures.put("huebar", PixmapUtils.hueTexture(150, 20));

	}

	@Override
	public void draw(Batch batch, float alpha) {
		drawBorder(batch, alpha);
		batch.setColor(brightness, brightness, brightness, alpha);
		batch.draw(Textures.get("huebar"), getX() + border, getY() + border, getWidth() - border*2, getHeight() - border*2);
		drawDisabled(batch, getX() + border, getY() + border, getWidth() - border*2, getHeight() - border*2);
		batch.setColor(Color.WHITE);
		drawSelection(batch, alpha);
	}

	@Override
	public Color getSelectedColor() {
		return Hue.fromHSB(selection, 1f, 1f);
	}
}