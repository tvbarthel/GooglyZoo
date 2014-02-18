package fr.tvbarthel.attempt.googlyzooapp.model;

import fr.tvbarthel.attempt.googlyzooapp.R;

/**
 * Created by tbarthel on 16/02/14.
 */
public class GooglyPetFactory {

    /**
     * relative values for zebra's eyes position
     */
    private static final float ZEBRA_LEFT_EYE_X = 0.40f;
    private static final float ZEBRA_LEFT_EYE_Y = 0.35f;
    private static final float ZEBRA_RIGHT_EYE_X = 0.60f;
    private static final float ZEBRA_RIGHT_EYE_Y = 0.35f;
    private static final float ZEBRA_EYES_DIAMETER = 35f;
    private static final float ZERBA_BODY_PROPORTION = 0.70f;

    /**
     * relative values for gnu's eyes position
     */
    private static final float GNU_LEFT_EYE_X = 0.40f;
    private static final float GNU_LEFT_EYE_Y = 0.38f;
    private static final float GNU_RIGHT_EYE_X = 0.60f;
    private static final float GNU_RIGHT_EYE_Y = 0.38f;
    private static final float GNU_EYES_DIAMETER = 36f;
    private static final float GNU_BODY_PROPORTION = 0.65f;

    /**
     * relative values for hippo's eyes position
     */
    private static final float HIPPO_LEFT_EYE_X = 0.40f;
    private static final float HIPPO_LEFT_EYE_Y = 0.25f;
    private static final float HIPPO_RIGHT_EYE_X = 0.60f;
    private static final float HIPPO_RIGHT_EYE_Y = 0.25f;
    private static final float HIPPO_EYES_DIAMETER = 38f;
    private static final float HIPPO_BODY_PROPORTION = 0.79f;

    /**
     * Create a Googly zebra
     *
     * @return googly zebra
     */
    public static GooglyPet createGooglyZebra() {
        return new GooglyPet(
                R.drawable.zebra,
                ZEBRA_LEFT_EYE_X,
                ZEBRA_LEFT_EYE_Y,
                ZEBRA_RIGHT_EYE_X,
                ZEBRA_RIGHT_EYE_Y,
                ZEBRA_EYES_DIAMETER,
                ZERBA_BODY_PROPORTION,
                R.string.googly_zebra_name);
    }

    public static GooglyPet createGooglyGnu() {
        return new GooglyPet(
                R.drawable.gnu,
                GNU_LEFT_EYE_X,
                GNU_LEFT_EYE_Y,
                GNU_RIGHT_EYE_X,
                GNU_RIGHT_EYE_Y,
                GNU_EYES_DIAMETER,
                GNU_BODY_PROPORTION,
                R.string.googly_gnu_name);
    }

    public static GooglyPet createGooglyHippo() {
        return new GooglyPet(
                R.drawable.hippo,
                HIPPO_LEFT_EYE_X,
                HIPPO_LEFT_EYE_Y,
                HIPPO_RIGHT_EYE_X,
                HIPPO_RIGHT_EYE_Y,
                HIPPO_EYES_DIAMETER,
                HIPPO_BODY_PROPORTION,
                R.string.googly_hippo_name);
    }
}
