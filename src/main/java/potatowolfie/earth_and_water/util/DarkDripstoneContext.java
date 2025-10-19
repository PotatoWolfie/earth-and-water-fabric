package potatowolfie.earth_and_water.util;

public class DarkDripstoneContext {
    private static final ThreadLocal<Boolean> IS_DARK_DRIPSTONE = ThreadLocal.withInitial(() -> false);

    public static void setDarkDripstoneMode(boolean isDark) {
        IS_DARK_DRIPSTONE.set(isDark);
    }

    public static boolean isDarkDripstoneMode() {
        return IS_DARK_DRIPSTONE.get();
    }

    public static void clearDarkDripstoneMode() {
        IS_DARK_DRIPSTONE.remove();
    }
}