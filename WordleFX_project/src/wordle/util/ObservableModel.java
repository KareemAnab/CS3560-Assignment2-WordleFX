
package wordle.util; import java.util.*;
public abstract class ObservableModel {
    private final List<ModelListener> listeners=new ArrayList<>();
    public void addListener(ModelListener l){listeners.add(l);} public void removeListener(ModelListener l){listeners.remove(l);}
    protected void notifyListeners(){ for(ModelListener l: listeners) l.onModelChanged(); }
}
