package ufscar.tacomfome.tacomfome.interfaces;

/**
 * Created by Gabriel on 22/11/2017.
 */

public interface Observer {
    public void update(int position);
    public void addSubject(Subject subject);
}
