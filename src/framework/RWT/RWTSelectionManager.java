package framework.RWT;


public class RWTSelectionManager {

	private int size = 0;
	
	private WidgetHolder selectedHolder = new WidgetHolder(null, 0, 0);
	
	private WidgetHolder upMostHolder;
	private WidgetHolder leftMostHolder;
	
	private RWTSelector selector = new DefaultSelector();
	
	public void add(RWTSelectableWidget widget, int m, int n) {
		WidgetHolder holder = new WidgetHolder(widget, m, n);
		if(size == 0) {
			widget.selected();
			selectedHolder = holder;
			upMostHolder = holder;
			leftMostHolder = holder;
			selector.setSelectableWidget(selectedHolder.getSelectableWidget());
		}
		else {
			//どれくらい下か
			connectY(holder);
			
			//どれくらい右か
			connectX(holder);
		}
		size++;
	}
	
	public void up() {
		if(selectedHolder.hasUp()) {
			selectedHolder.getSelectableWidget().deselected();
			selectedHolder = selectedHolder.getUp();
			selectedHolder.getSelectableWidget().selected();
			selector.setSelectableWidget(selectedHolder.getSelectableWidget());
		}
	}
	
	public void down() {
		if(selectedHolder.hasDown()) {
			selectedHolder.getSelectableWidget().deselected();
			selectedHolder = selectedHolder.getDown();
			selectedHolder.getSelectableWidget().selected();
			selector.setSelectableWidget(selectedHolder.getSelectableWidget());
		}
	}
	
	public void right() {
		if(selectedHolder.hasRight()) {
			selectedHolder.getSelectableWidget().deselected();
			selectedHolder = selectedHolder.getRight();
			selectedHolder.getSelectableWidget().selected();
			selector.setSelectableWidget(selectedHolder.getSelectableWidget());
		}
	}
	
	public void left() {
		if(selectedHolder.hasLeft()) {
			selectedHolder.getSelectableWidget().deselected();
			selectedHolder = selectedHolder.getLeft();
			selectedHolder.getSelectableWidget().selected();
			selector.setSelectableWidget(selectedHolder.getSelectableWidget());
		}
	}
	
	public RWTWidget getSelectedWidget() {
		if(selectedHolder != null) {
			return selectedHolder.getWidget();
		}
		else {
			return null;
		}
	}
	
	public void setSelector(RWTSelector c) {
		selector = c;
	}
	
	public RWTSelector getSelector() {
		return selector;
	}
	
	/**
	 * 左右をつなげる
	 * @param newHolder
	 */
	private void connectX(WidgetHolder newHolder) {
		WidgetHolder holder = leftMostHolder;
		while(true) {
			if(newHolder.getY() < holder.getY()) {
				//holderのほうが左
				if(holder.getLeft() != null) {
					holder.getLeft().setRight(newHolder);
				}
				newHolder.setLeft(holder.getLeft());
				
				holder.setLeft(newHolder);
				newHolder.setRight(holder);
				return;
			}
			else if(newHolder.getY() == holder.getY()) {
				if(newHolder.getX() < holder.getX()) {
					//holderのほうが左
					if(holder.getLeft() != null) {
						holder.getLeft().setRight(newHolder);
					}
					newHolder.setLeft(holder.getLeft());
					
					holder.setLeft(newHolder);
					newHolder.setRight(holder);
					return;
				}
			}
			//まだ右のやつはいる？
			if(holder.hasRight()) {
				holder = holder.getRight();
			}
			//もう右のやつはいない
			else {
				holder.setRight(newHolder);
				newHolder.setLeft(holder);
				return;
			}
		}
	}
	
	/**
	 * 上下をつなげる
	 * @param newHolder
	 */
	private void connectY(WidgetHolder newHolder) {
		WidgetHolder holder = upMostHolder;
		while(true) {
			if(newHolder.getX() < holder.getX()) {
				//holderのほうが左
				if(holder.getUp() != null) {
					holder.getUp().setDown(newHolder);
				}
				newHolder.setUp(holder.getUp());
				
				holder.setUp(newHolder);
				newHolder.setDown(holder);
				return;
			}
			else if(newHolder.getX() == holder.getX()) {
				if(newHolder.getY() < holder.getY()) {
					//holderのほうが左
					if(holder.getUp() != null) {
						holder.getUp().setDown(newHolder);
					}
					newHolder.setUp(holder.getUp());
					
					holder.setUp(newHolder);
					newHolder.setDown(holder);
					return;
				}
			}
			//まだ下のやつはいる？
			if(holder.hasDown()) {
				holder = holder.getDown();
			}
			//もう下のやつはいない
			else {
				holder.setDown(newHolder);
				newHolder.setUp(holder);
				return;
			}
		}
	}
	
	private class WidgetHolder {
		private RWTSelectableWidget widget;
		
		//行
		private int x;
		//列
		private int y;
		
		private WidgetHolder up = null;
		private WidgetHolder down = null;
		private WidgetHolder right = null;
		private WidgetHolder left = null;
		
		public WidgetHolder(RWTSelectableWidget r, int m, int n) {
			widget = r;
			x = m;
			y = n;
		}
		
		//getter
		public RWTWidget getWidget() {
			return (RWTWidget) widget;
		}
		
		public RWTSelectableWidget getSelectableWidget() {
			return widget;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public WidgetHolder getUp() {
			return up;
		}
		public WidgetHolder getDown() {
			return down;
		}
		public WidgetHolder getRight() {
			return right;
		}
		public WidgetHolder getLeft() {
			return left;
		}
		
		//setter
		public void setUp(WidgetHolder d) {
			up = d;
		}
		public void setDown(WidgetHolder d) {
			down = d;
		}
		public void setRight(WidgetHolder d) {
			right = d;
		}
		public void setLeft(WidgetHolder d) {
			left = d;
		}
		
		public boolean hasUp() {
			if(up == null) {
				return false;
			}
			return true;
		}
		
		public boolean hasDown() {
			if(down == null) {
				return false;
			}
			return true;
		}
		
		public boolean hasRight() {
			if(right == null) {
				return false;
			}
			return true;
		}
		
		public boolean hasLeft() {
			if(left == null) {
				return false;
			}
			return true;
		}
	}
}
