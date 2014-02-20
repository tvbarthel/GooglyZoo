package fr.tvbarthel.attempt.googlyzooapp.model;

/**
 * Created by tbarthel on 20/02/14.
 * class used as model for drawer entry
 */
public class GooglyPetEntry {

    /**
     * Pet name res
     */
    private int mPetName;

    /**
     * black and white icon res
     */
    private int mPetBlackAndWhiteIc;


    /**
     * constructor for a drawer entry model
     *
     * @param name              pet name res
     * @param whiteAndBlackIcon white and black icon res
     */
    public GooglyPetEntry(int name, int whiteAndBlackIcon) {
        mPetName = name;
        mPetBlackAndWhiteIc = whiteAndBlackIcon;
    }

    /**
     * GETTER & SETTER
     */

    public int getName() {
        return mPetName;
    }

    public int getBlackAndWhiteIcon() {
        return mPetBlackAndWhiteIc;
    }

}
