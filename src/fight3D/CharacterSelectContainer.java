package fight3D;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.RWT.RWTLine;
import framework.RWT.RWTSelectionCanvas3D;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.model3D.BaseObject3D;

public class CharacterSelectContainer extends RWTContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8354537837334374243L;

	Game game;
	
	static final int PLAYER_NUM = 2; // 総プレイヤー数
	
	private RWTSelectionCanvas3D objectCanvas;
	private boolean flag = false; // 確認画面のフラグ
	private ArrayList<Character> characters = new ArrayList<Character>();
	private int pn = 0;
	private int character[] = new int[PLAYER_NUM];
	protected RWTLabel activatedLabel = new RWTLabel();
	private RWTLabel s1 = new RWTLabel();
	private RWTLabel s2 = new RWTLabel();
	private RWTLabel m5 = new RWTLabel();
	private RWTLabel m6 = new RWTLabel();
	private RWTLine l1 = new RWTLine();
	private RWTLine l2 = new RWTLine();

	// コンストラクタ
	public CharacterSelectContainer(Game g) {
		game = g;
	}
	
	// フレームに追加してサイズが確定した後に中身を構築する
	public void build(GraphicsConfiguration gc) {
		// 初期化
		removeAll();
		setBackground(Color.BLACK);

		// キャラクターを描画するためのキャンバス(相対的キャンバス)を追加
		objectCanvas = new RWTSelectionCanvas3D();
		objectCanvas.setRelativePosition(0.03f, 0.675f);
		objectCanvas.setRelativeSize(0.25f, 0.3f);
		addCanvas(objectCanvas);
		
        //画面タイトル、戻るアイコン、メッセージエリア・１
        s1.setRelativePosition(0.20f,0.10f);
        s1.setString("「キャラクター選択」");
        s1.setColor(Color.WHITE);
        Font f1 = new Font("",Font.PLAIN,20);
        s1.setFont(f1);
        addWidget(s1);
        
        s2.setRelativePosition(0.025f,0.05f);
        s2.setString("Ｂ：タイトル画面へ");
        s2.setColor(Color.WHITE);
        Font f2 = new Font("",Font.PLAIN,8);
        s2.setFont(f2);
        addWidget(s2);

        m5.setRelativePosition(0.60f,0.05f);
        m5.setString((pn+1) + "Ｐはキャラクターを選択して下さい。");
        m5.setColor(Color.YELLOW);
        addWidget(m5);
        m6.setRelativePosition(0.60f,0.10f);
        m6.setString("Ａ：決定");
        m6.setColor(Color.YELLOW);
        addWidget(m6);
        
        //全キャラクターの数
        int n = CharacterManager.getInstance().getNumberOfCharacters(); //キャラクターの総数
        //全キャラクターの登録
        for(int i=0;i<n;i++){
            characters.add(i, CharacterManager.getInstance().getCharacter(i));
        }
        
        //キャラクターボタンの表示
        for(int i=0;i<n;i++){
            String str = characters.get(i).getName()+ RWTLabel.NEW_PARAGRAPH + characters.get(i).getComment();
            CharacterSelectImageButton cb = new CharacterSelectImageButton(str, characters.get(i).getModel(),activatedLabel,i, this);
            cb.setString(characters.get(i).getName());
            cb.setColor(Color.YELLOW);
            cb.setRelativePosition(0.10f + i % 3 * 0.3f, (i / 3 + 1) * 0.3f);
            addSelectableWidget(cb, i%3, i/3);
        }
        
        //選択キャラクター、メッセージエリア・２
        addWidget(activatedLabel);
        
        //ライン
        l1.setRelativePosition(0.00f, 0.15f, 1.00f, 0.15f);
        l1.setColor(Color.WHITE);
        addWidget(l1);
        
        l2.setRelativePosition(0.00f, 0.65f, 1.00f, 0.65f);
        l2.setColor(Color.WHITE);
        addWidget(l2);
		
		repaint();
	}
	
	public void updateObjectCanvas(BaseObject3D o) {
		if (objectCanvas != null) objectCanvas.setObject(o);
	}

	// キーボードを押した瞬間
	public void keyPressed(RWTVirtualKey key) {
		if (key.getPlayer() == -1) {
			key.assignPlayer(1);
		}
	}

	// キーボードを離した瞬間
	public void keyReleased(RWTVirtualKey key) {
		if (key.getVirtualKey() == RWTVirtualController.UP && pn < PLAYER_NUM)
			cursorMoveUp();
		if (key.getVirtualKey() == RWTVirtualController.DOWN
				&& pn < PLAYER_NUM)
			cursorMoveDown();
		if (key.getVirtualKey() == RWTVirtualController.RIGHT
				&& pn < PLAYER_NUM)
			cursorMoveRight();
		if (key.getVirtualKey() == RWTVirtualController.LEFT
				&& pn < PLAYER_NUM)
			cursorMoveLeft();
		if (key.getVirtualKey() == RWTVirtualController.BUTTON_A) {
			// キャラクター番号を入力
			if (pn < 2) {
				character[pn] = ((CharacterSelectImageButton) getSelectedWidget()).number;
				// 選択したキャラクターの番号
				pn++; // プレイヤーの追加

				m5.setString((pn + 1) + "Ｐはキャラクターを選択して下さい。");
				addWidget(m5);
				
				repaint();
			}

			// 確認画面
			if (pn == PLAYER_NUM) {
				s2.setString("");
				addWidget(s2);
				m5.setString("これでよろしいですか。");
				addWidget(m5);
				m6.setString("Ａ：決定　Ｂ：戻る");
				addWidget(m6);
				
				repaint();

				if (flag == true) {
					// 次の画面へ
					game.goNextGameState();
					game.setPlayerNum(pn);
					game.setCharacter(character);
				}

				flag = true;
			}
		}
		if (key.getVirtualKey() == RWTVirtualController.BUTTON_B) {
			// 確認画面
			if (flag == true) {
				pn = 0; // 入力した情報を初期化

				s2.setString("Ｂ：タイトル画面へ");
				addWidget(s2);
				m5.setString((pn + 1) + "Ｐはキャラクターを選択して下さい。");
				addWidget(m5);
				m6.setString("Ａ：決定");
				addWidget(m6);
				
				repaint();

				flag = false;
			} else {
				game.goPrevGameState();
			}
		}
	}

	// キーボードを押して離す
	public void keyTyped(RWTVirtualKey key) {

	}
}
