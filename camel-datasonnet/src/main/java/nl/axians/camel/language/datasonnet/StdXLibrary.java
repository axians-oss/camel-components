package nl.axians.camel.language.datasonnet;

import com.datasonnet.header.Header;
import com.datasonnet.jsonnet.Val;
import com.datasonnet.spi.DataFormatService;
import com.datasonnet.spi.Library;

import java.util.*;

/**
 * This class is an extension of the standard Datasonnet library. It provides additional functions and modules that
 * can be used in Datasonnet scripts.
 */
public class StdXLibrary extends Library {

    private static final StdXLibrary INSTANCE = new StdXLibrary();

    /**
     * Private constructor to prevent instantiation of the StdXLibrary.
     */
    private StdXLibrary() {
    }

    /**
     * Returns the singleton instance of the StdXLibrary.
     *
     * @return The singleton instance of the StdXLibrary
     */
    public static StdXLibrary getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the namespace of the StdXLibrary.
     *
     * @return The namespace of the StdXLibrary.
     */
    @Override
    public String namespace() {
        return "stdx";
    }

    @Override
    public Map<String, Val.Func> functions(DataFormatService dataFormats, Header header) {
        final Map<String, Val.Func> functions = new HashMap<>();

        functions.put("parseLong", makeSimpleFunc(Collections.singletonList("addressLine"), args -> parseAddress(args.get(0))));

        return functions;
    }

    /**
     * Returns the set of additional modules that are provided by the StdXLibrary.
     *
     * @param dataFormats The supported data formats.
     * @param header      The header that is used for the transformation.
     * @return The set of additional modules that are provided by the StdXLibrary
     */
    @Override
    public Map<String, Val.Obj> modules(DataFormatService dataFormats, Header header) {
        return Collections.emptyMap();
    }

    /**
     * Returns the set of additional modules that are provided by the StdXLibrary.
     *
     * @return The set of additional modules that are provided by the StdXLibrary
     */
    @Override
    public Set<String> libsonnets() {
        return Collections.emptySet();
    }

    private Val parseAddress(Val addressLine) {
        return Val.Null$.MODULE$;
    }

}
