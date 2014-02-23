package fr.tvbarthel.attempt.googlyzooapp.model;

/**
 * Created by tbarthel on 20/02/14.
 * class used as model for drawer entry
 */
public class GooglyPetEntry {

    /**
     * Pet id
     */
    private int mPetId;

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
     * use to know if the entry is selected or not
     */
    private boolean mIsSelected;

    /**
     * constructor for a drawer entry model
     *
     * @param name              pet name res
     * @param whiteAndBlackIcon white and black icon res
     * @param coloredIc         colored icon res
     * @param petId             pet id from GooglyPetUtils
     */
    public GooglyPetEntry(int name, int whiteAndBlackIcon, int coloredIc, int petId) {
        mPetName = name;
        mPetBlackAndWhiteIc = whiteAndBlackIcon;
        mPetColoredIc = coloredIc;
        mPetId = petId;
        mIsSelected = false;
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

    public int getPetId() {
        return mPetId;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void selected(boolean selected) {
        mIsSelected = selected;
    }
}
