package fight3D;

import java.awt.Color;

import framework.RWT.RWTLabel;
import framework.RWT.RWTSelectableWidget;
import framework.model3D.Model3D;

public class CharacterSelectImageButton extends RWTLabel implements RWTSelectableWidget {
	private RWTLabel label;
	protected int number = 0;
	private String comment;
	CharacterSelectContainer container;
	Model3D model;

	public CharacterSelectImageButton(String str, Model3D m, RWTLabel activeLabel,
			int i, CharacterSelectContainer c) {
		comment = str;
		model = m;
		label = activeLabel;
		number = i;
		container = c;
	}

	@Override
	public void selected() {
		// アクティブなキャラクタの名前や説明を表示
		label.setString(comment);
		label.setColor(Color.WHITE);
		label.setRelativePosition(0.35f, 0.75f);

		// アクティブなキャラクタのオブジェクトを設定
		container.updateObjectCanvas(model.createObject());
	}
	
	@Override
	public void deselected() {		
	}

	@Override
	public int getAbsoluteHeight() {
		return super.getAbsoluteHeight();
	}

	@Override
	public int getAbsoluteWidth() {
		return super.getAbsoluteWidth();
	}

	@Override
	public int getAbsoluteX() {
		return super.getAbsoluteX();
	}

	@Override
	public int getAbsoluteY() {
		return super.getAbsoluteY();
	}
}
