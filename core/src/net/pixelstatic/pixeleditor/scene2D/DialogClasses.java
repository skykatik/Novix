package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Filter;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidDialogs;
import net.pixelstatic.utils.graphics.PixmapUtils;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.scene2D.AndroidColorPicker;
import net.pixelstatic.utils.scene2D.ColorBox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class DialogClasses{
	static float s = GUI.s;

	public static class SizeDialog extends MenuDialog{
		VisTextField widthfield, heightfield;

		public SizeDialog(String title){
			super(title);

			widthfield = new VisTextField();
			heightfield = new VisTextField();
			widthfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
			heightfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());

			widthfield.setText((GUI.gui.drawgrid.canvas.width()) + "");
			heightfield.setText(GUI.gui.drawgrid.canvas.height() + "");
			
			float twidth = 160*s, theight = 40*s;

			getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(widthfield).size(twidth, theight).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
			getContentTable().add(heightfield).size(twidth, theight).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

			getContentTable().row();
			
			ok.addAction(new Action(){
				@Override
				public boolean act(float delta){
					ok.setDisabled(widthfield.getText().isEmpty() || heightfield.getText().isEmpty());
					return false;
				}
			});

		}

		public void result(){
			try{
				int width = Integer.parseInt(widthfield.getText());
				int height = Integer.parseInt(heightfield.getText());
				result(width, height);
			}catch(Exception e){
				e.printStackTrace();
				AndroidDialogs.showError(getStage(), "Image error!", e);
			}
		}

		public void result(int width, int height){

		}

	}
	
	public static class NamedSizeDialog extends MenuDialog{
		VisTextField widthfield, heightfield, namefield;

		public NamedSizeDialog(String title){
			super(title);

			widthfield = new VisTextField();
			heightfield = new VisTextField();
			namefield = new VisTextField();

			widthfield.setText((GUI.gui.drawgrid.canvas.width()) + "");
			heightfield.setText(GUI.gui.drawgrid.canvas.height() + "");
			
			
			float twidth = 160*s, theight = 40*s;

			getContentTable().add(new VisLabel("Name: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(namefield).size(twidth, theight).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(widthfield).size(twidth, theight).padRight(50 * s).padTop(40 * s);

			getContentTable().row();
			
			getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
			getContentTable().add(heightfield).size(twidth, theight).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

			getContentTable().row();
			
			ok.addAction(new Action(){
				@Override
				public boolean act(float delta){
					ok.setDisabled(widthfield.getText().isEmpty() || heightfield.getText().isEmpty() || namefield.getText().replace(" ", "").isEmpty());
					return false;
				}
			});

		}

		public void result(){
			try{
				int width = Integer.parseInt(widthfield.getText());
				int height = Integer.parseInt(heightfield.getText());
				result(namefield.getText(), width, height);
			}catch(Exception e){
				e.printStackTrace();
				AndroidDialogs.showError(getStage(), "Image error!", e);
			}
		}

		public void result(String name, int width, int height){

		}

	}


	public static class ContrastDialog extends FilterDialog{
		VisSlider slider;

		public ContrastDialog(){
			super(Filter.contrast, "Change Image Contrast");

			final VisLabel label = new VisLabel("Contrast: 0");

			slider = new VisSlider( -50f, 50, 1f, false);

			slider.setValue(0f);

			slider.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					label.setText("Contrast: " + slider.getValue());
					updatePreview();
				}
			});

			getContentTable().add(label).align(Align.left).padTop(15f * s).row();
			getContentTable().add(slider).expand().fill().padBottom(30 * s);

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{slider.getValue() / 50f};
		}
	}

	public static class ReplaceDialog extends FilterDialog{
		ColorBox from, to, selected;

		public ReplaceDialog(){
			super(Filter.replace, "Replace Colors");

			from = new ColorBox(GUI.gui.selectedColor());
			to = new ColorBox();

			final AndroidColorPicker picker = new AndroidColorPicker(false){
				public void onColorChanged(){
					selected.setColor(getSelectedColor());
				}
			};
			picker.setRecentColors(GUI.gui.apicker.getRecentColors());

			final VisDialog dialog = new VisDialog("Choose Color", "dialog");
			dialog.getContentTable().add(picker).expand().fill();

			VisTextButton button = new VisTextButton("OK");
			
			button.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					updatePreview();
				}
			});

			dialog.getButtonsTable().add(button).size(320 * s, 70 * s).pad(5f * s);
			dialog.setObject(button, true);

			VisImageButton closeButton = new VisImageButton("close-window");
			dialog.getTitleTable().add(closeButton).padRight( -getPadRight() + 0.7f);
			closeButton.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					dialog.hide();
				}
			});

			ClickListener listener = new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					selected = (ColorBox)event.getTarget();
					picker.setSelectedColor(event.getTarget().getColor());
					dialog.show(GUI.gui.stage);
				}
			};

			from.addSelectListener();
			to.addSelectListener();

			from.addListener(listener);
			to.addListener(listener);

			Table table = new VisTable();

			getContentTable().add(table).expand().fill();

			VisImageButton pickfrom = new VisImageButton(new TextureRegionDrawable(new TextureRegion(Textures.get("icon-pick"))));
			VisImageButton pickto = new VisImageButton(new TextureRegionDrawable(new TextureRegion(Textures.get("icon-pick"))));

			pickfrom.getImageCell().size(60 * s);

			pickto.getImageCell().size(60 * s);

			table.add(from).size(70 * s).pad(10 * s);

			Image image = new Image((Textures.get("icon-arrow-right")));

			table.add(image).size(60 * s).pad(5 * s);

			table.add(to).size(70 * s).pad(10 * s);

			/*
			VisLabel label = new VisLabel("Pick");
			label.setAlignment(Align.center);
			
			
			table.row();
			table.add(pickfrom).size(70*s).pad(4f);
			table.add().size(60*s).align(Align.center);
			table.add(pickto).size(70*s).pad(4f);
			*/

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{from.getColor(), to.getColor()};
		}
	}
	
	public static class ColorAlphaDialog extends FilterDialog{
		ColorBox selected;

		public ColorAlphaDialog(){
			super(Filter.colorToAlpha, "Color to Alpha");

			selected = new ColorBox(GUI.gui.selectedColor());
			
			selected.addSelectListener();

			final AndroidColorPicker picker = new AndroidColorPicker(false){
				public void onColorChanged(){
					selected.setColor(getSelectedColor());
				}
			};
			picker.setRecentColors(GUI.gui.apicker.getRecentColors());

			final VisDialog dialog = new VisDialog("Choose Color", "dialog");
			dialog.getContentTable().add(picker).expand().fill();

			VisTextButton button = new VisTextButton("OK");
			
			button.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					updatePreview();
				}
			});

			dialog.getButtonsTable().add(button).size(320 * s, 70 * s).pad(5f * s);
			dialog.setObject(button, true);

			VisImageButton closeButton = new VisImageButton("close-window");
			dialog.getTitleTable().add(closeButton).padRight( -getPadRight() + 0.7f);
			closeButton.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					dialog.hide();
				}
			});

			ClickListener listener = new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					selected = (ColorBox)event.getTarget();
					picker.setSelectedColor(event.getTarget().getColor());
					dialog.show(GUI.gui.stage);
				}
			};

			selected.addListener(listener);

			Table table = new VisTable();
			
			getContentTable().add(new VisLabel("Color:")).padTop(15f*s).row();
			
			getContentTable().add(table).expand().fill();
			
			
			table.add(selected).size(70 * s).pad(5 * s);

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{selected.getColor()};
		}
	}

	public static class ColorizeDialog extends FilterDialog{
		VisSlider hslider;
		VisSlider sslider;
		VisSlider bslider;

		public ColorizeDialog(){
			super(Filter.colorize, "Colorize Image");

			hslider = new VisSlider(0, 360, 1f, false);
			sslider = new VisSlider(0, 100, 1f, false);
			bslider = new VisSlider(0, 100, 1f, false);

			final VisLabel hlabel = new VisLabel("Hue:"), slabel = new VisLabel("Saturation:"), blabel = new VisLabel("Brightness:");

			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					hlabel.setText("Hue: " + hslider.getValue());
					slabel.setText("Saturation: " + sslider.getValue());
					blabel.setText("Brightness: " + bslider.getValue());

					updatePreview();
				}
			};

			hslider.addListener(listener);
			sslider.addListener(listener);
			bslider.addListener(listener);

			hslider.setValue(180f);
			sslider.setValue(50f);
			bslider.setValue(50f);

			getContentTable().add(hlabel).align(Align.left).padTop(10 * s).row();
			getContentTable().add(hslider).expand().fill().row();

			getContentTable().add(slabel).align(Align.left).padTop(5 * s).row();
			getContentTable().add(sslider).expand().fill().row();

			getContentTable().add(blabel).align(Align.left).padTop(5 * s).row();
			getContentTable().add(bslider).expand().fill().padBottom(30 * s);

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{hslider.getValue() / 360f, sslider.getValue() / 100f, bslider.getValue() / 100f};
		}
	}

	public static class InvertDialog extends FilterDialog{

		public InvertDialog(){
			super(Filter.invert, "Invert Image");

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{};
		}
	}

	public static class FlipDialog extends FilterDialog{
		VisCheckBox hbox, vbox;

		public FlipDialog(){
			super(Filter.flip, "Flip Image");

			vbox = new VisCheckBox("Flip Vertically");
			hbox = new VisCheckBox("Flip Horizontally");

			new ButtonGroup<VisCheckBox>(hbox, vbox);

			vbox.setChecked(true);

			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					updatePreview();
				}
			};

			hbox.addListener(listener);
			vbox.addListener(listener);

			hbox.getImageStackCell().size(40 * s);
			vbox.getImageStackCell().size(40 * s);

			Table table = getContentTable();

			table.add(vbox).align(Align.left).row();
			table.add(hbox).align(Align.left).padTop(10 * s).padBottom(10 * s);
			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{vbox.isChecked()};
		}
	}

	public static class RotateDialog extends FilterDialog{
		VisSlider slider;

		public RotateDialog(){
			super(Filter.rotate, "Rotate Image");

			final VisLabel label = new VisLabel("Rotation: 0.0");

			slider = new VisSlider(0, 360, 5f, false);

			slider.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					label.setText("Rotation: " + MiscUtils.limit(slider.getValue() + "", 5));
					updatePreview();
				}
			});

			getContentTable().add(label).align(Align.left).padTop(20 * s).row();
			getContentTable().add(slider).expand().fill().padBottom(30 * s).padTop(5 * s);
			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{slider.getValue()};
		}
	}

	public abstract static class FilterDialog extends MenuDialog{
		private Filter filter;
		ImagePreview preview;

		public FilterDialog(Filter filter, String title){
			super(title);
			this.filter = filter;
			preview = new ImagePreview(PixmapUtils.copy(sourcePixmap()));

			Cell<?> cell = getContentTable().add(preview);
			
			resizeImageCell(cell);
			
			getContentTable().row();
		}

		abstract Object[] getArgs();

		public void updatePreview(){
			filter.apply(sourcePixmap(), pixmap(), getArgs());
			preview.image.updateTexture();
		}

		public final void result(){
			filter.apply(sourcePixmap(), pixmap(), getArgs());
			GUI.gui.drawgrid.canvas.drawPixmap(pixmap());
			pixmap().dispose();
		}

		public Pixmap sourcePixmap(){
			return GUI.gui.drawgrid.canvas.pixmap;
		}

		public Pixmap pixmap(){
			return preview.image.pixmap;
		}
	}

	public static class SymmetryDialog extends MenuDialog{
		VisCheckBox hbox, vbox;

		public SymmetryDialog(){
			super("Edit Symmetry");

			vbox = new VisCheckBox("Vertical Symmetry", GUI.gui.drawgrid.vSymmetry);
			hbox = new VisCheckBox("Horizontal Symmetry", GUI.gui.drawgrid.hSymmetry);

			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					GUI.gui.drawgrid.hSymmetry = hbox.isChecked();
					GUI.gui.drawgrid.vSymmetry = vbox.isChecked();
				}
			};

			hbox.addListener(listener);
			vbox.addListener(listener);

			hbox.getImageStackCell().size(40 * s);
			vbox.getImageStackCell().size(40 * s);

			Table table = getContentTable();

			table.add(vbox).align(Align.left).row();
			table.add(hbox).align(Align.left).padTop(10 * s).padBottom(10 * s);
		}

		Object[] getArgs(){
			return null;
		}
	}

	public static class ScaleDialog extends MenuDialog{
		VisTextField widthfield, heightfield, xscalefield, yscalefield;

		public ScaleDialog(){
			super("Scale Image");

			final float aspectRatio = (float)GUI.gui.drawgrid.canvas.width() / GUI.gui.drawgrid.canvas.height();

			widthfield = new VisTextField(GUI.gui.drawgrid.canvas.width() + "");
			heightfield = new VisTextField(GUI.gui.drawgrid.canvas.height() + "");
			
			widthfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
			heightfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());


			xscalefield = new VisTextField("1");
			yscalefield = new VisTextField("1");
			
			xscalefield.setTextFieldFilter(new FloatFilter());
			yscalefield.setTextFieldFilter(new FloatFilter());


			final VisCheckBox box = new VisCheckBox("Keep Aspect Ratio", true);

			box.getImageStackCell().size(40);

			box.addListener(new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					heightfield.setDisabled(box.isChecked());
					yscalefield.setDisabled(box.isChecked());
				}
			});

			box.fire(new ChangeListener.ChangeEvent());

			ChangeListener sizeClickListener = new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					VisTextField field = (VisTextField)actor;

					int value = Integer.parseInt(field.getText());

					if(box.isChecked()){
						if(field == widthfield){
							heightfield.setText((int)(value / aspectRatio) + "");
						}else{
							widthfield.setText((int)(value * aspectRatio) + "");
						}
					}

					float xscl = (float)Integer.parseInt(widthfield.getText()) / GUI.gui.drawgrid.canvas.width();
					float yscl = (float)Integer.parseInt(heightfield.getText()) / GUI.gui.drawgrid.canvas.height();

					xscalefield.setText(MiscUtils.displayFloat(xscl));
					yscalefield.setText(MiscUtils.displayFloat(yscl));

				}
			};

			ChangeListener scaleClickListener = new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					VisTextField field = (VisTextField)actor;
					
					if(field.getText().isEmpty() || field.getText().equals(".")) return;
					
					float value = Float.parseFloat(field.getText());

					if(box.isChecked()){
						if(field == xscalefield){
							yscalefield.setText(MiscUtils.displayFloat(value));
						}else{
							xscalefield.setText(MiscUtils.displayFloat(value));
						}
					}

					int width = (int)(Float.parseFloat(xscalefield.getText()) * GUI.gui.drawgrid.canvas.width());
					int height = (int)(Float.parseFloat(yscalefield.getText()) * GUI.gui.drawgrid.canvas.height());

					widthfield.setText(width + "");
					heightfield.setText(height + "");
				}
			};

			widthfield.addListener(sizeClickListener);
			heightfield.addListener(sizeClickListener);

			yscalefield.addListener(scaleClickListener);
			xscalefield.addListener(scaleClickListener);

			Table table = getContentTable();

			float width = 135 * s, height = 55 * s, pad = 30 * s, right = 80f * s;

			table.add().height(30f * s);
			table.row();

			table.add(new VisLabel("Size: "));
			table.add(widthfield).size(width, height);
			table.add(new VisLabel("x"));
			table.add(heightfield).size(width, height).padRight(right);

			table.row();

			table.add(new VisLabel("Scale: ")).padTop(pad);
			table.add(xscalefield).size(width, height).padTop(pad);
			table.add(new VisLabel("x")).padTop(pad);
			table.add(yscalefield).size(width, height).padTop(pad).padRight(right);

			table.row();
			table.add(box).colspan(4).padTop(15f * s);

			table.row();
			table.add().height(30f * s);
		}

		public void result(){
			try{
				float xscale = Float.parseFloat(xscalefield.getText());
				float yscale = Float.parseFloat(yscalefield.getText());

				PixelCanvas canvas = new PixelCanvas(PixmapUtils.scale(GUI.gui.drawgrid.canvas.pixmap, xscale, yscale));

				GUI.gui.drawgrid.setCanvas(canvas);
				GUI.gui.updateToolColor();
			}catch(Exception e){
				e.printStackTrace();
				AndroidDialogs.showError(getStage(), e);
			}
		}
	}

	public static class ShiftDialog extends MenuDialog{
		ShiftImagePreview preview;
		
		public ShiftDialog(){
			super("Shift Image");
			
			preview = new ShiftImagePreview();

			VisTable table = new VisTable();
			table.setClip(true);
			table.add(preview).grow().pad(2);
			
			Cell<?> cell = getContentTable().add(table);
			
			resizeImageCell(cell);
			
			getContentTable().row();
		}
		
		static class ShiftController extends Group{
			VisImage left, right, up, down;
			
			public ShiftController(){
				left = new VisImage(Textures.getDrawable("icon-arrow-left"));
				right = new VisImage(Textures.getDrawable("icon-arrow-right"));
				up = new VisImage(Textures.getDrawable("icon-arrow-up"));
				down = new VisImage(Textures.getDrawable("icon-arrow-down"));
				
				final Color color = Color.PURPLE;
				
				up.setColor(color);
				down.setColor(color);
				left.setColor(color);
				right.setColor(color);
				
				float size = 70;

				left.setSize(size, size);
				right.setSize(size, size);
				up.setSize(size, size);
				down.setSize(size, size);
				
				
				InputListener colorlistener = new InputListener(){
					public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
						event.getTarget().setColor(Color.CORAL);
						return true;
					}
					
					public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
						event.getTarget().setColor(color);
					}
				};
				
				up.addListener(colorlistener);
				down.addListener(colorlistener);
				left.addListener(colorlistener);
				right.addListener(colorlistener);
				
				up.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(0, 1);
					}
				});
				down.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(0, -1);
					}
				});
				left.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(-1, 0);
					}
				});
				right.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(1, 0);
					}
				});
				
				addActor(left);
				addActor(right);
				addActor(up);
				addActor(down);
			}
			
			public void draw(Batch batch, float alpha){
				super.draw(batch, alpha);
				float centerx = getX() + getWidth()/2f;
				float centery = getY() + getHeight()/2f;
				
				float hwidth = getWidth()/2.5f;
				float hheight = getHeight()/2.5f;
				
				up.setPosition(centerx, centery + hheight, Align.center);
				down.setPosition(centerx, centery - hheight, Align.center);
				left.setPosition(centerx - hwidth, centery, Align.center);
				right.setPosition(centerx + hwidth, centery, Align.center);
				
				
			}
			
			public void shifted(int x, int y){
				
			}
		}
		//note: call back, d
		
		class ShiftImagePreview extends Group{
			Stack stack;
			ShiftedImage image;
			ShiftController controller;
			
			public ShiftImagePreview(){
				stack = new Stack();
				
				
				AlphaImage alpha = new AlphaImage(GUI.gui.drawgrid.canvas.width(), GUI.gui.drawgrid.canvas.height());
				GridImage grid = new GridImage(GUI.gui.drawgrid.canvas.width(), GUI.gui.drawgrid.canvas.height());
				image = new ShiftedImage(GUI.gui.drawgrid.canvas.texture);
				controller = new ShiftController(){
					public void shifted(int x, int y){
						image.offsetx += x;
						image.offsety += y;
					}
				};

				stack.add(alpha);
				stack.add(image);

				if(GUI.gui.drawgrid.grid)stack.add(grid);
				

				stack.add(controller);
				
				addActor(stack);
			}
			
			public void draw(Batch batch, float alpha){
				super.draw(batch, alpha);
				stack.setBounds(0, 0, getWidth(), getHeight());
				
				Color color = Color.CORAL.cpy();
				color.a = alpha;
				batch.setColor(color);
				MiscUtils.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), 2, 2);
				batch.setColor(Color.WHITE);
			}
		}
		
		public void result(){
			PixelCanvas canvas = GUI.gui.drawgrid.canvas;
			Pixmap temp = PixmapUtils.copy(canvas.pixmap);
			
			int offsetx= preview.image.offsetx, offsety = preview.image.offsety;
			
			Pixmap.setBlending(Blending.None);
			for(int x = 0;x < canvas.width();x ++){
				for(int y = 0;y < canvas.height();y ++){
					if(x >= canvas.width() + offsetx || y >= canvas.height() + offsety || x < offsetx || y < offsety){
						canvas.drawPixelBlendless(x, y, 0);
					}
					
					canvas.drawPixelBlendless(x + offsetx, y + offsety, temp.getPixel(x, temp.getHeight() - 1 - y));
				}
			}
			Pixmap.setBlending(Blending.SourceOver);
			
			canvas.updateAndPush();
		}
	}
	
	public static class CropDialog extends MenuDialog{
		CropImagePreview preview;
		
		public CropDialog(){
			super("Crop Image");
			Cell<?> cell = getContentTable().add((preview = new CropImagePreview()));
			
			resizeImageCell(cell);
			
			//float pad = 20;
			
			//cell.padBottom(cell.getPadBottom()+pad);
			//cell.padLeft(cell.getPadLeft()+pad);
			//cell.padRight(cell.getPadRight()+pad);
			//cell.padTop(cell.getPadTop()+pad);
			
			getContentTable().row();
		}
		
		
		static class CropImagePreview extends ImagePreview{
			CropController controller;
			
			public CropImagePreview(){
				super(GUI.gui.drawgrid.canvas.pixmap);
				stack.add((controller = new CropController(this)));
				
			}
		
		}
		
		static class CropController extends Actor{
			private float xscale, yscale;
			Vector2[] points = {new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2()};
			int selx1, sely1, selx2 = 10, sely2 = 10;
			CropImagePreview preview;
			int point = 0;
			
			public CropController(final CropImagePreview preview){
				this.preview = preview;
				
				addListener(new InputListener(){
					
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
						for(int i = 0; i < 8; i ++){
							if(points[i].dst(x, y) < 30){
								point = i;
								touch(x,y);
								return true;
							}
						}
						
						
						
						return false;
					}
					
					public void touchUp(InputEvent event, float x, float y, int pointer, int button){
						point = -1;
					}
					
					public void touchDragged (InputEvent event, float x, float y, int pointer) {
						touch(x,y);
					}
					
					void touch(float x, float y){
						points[point].set(x,y);
						
						//0 is bottom left, 1 is bottom right, 2 is  top left, 3 is top right
						// 4 is left, 5 is bottom, 6 is right, 7 is top
						
						Vector2 vector = points[point];
						
						vector.x = MiscUtils.clamp(vector.x, getX(), getX() + getWidth());
						vector.y = MiscUtils.clamp(vector.y, getY(), getY() + getHeight());
						
						if(point == 4){
							points[0].x = vector.x;
							points[2].x = vector.x;
						}else if(point == 5){
							points[0].y = vector.y;
							points[1].y = vector.y;
						}else if(point == 6){
							points[1].x = vector.x;
							points[3].x = vector.x;
						}else if(point == 7){
							points[3].y = vector.y;
							points[2].y = vector.y;
						}
							
						if(point == 1){
							points[0].y = vector.y;
							points[3].x = vector.x;
						}else if(point == 2){
							points[0].x = vector.x;
							points[3].y = vector.y;
						}
						
						selx1 = (int)((points[0].x - getX())/xscale+0.5f);
						sely1 = (int)((points[0].y - getY())/yscale+0.5f);
						selx2 = (int)((points[3].x - getX())/xscale+0.5f);
						sely2 = (int)((points[3].y - getY())/yscale+0.5f);
					}
				});
			}
			
			public void draw(Batch batch, float alpha){
				xscale = preview.getWidth() / preview.image.pixmap.getWidth();
				yscale = preview.getHeight() / preview.image.pixmap.getHeight();
				updatePoints();

				TextureRegion region = VisUI.getSkin().getAtlas().findRegion("white");
				
				int s = 1;
				
				if(selx1 > selx2 && sely1 > sely2) s = -1;
				
				batch.setColor(0,0,0,0.5f);
				MiscUtils.setBatchAlpha(batch, alpha);
				
				MiscUtils.drawMasked(batch, region, getX(), getY(), getWidth(), getHeight(), getX() + selx1*xscale, getY() + sely1*yscale, (selx2-selx1)*xscale, (sely2-sely1)*yscale);
					
				batch.setColor(Color.PURPLE);
				MiscUtils.setBatchAlpha(batch, alpha);
				
				MiscUtils.drawBorder(batch, getX() + selx1*xscale, getY() + sely1*yscale, (selx2-selx1)*xscale, (sely2-sely1)*yscale, 2*s, s*4);
				
				Color color = Color.CORAL;
				Color select = Color.PURPLE;
				
				int size = 40;
				
				for(int i = 0; i < 8; i++){
					batch.setColor(point == i ? select : color);
					MiscUtils.setBatchAlpha(batch, alpha);
					//MiscUtils.drawBorder(batch, points[i].x - size/2,  points[i].y - size/2, size, size, 2);
					batch.draw(region, points[i].x - size/2,  points[i].y - size/2, size, size);
				
				}
			}
			
			void updatePoints(){
				float width =  (selx2-selx1)*xscale, height = (sely2-sely1)*yscale;
				
				//0 is bottom left, 1 is bottom right, 2 is  top left, 3 is top right
				// 4 is left, 5 is bottom, 6 is right, 7 is top
				points[0].set(getX() + selx1*xscale,  getY() + sely1*yscale);
				points[1].set(getX() + selx1*xscale+width,  getY() + sely1*yscale);
				points[2].set(getX() + selx1*xscale,  getY() + sely1*yscale+height);
				points[3].set( getX() + selx1*xscale+width,  getY() + sely1*yscale+height);  
				points[4].set(points[0].cpy().add(points[2]).scl(0.5f));
				points[5].set(points[0].cpy().add(points[1]).scl(0.5f));
				points[6].set(points[1].cpy().add(points[3]).scl(0.5f));
				points[7].set(points[3].cpy().add(points[2]).scl(0.5f));
			}
		}
	}

	public static class ClearDialog extends MenuDialog{
		public ClearDialog(){
			super("Confirm Clear Image");

			VisLabel label = new VisLabel("Are you sure you want\nto clear the image?");

			label.setAlignment(Align.center);
			getContentTable().center().add(label).pad(40 * s).align(Align.center);
		}

		public void result(){
			PixelCanvas canvas = GUI.gui.drawgrid.canvas;
			float alpha = canvas.getAlpha();

			canvas.setAlpha(1f);

			for(int x = 0;x < canvas.width();x ++){
				for(int y = 0;y < canvas.height();y ++){
					canvas.erasePixelFullAlpha(x, y);
				}
			}
			canvas.pushActions();
			canvas.updateTexture();

			canvas.setAlpha(alpha);
		}
	}
	
	public static class InputDialog extends MenuDialog{
		VisTextField field;
		
		public InputDialog(String title, String fieldtext, String text){
			super(title);
			
			field = new VisTextField(fieldtext);
			getContentTable().center().add(new VisLabel(text));
			getContentTable().center().add(field).pad(20*s).padLeft(0f);
		}
		
		public final void result(){
			result(field.getText());
		}
		
		public void result(String string){
			
		}
	}
	
	public static class ConfirmDialog extends MenuDialog{
		
		public ConfirmDialog(String title, String text){
			super(title);
			
			VisLabel label = new VisLabel(text);
			getContentTable().center().add(label).pad(20*s);
		}
	}

	public static abstract class MenuDialog extends VisDialog{
		VisTextButton ok, cancel;
		
		public MenuDialog(String title){
			super(title, "dialog");
			getTitleLabel().setColor(Color.CORAL);
			setMovable(false);
			addCloseButton();
			//getTitleTable().row();
			//getTitleTable().add(new Separator()).expandX().fillX().padTop(3*s);
			
			
			addButtons();
		}

		void addButtons(){
			cancel = new VisTextButton("Cancel");
			ok = new VisTextButton("OK");

			setObject(ok, true);
			setObject(cancel, false);

			getButtonsTable().add(cancel).size(130 * s, 60 * s);
			getButtonsTable().add(ok).size(130 * s, 60 * s);
			
		}

		protected void result(Object object){
			if((Boolean)object == true) result();

		}
		
		public void hide(){
			super.hide();
			Gdx.input.setOnscreenKeyboardVisible(false);
		}
		
		public void close(){
			super.close();
			Gdx.input.setOnscreenKeyboardVisible(false);
		}

		public void result(){

		}
	}
	
	static Cell<? extends Actor> resizeImageCell(Cell<? extends Actor> cell){
		float ratio = GUI.gui.drawgrid.canvas.width() / GUI.gui.drawgrid.canvas.height();

		float isize = 400;
		float width = isize, height = isize / ratio;
		if(height > width){
			height = isize;
			width = isize * ratio;
		}
		float sidePad = (isize - width) / 2f, topPad = (isize - height) / 2f;
		
		return cell.size(width, height).padTop(3 + topPad).padBottom(topPad).padLeft(sidePad + 2).padRight(sidePad + 2);
	}
	
	static class FloatFilter implements VisTextField.TextFieldFilter{
		@Override
		public boolean acceptChar(VisTextField textField, char c){
			
			return Character.isDigit(c) || (c == '.' && !textField.getText().contains("."));
		}
	}
}
