package fr.tvbarthel.attempt.googlyzooapp.listener;

/**
 * Created by tbarthel on 13/02/14.
 */
public interface GooglyPetListener {

    /**
     * called when pet opens both eyes
     */
    public void onAwake();

    /**
     * called when pet closes both eyes
     */
    public void onFallAsleep();
}
