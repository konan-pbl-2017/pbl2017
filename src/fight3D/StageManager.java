package fight3D;

import java.util.ArrayList;

import javax.vecmath.Point3d;

import framework.gameMain.AbstractManager;

public class StageManager extends AbstractManager{
	private String filename= "data";
	private static StageManager theInstance = null;
	
	private static String stageFile = "stage.def";

	//ステージのデータタグ
	private static final String NAME_TAG = "Name";
	private static final String MODELFILE_TAG = "ModelFile";
	private static final String BACKGROUNDFILE_TAG = "BackgroundFile";
	private static final String COMMENT_TAG = "Comment";
	private static final String EFFECT_TAG = "Effect";
	private ArrayList<Stage> stageList = new ArrayList<Stage>();
	
	/**
	 *コンストラクタ
	 */
	private StageManager(){
		addSeekFile(stageFile);
		setTag(NAME_TAG);
		setTag(MODELFILE_TAG);
		setTag(BACKGROUNDFILE_TAG);
		setTag(COMMENT_TAG);
		setTag(EFFECT_TAG);
		seek(filename);
	}
	public static StageManager getInstance(){
		if(theInstance == null){
			theInstance = new StageManager();
		}
		return theInstance;
	}
	
	/**
	 * StageのArrayListを返します
	 * @return
	 */
	public ArrayList<Stage> getStageList(){
		return stageList;
	}
	
	/**
	 * 指定されたStageを返します。
	 * @param id　 Stage number
	 * @return
	 */
	public Stage getStage(int id){
		if(id<stageList.size()){
			return stageList.get(id);
		}
		else{
			return null;
		}
	}
	
	/**
	 * Stageの数
	 * @return
	 */
	public int getNumberOfStages(){
		return stageList.size();
	}
	@Override
	protected void create() {
		// TODO Auto-generated method stub
		//
		if(hasData(MODELFILE_TAG)){
			Stage stage = new Stage(getData(MODELFILE_TAG));
			if(hasData(NAME_TAG)){
				stage.setName(getData(NAME_TAG));
			}
			if(hasData(BACKGROUNDFILE_TAG)){
				stage.setBackground(getData(BACKGROUNDFILE_TAG));
			}
			if(hasData(COMMENT_TAG)){
				stage.setComment(getData(COMMENT_TAG));
			}

			stageList.add(stage);
			if(hasData(EFFECT_TAG)) {
				boolean flag = true;
				String[] strings = getData(EFFECT_TAG).split(",");		
				for(int i=0; i<6 ; i++){ 
					if(strings[i].equals("")){
						System.out.println("Error!! EFFECT is no data!!");
						flag = false;
						break;
					}
				}
				if(flag) {
					double x1 = Double.parseDouble(strings[0]);
					double y1 = Double.parseDouble(strings[1]);
					double z1 = Double.parseDouble(strings[2]);
					double x2 = Double.parseDouble(strings[3]);
					double y2 = Double.parseDouble(strings[4]);
					double z2 = Double.parseDouble(strings[5]);

					if(!strings[6].equals("")){
						StageEffect e = new StageEffect();
						e.setEffect(strings[6]);
						e.setPositions(new Point3d(x1, y1, z1), new Point3d(x2, y2, z2));
						stage.addEffect(e);
					}
				}
			}
			else{
				System.out.println("Error!!EFFCT is no data!!");
			}
		}
	
	}
	
}