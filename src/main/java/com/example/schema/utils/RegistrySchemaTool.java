package com.example.schema.utils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.avro.Schema;
import com.example.avro.schema.provider.RegistrySchemaProvider;
import com.example.schema.domain.VersionedSchema;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RegistrySchemaTool {

    public static final String ADD = "add";
    public static final String LIST = "list";
    public static final String DESCRIBE = "describe";
    public static final String SCHEMA_FILE = "schema-file";
    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String ID = "id";
    private final RegistrySchemaProvider provider;

    public RegistrySchemaTool(RegistrySchemaProvider provider) {
        this.provider = provider;
    }

    public Collection<VersionedSchema> listSchemas() {
        return provider.getAllSchemas();
    }

    public VersionedSchema getSchema(String name, int version) {
        return provider.get(name, version);
    }

    public VersionedSchema getSchema(int id) {
        return provider.get(id);
    }

    public VersionedSchema addSchema(String name, int version, String schemaFileName) throws Exception {
        String avroSchema = new String(Files.readAllBytes(Paths.get(schemaFileName)));
        VersionedSchema versionedSchema = new VersionedSchema(0, name, version,
                new Schema.Parser().parse(avroSchema));
        provider.add(versionedSchema);
        return versionedSchema;
    }

    public static void main(String... args) throws IOException {
    	
        OptionParser parser = new OptionParser();
        parser.accepts(ADD);
        parser.accepts(LIST);
        parser.accepts(DESCRIBE);

        parser.accepts(SCHEMA_FILE).withRequiredArg();
        parser.accepts(NAME).withRequiredArg();
        parser.accepts(VERSION).withRequiredArg();
        parser.accepts(ID).withRequiredArg();

        parser.accepts(RegistrySchemaProvider.ENDPOINT_CONF).withRequiredArg();

        try {


            OptionSet options = parser.parse(args);

            if (options.has(ADD)) {
                if (options.has(LIST) || options.has(DESCRIBE)) {
                    throw new IllegalArgumentException("You must specify exactly one of --"+ADD+", --"+LIST+" or --"+DESCRIBE+"");
                }
                if (!(options.has(SCHEMA_FILE) && options.has(NAME) && options.has(VERSION))) {
                    throw new IllegalStateException("You must specify all of --" + SCHEMA_FILE + ", --" + NAME + ", --" + VERSION + " when adding new schema.");
                }
                if (options.has(ID)) {
                    throw new IllegalStateException("You must not specify --" + ID + " when adding new schema.");
                }
                RegistrySchemaTool tool = new RegistrySchemaTool(createProvider(options));
                VersionedSchema schema = tool.addSchema(options.valueOf(NAME).toString(), Integer.valueOf(options.valueOf(VERSION).toString()), options.valueOf(SCHEMA_FILE).toString());
                System.out.println("Successfully added schema.");
                printSchema(schema);
            } else if (options.has(LIST)) {
                if (options.has(DESCRIBE)) {
                    throw new IllegalArgumentException("You must specify exactly one of --"+ADD+", --"+LIST+" or --"+DESCRIBE+"");
                }
                if (options.has(SCHEMA_FILE)) {
                    throw new IllegalStateException("You must not specify --" + SCHEMA_FILE + " when listing schemas.");
                }
                RegistrySchemaTool tool = new RegistrySchemaTool(createProvider(options));
                Stream<VersionedSchema> schemas = tool.listSchemas().stream();
                if (options.hasArgument(NAME)) {
                    schemas = schemas.filter(schema -> options.valueOf(NAME).equals(schema.getName()));
                }
                if (options.hasArgument(VERSION)) {
                    schemas = schemas.filter(schema -> options.valueOf(VERSION).equals(String.valueOf(schema.getVersion())));
                }
                if (options.hasArgument(ID)) {
                    schemas = schemas.filter(schema -> options.valueOf(ID).equals(String.valueOf(schema.getId())));
                }
                schemas.forEach(RegistrySchemaTool::printSchema);
            }  else if (options.has(DESCRIBE)) {
                if (options.has(SCHEMA_FILE)) {
                    throw new IllegalStateException("You must not specify --" + SCHEMA_FILE + " when describing a schema.");
                }
                if (!options.has(ID) && !(options.has(NAME) && options.has(VERSION)) ) {
                    throw new IllegalStateException("You must either specify --" + ID + " or --" + NAME + " and --" + VERSION + ".");

                }
                RegistrySchemaTool tool = new RegistrySchemaTool(createProvider(options));
                VersionedSchema schema = options.has(ID)
                        ? tool.getSchema(Integer.valueOf(options.valueOf(ID).toString()))
                        : tool.getSchema(options.valueOf(NAME).toString(), Integer.valueOf(options.valueOf(VERSION).toString()));
                printSchema(schema);
                System.out.println("Avro schema:");
                System.out.println(schema.getSchema().toString(true));
            }
        } catch (Exception e) {
            e.printStackTrace();
            parser.printHelpOn(System.err);
        }

    }

    private static void printSchema(VersionedSchema schema) {
        System.out.println("ID: " + schema.getId() + "\tName: " + schema.getName() + "\tVersion: " + schema.getVersion());
    }

    private static RegistrySchemaProvider createProvider(OptionSet options) throws Exception {
        Properties p = new Properties();
        if (options.has(RegistrySchemaProvider.ENDPOINT_CONF)) {
        	p.put(RegistrySchemaProvider.ENDPOINT_CONF, options.valueOf(RegistrySchemaProvider.ENDPOINT_CONF));
        }

        return (RegistrySchemaProvider) new RegistrySchemaProvider.RegistrySchemaProviderFactory().getProvider(cast(p));

    }

    private static Map<String, Object> cast(Object o) {
        return (Map<String, Object>) o;
    }


}
