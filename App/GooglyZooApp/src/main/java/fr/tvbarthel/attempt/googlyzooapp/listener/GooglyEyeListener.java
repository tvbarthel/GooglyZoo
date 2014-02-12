package fr.tvbarthel.attempt.googlyzooapp.listener;

import fr.tvbarthel.attempt.googlyzooapp.model.GooglyEye;

/**
 * Created by tbarthel on 12/02/14.
 */
public interface GooglyEyeListener {

    /**
     * called when eye has just opened
     */
    public void onOpened(GooglyEye src);

    /**
     * called when eye has just blinked
     */
    public void onBlinked(GooglyEye src);

    /**
     * called when eye has just closed
     */
    public void onClosed(GooglyEye src);
}
