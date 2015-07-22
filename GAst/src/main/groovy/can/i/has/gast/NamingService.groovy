package can.i.has.gast

/**
 * Service providing unique names for auxiliary entities.
 *
 * When you're adding method or creating new auxiliary class you may need some "ugly" name for that, for example
 * <pre>foo$original</pre> as name of original method wrapped by your transformation.
 * This class provides this possibility, keeping count of obtained auxiliary names.
 * That count is kept as int, so names will be unique and valid as long as you won't pass Integer.MAX_INT names
 * for the same target and modification (see getNewNumberedName(String, String) javadoc).
 */
final class NamingService {
    private static Map<String, Map<String, Integer>> namesIndexes = [:].withDefault {
        [:].withDefault {
            0
        }.asSynchronized()
    }.asSynchronized()

    /**
     * Obtain new auxiliary name that will not be used again in this process.
     * @param target Description of modified entity, e.g. qualified class name or simple method name
     * @param modification Description of reason for getting this name, like "original", "wrapped", etc
     * @return New name composed as '<target>$<modification><uniqueInteger>'
     */
    static String getNewNumberedName(String target, String modification){
        "$target\$$modification${namesIndexes[target][modification]++}"
    }
}
