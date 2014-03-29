package fr.tvbarthel.attempt.googlyzooapp.model;

import android.content.Context;

import fr.tvbarthel.attempt.googlyzooapp.R;
import fr.tvbarthel.attempt.googlyzooapp.utils.GooglyPetUtils;

/**
 * Created by tbarthel on 16/02/14.
 */
public final class GooglyPetFactory {

    /**
     * relative values for zebra's eyes position
     */
    private static final float ZEBRA_LEFT_EYE_X = 0.40f;
    private static final float ZEBRA_LEFT_EYE_Y = 0.35f;
    private static final float ZEBRA_RIGHT_EYE_X = 0.60f;
    private static final float ZEBRA_RIGHT_EYE_Y = 0.35f;
    private static final float ZERBA_BODY_PROPORTION = 0.70f;

    /**
     * relative values for gnu's eyes position
     */
    private static final float GNU_LEFT_EYE_X = 0.40f;
    private static final float GNU_LEFT_EYE_Y = 0.38f;
    private static final float GNU_RIGHT_EYE_X = 0.60f;
    private static final float GNU_RIGHT_EYE_Y = 0.38f;
    private static final float GNU_BODY_PROPORTION = 0.65f;

    /**
     * relative values for hippo's eyes position
     */
    private static final float HIPPO_LEFT_EYE_X = 0.38f;
    private static final float HIPPO_LEFT_EYE_Y = 0.25f;
    private static final float HIPPO_RIGHT_EYE_X = 0.58f;
    private static final float HIPPO_RIGHT_EYE_Y = 0.25f;
    private static final float HIPPO_BODY_PROPORTION = 0.79f;

    /**
     * relative values for bee's eyes position
     */
    private static final float BEE_LEFT_EYE_X = 0.40f;
    private static final float BEE_LEFT_EYE_Y = 0.30f;
    private static final float BEE_RIGHT_EYE_X = 0.60f;
    private static final float BEE_RIGHT_EYE_Y = 0.30f;
    private static final float BEE_BODY_PROPORTION = 0.74f;

    /**
     * relative values for elephant's eyes position
     */
    private static final float ELEPHANT_LEFT_EYE_X = 0.41f;
    private static final float ELEPHANT_LEFT_EYE_Y = 0.26f;
    private static final float ELEPHANT_RIGHT_EYE_X = 0.59f;
    private static final float ELEPHANT_RIGHT_EYE_Y = 0.26f;
    private static final float ELEPHANT_BODY_PROPORTION = 0.78f;

    /**
     * relative values for cow's eyes position
     */
    private static final float COW_LEFT_EYE_X = 0.39f;
    private static final float COW_LEFT_EYE_Y = 0.26f;
    private static final float COW_RIGHT_EYE_X = 0.61f;
    private static final float COW_RIGHT_EYE_Y = 0.26f;
    private static final float COW_BODY_PROPORTION = 0.78f;

    /**
     * relative values for goat's eyes position
     */
    private static final float GOAT_LEFT_EYE_X = 0.40f;
    private static final float GOAT_LEFT_EYE_Y = 0.37f;
    private static final float GOAT_RIGHT_EYE_X = 0.60f;
    private static final float GOAT_RIGHT_EYE_Y = 0.37f;
    private static final float GOAT_BODY_PROPORTION = 0.67f;

    /**
     * relative values for elk's eyes position
     */
    private static final float ELK_LEFT_EYE_X = 0.43f;
    private static final float ELK_LEFT_EYE_Y = 0.40f;
    private static final float ELK_RIGHT_EYE_X = 0.57f;
    private static final float ELK_RIGHT_EYE_Y = 0.40f;
    private static final float ELK_BODY_PROPORTION = 0.63f;

    /**
     * relative values for horse's eyes position
     */
    private static final float HORSE_LEFT_EYE_X = 0.39f;
    private static final float HORSE_LEFT_EYE_Y = 0.34f;
    private static final float HORSE_RIGHT_EYE_X = 0.605f;
    private static final float HORSE_RIGHT_EYE_Y = 0.34f;
    private static final float HORSE_BODY_PROPORTION = 0.70f;

    /**
     * relative values for pig's eyes position
     */
    private static final float PIG_LEFT_EYE_X = 0.38f;
    private static final float PIG_LEFT_EYE_Y = 0.23f;
    private static final float PIG_RIGHT_EYE_X = 0.61f;
    private static final float PIG_RIGHT_EYE_Y = 0.23f;
    private static final float PIG_BODY_PROPORTION = 0.80f;

    /**
     * relative values for triceratops' eyes position
     */
    private static final float TRICERATOPS_LEFT_EYE_X = 0.37f;
    private static final float TRICERATOPS_LEFT_EYE_Y = 0.36f;
    private static final float TRICERATOPS_RIGHT_EYE_X = 0.62f;
    private static final float TRICERATOPS_RIGHT_EYE_Y = 0.36f;
    private static final float TRICERATOPS_BODY_PROPORTION = 0.67f;

    /**
     * relative values for tbarthel's eyes position
     */
    private static final float TBARTHEL_LEFT_EYE_X = 0.385f;
    private static final float TBARTHEL_LEFT_EYE_Y = 0.42f;
    private static final float TBARTHEL_RIGHT_EYE_X = 0.60f;
    private static final float TBARTHEL_RIGHT_EYE_Y = 0.42f;
    private static final float TBARTHEL_BODY_PROPORTION = 0.80f;

    /**
     * relative values for vbarthel's eyes position
     */
    private static final float VBARTHEL_LEFT_EYE_X = 0.385f;
    private static final float VBARTHEL_LEFT_EYE_Y = 0.42f;
    private static final float VBARTHEL_RIGHT_EYE_X = 0.60f;
    private static final float VBARTHEL_RIGHT_EYE_Y = 0.42f;
    private static final float VBARTHEL_BODY_PROPORTION = 0.80f;


    /**
     * Create a googly pet using pet identifier
     *
     * @param id id from GooglyPetUtils
     * @return
     */
    public static GooglyPet createGooglyPet(int id, Context context) {
        final GooglyPet createdPet;
        switch (id) {
            case GooglyPetUtils.GOOGLY_PET_ZEBRA:
                createdPet = createGooglyZebra(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_GNU:
                createdPet = createGooglyGnu(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_HIPPO:
                createdPet = createGooglyHippo(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_BEE:
                createdPet = createGooglyBee(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_ELEPHANT:
                createdPet = createGooglyElephant(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_COW:
                createdPet = createGooglyCow(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_GOAT:
                createdPet = createGooglyGoat(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_ELK:
                createdPet = createGooglyElk(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_HORSE:
                createdPet = createGooglyHorse(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_PIG:
                createdPet = createGooglyPig(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_TRICERATOPS:
                createdPet = createGooglyTriceratops(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_TBARTHEL:
                createdPet = createGooglyTbarhel(context);
                break;
            case GooglyPetUtils.GOOGLY_PET_VBARTHEL:
                createdPet = createGooglyVbarhel(context);
                break;
            default:
                createdPet = createGooglyZebra(context);
        }
        return createdPet;
    }

    /**
     * Create a Googly zebra
     *
     * @return googly zebra
     */
    public static GooglyPet createGooglyZebra(Context context) {
        return new GooglyPet(
                R.drawable.zebra,
                ZEBRA_LEFT_EYE_X,
                ZEBRA_LEFT_EYE_Y,
                ZEBRA_RIGHT_EYE_X,
                ZEBRA_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_zebra),
                ZERBA_BODY_PROPORTION);
    }

    /**
     * Create a Googly Gnu
     *
     * @return googly gnu
     */
    public static GooglyPet createGooglyGnu(Context context) {
        return new GooglyPet(
                R.drawable.gnu,
                GNU_LEFT_EYE_X,
                GNU_LEFT_EYE_Y,
                GNU_RIGHT_EYE_X,
                GNU_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_gnu),
                GNU_BODY_PROPORTION);
    }

    /**
     * Create a Googly Hippo
     *
     * @return googly hippo
     */
    public static GooglyPet createGooglyHippo(Context context) {
        return new GooglyPet(
                R.drawable.hippo,
                HIPPO_LEFT_EYE_X,
                HIPPO_LEFT_EYE_Y,
                HIPPO_RIGHT_EYE_X,
                HIPPO_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_hippo),
                HIPPO_BODY_PROPORTION);
    }

    /**
     * Create a Googly Bee
     *
     * @return googly bee
     */
    public static GooglyPet createGooglyBee(Context context) {
        return new GooglyPet(
                R.drawable.bee,
                BEE_LEFT_EYE_X,
                BEE_LEFT_EYE_Y,
                BEE_RIGHT_EYE_X,
                BEE_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_bee),
                BEE_BODY_PROPORTION);
    }

    /**
     * Create a Googly Elephant
     *
     * @return googly elephant
     */
    public static GooglyPet createGooglyElephant(Context context) {
        return new GooglyPet(
                R.drawable.elephant,
                ELEPHANT_LEFT_EYE_X,
                ELEPHANT_LEFT_EYE_Y,
                ELEPHANT_RIGHT_EYE_X,
                ELEPHANT_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_elephant),
                ELEPHANT_BODY_PROPORTION);
    }

    /**
     * Create a Googly Cow
     *
     * @return googly cow
     */
    public static GooglyPet createGooglyCow(Context context) {
        return new GooglyPet(
                R.drawable.cow,
                COW_LEFT_EYE_X,
                COW_LEFT_EYE_Y,
                COW_RIGHT_EYE_X,
                COW_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_cow),
                COW_BODY_PROPORTION);
    }

    /**
     * Create a Googly Goat
     *
     * @return googly goat
     */
    public static GooglyPet createGooglyGoat(Context context) {
        return new GooglyPet(
                R.drawable.goat,
                GOAT_LEFT_EYE_X,
                GOAT_LEFT_EYE_Y,
                GOAT_RIGHT_EYE_X,
                GOAT_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_goat),
                GOAT_BODY_PROPORTION);
    }

    /**
     * Create a Googly Elk
     *
     * @return googly elk
     */
    public static GooglyPet createGooglyElk(Context context) {
        return new GooglyPet(
                R.drawable.elk,
                ELK_LEFT_EYE_X,
                ELK_LEFT_EYE_Y,
                ELK_RIGHT_EYE_X,
                ELK_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_elk),
                ELK_BODY_PROPORTION);
    }

    /**
     * Create a Googly Horse
     *
     * @return googly horse
     */
    public static GooglyPet createGooglyHorse(Context context) {
        return new GooglyPet(
                R.drawable.horse,
                HORSE_LEFT_EYE_X,
                HORSE_LEFT_EYE_Y,
                HORSE_RIGHT_EYE_X,
                HORSE_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_horse),
                HORSE_BODY_PROPORTION);
    }

    /**
     * Create a Googly pig
     *
     * @return googly pig
     */
    public static GooglyPet createGooglyPig(Context context) {
        return new GooglyPet(
                R.drawable.pig,
                PIG_LEFT_EYE_X,
                PIG_LEFT_EYE_Y,
                PIG_RIGHT_EYE_X,
                PIG_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_pig),
                PIG_BODY_PROPORTION);
    }

    /**
     * Create a Googly triceratops
     *
     * @return googly triceratops
     */
    public static GooglyPet createGooglyTriceratops(Context context) {
        return new GooglyPet(
                R.drawable.triceratops,
                TRICERATOPS_LEFT_EYE_X,
                TRICERATOPS_LEFT_EYE_Y,
                TRICERATOPS_RIGHT_EYE_X,
                TRICERATOPS_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_triceratops),
                TRICERATOPS_BODY_PROPORTION);
    }

    /**
     * Create a Googly tbarthel
     *
     * @return googly tbarthel
     */
    public static GooglyPet createGooglyTbarhel(Context context) {
        return new GooglyPet(
                R.drawable.tbarthel,
                TBARTHEL_LEFT_EYE_X,
                TBARTHEL_LEFT_EYE_Y,
                TBARTHEL_RIGHT_EYE_X,
                TBARTHEL_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_tbarthel),
                TBARTHEL_BODY_PROPORTION);
    }

    /**
     * Create a Googly vbarthel
     *
     * @return googly vbarthel
     */
    public static GooglyPet createGooglyVbarhel(Context context) {
        return new GooglyPet(
                R.drawable.vbarthel,
                VBARTHEL_LEFT_EYE_X,
                VBARTHEL_LEFT_EYE_Y,
                VBARTHEL_RIGHT_EYE_X,
                VBARTHEL_RIGHT_EYE_Y,
                context.getResources().getDimensionPixelSize(R.dimen.eye_diameter_vbarthel),
                VBARTHEL_BODY_PROPORTION);
    }

    //Non instantiable class.
    private GooglyPetFactory() {
    }
}
