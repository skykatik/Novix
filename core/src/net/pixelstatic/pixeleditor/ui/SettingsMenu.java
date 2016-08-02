package net.pixelstatic.pixeleditor.ui;

import static net.pixelstatic.pixeleditor.modules.Core.s;
import net.pixelstatic.pixeleditor.modules.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class SettingsMenu extends VisDialog{
	private Core main;
	
	public SettingsMenu(Core main){
		super("Settings");
		this.main = main;
		
		setFillParent(true);
		getTitleLabel().setColor(Color.CORAL);
		getTitleTable().row();
		getTitleTable().add(new Separator()).expandX().fillX().padTop(3 * s);
		
		Table table = getContentTable();

		table.add().height(20).row();

		VisTextButton back = new VisTextButton("Back");
		back.add(new Image(VisUI.getSkin().getDrawable("icon-arrow-left"))).size(40 * s).center();

		back.getCells().reverse();
		back.getLabelCell().padRight(40f * s);

		getButtonsTable().add(back).width(Gdx.graphics.getWidth()).height(60 * s);
		setObject(back, false);
	}
	
	public void addScrollSetting(final String name, int min, int max, int value){
		final VisLabel label = new VisLabel(name + ": " + main.prefs.getInteger(name, value));
		final VisSlider slider = new VisSlider(min, max, 1, false);
		slider.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				label.setText(name + ": " + slider.getValue());
				main.prefs.putInteger(name, (int)slider.getValue());
			}
		});
		slider.setValue(main.prefs.getInteger(name));
		Table table = getContentTable();
		table.top().left().add(label).align(Align.left);
		table.row();
		table.add(slider).width(200 * s).padBottom(40f);
		table.row();
	}
	
	public void addPercentScrollSetting(final String name){
		final VisLabel label = new VisLabel();
		final VisSlider slider = new VisSlider(0f, 2f, 0.01f, false);
		slider.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				label.setText(name + ": " + (int)(slider.getValue()*100) + "%");
				main.prefs.putFloat(convert(name), slider.getValue());
			}
		});
		slider.setValue(main.prefs.getFloat(convert(name), 1f));
		slider.fire(new ChangeListener.ChangeEvent());
		Table table = getContentTable();
		table.top().left().add(label).align(Align.left);
		table.row();
		table.add(slider).width(200 * s).padBottom(40f);
		table.row();
	}

	public void addCheckSetting(final String name, boolean value){
		final VisLabel label = new VisLabel(name);
		final VisCheckBox box = new VisCheckBox("", main.prefs.getBoolean(convert(name), value));
		box.getImageStackCell().size(40 * s);
		box.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				main.prefs.putBoolean(convert(name), box.isChecked());
			}
		});
		Table table = getContentTable();
		table.top().left().add(label).align(Align.left);
		table.add(box);
		table.row();
	}
	
	private String convert(String name){
		return name.replace(" ", "").toLowerCase();
	}
	
	public void result(Object o){
		main.prefs.flush();
	}
}	
