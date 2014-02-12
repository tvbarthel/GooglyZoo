package fr.tvbarthel.attempt.googlyzooapp.listener;

/**
 * Created by tbarthel on 12/02/14.
 */
public interface GooglyEyeListener {

    /**
     * called when eye has just opened
     */
    public void onOpened();

    /**
     * called when eye has just blinked
     */
    public void onBlinked();

    /**
     * called when eye has just closed
     */
    public void onClosed();
}
