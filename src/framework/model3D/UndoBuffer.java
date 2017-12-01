package framework.model3D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;



public class UndoBuffer {

	@SuppressWarnings("unchecked")
	private ArrayList<Hashtable<Class, Property3D>> list = new ArrayList<Hashtable<Class,Property3D>>();
	@SuppressWarnings("unchecked")
	private Hashtable<Class, Property3D> nowCondition = new Hashtable<Class, Property3D>();
	private static final int max = 3;
	
	UndoBuffer() {
	}
	
	UndoBuffer(UndoBuffer another) {
		this.list = (ArrayList<Hashtable<Class, Property3D>>)another.list.clone();
		this.nowCondition = (Hashtable<Class, Property3D>)another.nowCondition.clone();
	}
	
	public void push(Property3D p) {
		nowCondition.put(p.getClass(), p.clone());
	}
	
	@SuppressWarnings("unchecked")
	public void setUndoMark() {
		if(list.size() == max) {
			list.remove(max - 1);
		}
		list.add(0, (Hashtable<Class, Property3D>) nowCondition.clone());
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Property3D> undo() {
		if(list.size() != 0) {
			nowCondition = list.remove(0);
			return nowCondition.values();
		}
		else {
			return new Hashtable<Class, Property3D>().values();
		}
	}
	
	public void clear() {
		
	}
}
