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
     * relative values for elephant's eyes position
     */
    private static final float ELEPHANT_LEFT_EYE_X = 0.41f;
    private static final float ELEPHANT_LEFT_EYE_Y = 0.26f;
    private static final float ELEPHANT_RIGHT_EYE_X = 0.59f;
    private static final float ELEPHANT_RIGHT_EYE_Y = 0.26f;
    private static final float ELEPHANT_EYES_DIAMETER = 38f;
    private static final float ELEPHANT_BODY_PROPORTION = 0.78f;

    /**
     * relative values for cow's eyes position
     */
    private static final float COW_LEFT_EYE_X = 0.39f;
    private static final float COW_LEFT_EYE_Y = 0.26f;
    private static final float COW_RIGHT_EYE_X = 0.61f;
    private static final float COW_RIGHT_EYE_Y = 0.26f;
    private static final float COW_EYES_DIAMETER = 38f;
    private static final float COW_BODY_PROPORTION = 0.78f;

    /**
     * relative values for goat's eyes position
     */
    private static final float GOAT_LEFT_EYE_X = 0.40f;
    private static final float GOAT_LEFT_EYE_Y = 0.37f;
    private static final float GOAT_RIGHT_EYE_X = 0.60f;
    private static final float GOAT_RIGHT_EYE_Y = 0.37f;
    private static final float GOAT_EYES_DIAMETER = 36f;
    private static final float GOAT_BODY_PROPORTION = 0.67f;

    /**
     * relative values for elk's eyes position
     */
    private static final float ELK_LEFT_EYE_X = 0.43f;
    private static final float ELK_LEFT_EYE_Y = 0.40f;
    private static final float ELK_RIGHT_EYE_X = 0.57f;
    private static final float ELK_RIGHT_EYE_Y = 0.40f;
    private static final float ELK_EYES_DIAMETER = 28f;
    private static final float ELK_BODY_PROPORTION = 0.63f;

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
            case GooglyPetUtils.GOOGLY_PET_ELEPHANT:
                createdPet = createGooglyElephant();
                break;
            case GooglyPetUtils.GOOGLY_PET_COW:
                createdPet = createGooglyCow();
                break;
            case GooglyPetUtils.GOOGLY_PET_GOAT:
                createdPet = createGooglyGoat();
                break;
            case GooglyPetUtils.GOOGLY_PET_ELK:
                createdPet = createGooglyElk();
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
     * Create a Googly Elephant
     *
     * @return googly elephant
     */
    public static GooglyPet createGooglyElephant() {
        return new GooglyPet(
                R.drawable.elephant,
                ELEPHANT_LEFT_EYE_X,
                ELEPHANT_LEFT_EYE_Y,
                ELEPHANT_RIGHT_EYE_X,
                ELEPHANT_RIGHT_EYE_Y,
                ELEPHANT_EYES_DIAMETER,
                ELEPHANT_BODY_PROPORTION);
    }

    /**
     * Create a Googly Cow
     *
     * @return googly cow
     */
    public static GooglyPet createGooglyCow() {
        return new GooglyPet(
                R.drawable.cow,
                COW_LEFT_EYE_X,
                COW_LEFT_EYE_Y,
                COW_RIGHT_EYE_X,
                COW_RIGHT_EYE_Y,
                COW_EYES_DIAMETER,
                COW_BODY_PROPORTION);
    }

    /**
     * Create a Googly Goat
     *
     * @return googly goat
     */
    public static GooglyPet createGooglyGoat() {
        return new GooglyPet(
                R.drawable.goat,
                GOAT_LEFT_EYE_X,
                GOAT_LEFT_EYE_Y,
                GOAT_RIGHT_EYE_X,
                GOAT_RIGHT_EYE_Y,
                GOAT_EYES_DIAMETER,
                GOAT_BODY_PROPORTION);
    }

    /**
     * Create a Googly Elk
     *
     * @return googly elk
     */
    public static GooglyPet createGooglyElk() {
        return new GooglyPet(
                R.drawable.elk,
                ELK_LEFT_EYE_X,
                ELK_LEFT_EYE_Y,
                ELK_RIGHT_EYE_X,
                ELK_RIGHT_EYE_Y,
                ELK_EYES_DIAMETER,
                ELK_BODY_PROPORTION);
    }
}
