package fr.tvbarthel.attempt.googlyzooapp.model;

import fr.tvbarthel.attempt.googlyzooapp.R;
import fr.tvbarthel.attempt.googlyzooapp.utils.GooglyPetUtils;

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
    private static final float HIPPO_LEFT_EYE_X = 0.38f;
    private static final float HIPPO_LEFT_EYE_Y = 0.25f;
    private static final float HIPPO_RIGHT_EYE_X = 0.58f;
    private static final float HIPPO_RIGHT_EYE_Y = 0.25f;
    private static final float HIPPO_EYES_DIAMETER = 38f;
    private static final float HIPPO_BODY_PROPORTION = 0.79f;

    /**
     * relative values for bee's eyes position
     */
    private static final float BEE_LEFT_EYE_X = 0.40f;
    private static final float BEE_LEFT_EYE_Y = 0.30f;
    private static final float BEE_RIGHT_EYE_X = 0.60f;
    private static final float BEE_RIGHT_EYE_Y = 0.30f;
    private static final float BEE_EYES_DIAMETER = 38f;
    private static final float BEE_BODY_PROPORTION = 0.74f;

    /**
     * relative values for monkey's eyes position
     */
    private static final float MONKEY_LEFT_EYE_X = 0.425f;
    private static final float MONKEY_LEFT_EYE_Y = 0.30f;
    private static final float MONKEY_RIGHT_EYE_X = 0.58f;
    private static final float MONKEY_RIGHT_EYE_Y = 0.30f;
    private static final float MONKEY_EYES_DIAMETER = 28f;
    private static final float MONKEY_BODY_PROPORTION = 0.74f;

    /**
     * Create a googly pet using pet identifier
     *
     * @param id id from GooglyPetUtils
     * @return
     */
    public static GooglyPet createGooglyPet(int id) {
        final GooglyPet createdPet;
        switch (id) {
            case GooglyPetUtils.GOOGLY_PET_ZEBRA:
                createdPet = createGooglyZebra();
                break;
            case GooglyPetUtils.GOOGLY_PET_GNU:
                createdPet = createGooglyGnu();
                break;
            case GooglyPetUtils.GOOGLY_PET_HIPPO:
                createdPet = createGooglyHippo();
                break;
            case GooglyPetUtils.GOOGLY_PET_BEE:
                createdPet = createGooglyBee();
                break;
            case GooglyPetUtils.GOOGLY_PET_MONKEY:
                createdPet = createGooglyMonkey();
                break;
            default:
                createdPet = createGooglyZebra();
        }
        return createdPet;
    }

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
                ZERBA_BODY_PROPORTION);
    }

    /**
     * Create a Googly Gnu
     *
     * @return googly gnu
     */
    public static GooglyPet createGooglyGnu() {
        return new GooglyPet(
                R.drawable.gnu,
                GNU_LEFT_EYE_X,
                GNU_LEFT_EYE_Y,
                GNU_RIGHT_EYE_X,
                GNU_RIGHT_EYE_Y,
                GNU_EYES_DIAMETER,
                GNU_BODY_PROPORTION);
    }

    /**
     * Create a Googly Hippo
     *
     * @return googly hippo
     */
    public static GooglyPet createGooglyHippo() {
        return new GooglyPet(
                R.drawable.hippo,
                HIPPO_LEFT_EYE_X,
                HIPPO_LEFT_EYE_Y,
                HIPPO_RIGHT_EYE_X,
                HIPPO_RIGHT_EYE_Y,
                HIPPO_EYES_DIAMETER,
                HIPPO_BODY_PROPORTION);
    }

    /**
     * Create a Googly Bee
     *
     * @return googly bee
     */
    public static GooglyPet createGooglyBee() {
        return new GooglyPet(
                R.drawable.bee,
                BEE_LEFT_EYE_X,
                BEE_LEFT_EYE_Y,
                BEE_RIGHT_EYE_X,
                BEE_RIGHT_EYE_Y,
                BEE_EYES_DIAMETER,
                BEE_BODY_PROPORTION);
    }

    /**
     * Create a Googly Monkey
     *
     * @return googly monkey
     */
    public static GooglyPet createGooglyMonkey() {
        return new GooglyPet(
                R.drawable.monkey,
                MONKEY_LEFT_EYE_X,
                MONKEY_LEFT_EYE_Y,
                MONKEY_RIGHT_EYE_X,
                MONKEY_RIGHT_EYE_Y,
                MONKEY_EYES_DIAMETER,
                MONKEY_BODY_PROPORTION);
    }
}
