package ufscar.tacomfome.tacomfome.interfaces;

/**
 * Created by Gabriel on 22/11/2017.
 */

public interface Subject {
    void addObserver(Observer observer);
    void notifyObservers();
    int getSpinnerPosition();
}
