package framework.gameMain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractManager {

	private DataBox data = new DataBox();
	private BufferedReader reader = null;
	private String seekFilename;
	
	protected void addSeekFile(String filename) {
		seekFilename = filename;
	}
	
	protected void seek(String d) {
		File file = new File(d);
		//ディレクトリのとき
		if(file.isDirectory()) {
			openDirectory(file.listFiles());
		}
		//ファイルのとき
		else {
			openFile(file);
		}
	}
	
	//ディレクトリをあける
	private void openDirectory(File[] fileList) {
		File file;
		for(int i = 0; i < fileList.length; i++) {
			file = fileList[i];
			//ディレクトリのとき
			if(file.isDirectory()) {
				openDirectory(file.listFiles());
			}
			//ファイルのとき
			else {
				openFile(file);
			}
		}
	}
	//ファイルをあける
	private void openFile(File file) {
		if(file.getName().equals(seekFilename)) {
			data.removeAll();
			
			try {
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			findFile(file);
			
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * setBufferedReader(File file)をすること
	 */
	protected String readLine() {
		if(reader != null) {
			try {
				return reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected void setTags(ArrayList<String> tags) {
		for(int i = 0; i < tags.size(); i++) {
			data.setTag(tags.get(i));
		}
	}
	protected void setTag(String tag) {
		data.setTag(tag);
	}
	
	protected void findFile(File file) {
		String str;
		while(true) {
			str = readLine();
			if(str == null) {
				break;
			}
			String[] strings = str.split(":");
			if(strings.length >= 2) {
				data.setData(strings[0], strings[1]);
			}
		}
		create();
	}
	
	protected String getData(String tag) {
		return data.getData(tag);
	}
	
	public boolean hasData(String tag){
		if(data.getData(tag) == null)
			return false;
		else if(data.getData(tag) == "")
			return false;
		else return true;
	}
	
	abstract protected void create();
	
	private class DataBox {
		private ArrayList<Data> dataList = new ArrayList<Data>();
		
		public void setTag(String tag) {
			dataList.add(new Data(tag));
		}
		
		public void setData(String tag, String data) {
			for(int i = 0; i < dataList.size(); i++) {
				if(dataList.get(i).getTag().equals(tag)) {
					dataList.get(i).addData(data);
				}
			}
		}
		
		/**
		 * このkeyのデータを返す。
		 * @param key タグ名
		 * @return
		 */
		public String getData(String tag) {
			for(int i = 0; i < dataList.size(); i++) {
				if(dataList.get(i).getTag().equals(tag)) {
					if(dataList.get(i).getData().size() > 0) {
						return dataList.get(i).getData().get(0);
					}
					else {
						break;
					}
				}
			}
			return "";
		}
		
		public ArrayList<String> getDatas(String tag) {
			for(int i = 0; i < dataList.size(); i++) {
				if(dataList.get(i).getTag().equals(tag)) {
					return dataList.get(i).getData();
				}
			}
			return new ArrayList<String>();
		}
		
		/**
		 * このタグが存在するか。
		 * @param tag
		 * @return
		 */
		public boolean isExist(String tag) {
			for(int i = 0; i < dataList.size(); i++) {
				if(dataList.get(i).getTag().equals(tag)) {
					return true;
				}
			}
			return false;
		}
		
		public void removeAll() {
			for(int i = 0; i < dataList.size(); i++) {
				dataList.get(i).removeData();
			}
		}
	}
	
	private class Data {
		private String tag;
		private ArrayList<String> data = new ArrayList<String>();
		
		public Data(String tag) {
			this.tag = tag;
		}
		
		public String getTag() {
			return tag;
		}
		
		public void addData(String d) {
			data.add(d);
		}
		
		public ArrayList<String> getData() {
			return data;
		}
		
		public void removeData() {
			data = new ArrayList<String>();
		}
	}
}
