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
     * colored icon
     */
    private int mPetColoredIc;

    /**
     * constructor for a drawer entry model
     *
     * @param name              pet name res
     * @param whiteAndBlackIcon white and black icon res
     * @param coloredIc         colored icon res
     */
    public GooglyPetEntry(int name, int whiteAndBlackIcon, int coloredIc) {
        mPetName = name;
        mPetBlackAndWhiteIc = whiteAndBlackIcon;
        mPetColoredIc = coloredIc;
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

    public int getColoredIcon() {
        return mPetColoredIc;
    }
}
