/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class Model extends AbstractNoraUiCli {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Model.class);
    
    public static final String UTILS = "utils";
    public static final String APPLICATION_MODEL = "application/model/";
    private static final String CONTEXT = "Context";

    private String mainPath;

    public Model() {
        this.mainPath = "src" + File.separator + "main";
    }

    protected Model(String mainPath) {
        this.mainPath = mainPath;
    }

    /**
     * @param applicationName
     *            name of application.
     * @param robotContext
     *            Context class from robot.
     * @return a list of available model (name).
     */
    public List<String> getModels(String applicationName, Class<?> robotContext) {
        List<String> models = new ArrayList<>();
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        String[] list = new File(modelPath).list();
        if (list != null) {
            models.addAll(Arrays.asList(list));
            for (int i = 0; i < models.size(); i++) {
                models.set(i, models.get(i).replaceAll(".java", "").toLowerCase());
            }
            for (int i = 0; i < models.size(); i++) {
                if (models.contains(models.get(i) + "s")) {
                    models.remove(models.get(i) + "s");
                }
            }
        }
        return models;
    }

    /**
     * @param robotContext
     *            Context class from robot.
     * @return a list of available application (name).
     */
    public List<String> getApplications(Class<?> robotContext) {
        List<String> applications = new ArrayList<>();
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        String[] apps = new File(modelPath.substring(0, modelPath.length() - 1)).list();
        if (apps != null) {
            applications.addAll(Arrays.asList(apps));
            TreeSet<String> hs = new TreeSet<>();
            hs.addAll(applications);
            applications.clear();
            applications.addAll(hs);
        }
        return applications;
    }

    /**
     * Add new model for a target application to your robot.
     * Sample if you add google: -f 5 -a google -m user -fi "field1 field2" -re "result1 result2"--verbose
     * 
     * @param applicationName
     *            name of application added.
     * @param modelName
     *            name of model added.
     * @param fields
     *            is fields of model (String separated by a space).
     * @param results
     *            is results of model (String separated by a space).
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    public void add(String applicationName, String modelName, String fields, String results, Class<?> robotContext, boolean verbose) {
        LOGGER.info("Add a new model named [{}] in application named [{}]", modelName, applicationName);
        String[] fieldList = fields.split(" ");
        for (String field : fieldList) {
            LOGGER.info("field: [{}]", field);
        }
        String[] resultList = new String[0];
        if (results != null) {
            resultList = results.split(" ");
            for (String result : resultList) {
                LOGGER.info("result: [{}]", result);
            }
        }
        addModel(applicationName, modelName, fieldList, resultList, robotContext, verbose);
        addModels(applicationName, modelName, robotContext, verbose);
    }

    /**
     * Remove model for a target application to your robot.
     * Sample if you add google: -f 6 -a google -m user --verbose
     * 
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    public void remove(String applicationName, String modelName, Class<?> robotContext, boolean verbose) {
        LOGGER.info("Remove model named [{}] in application named [{}]", modelName, applicationName);
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + ".java";
        String modelsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + "s.java";
        try {
            FileUtils.forceDelete(new File(modelPath));
            if (verbose) {
                LOGGER.info("{} removed with success.", modelPath);
            }
        } catch (IOException e) {
            LOGGER.debug("{} not revove because do not exist.", modelPath);
        }
        try {
            FileUtils.forceDelete(new File(modelsPath));
            if (verbose) {
                LOGGER.info("{} removed with success.", modelsPath);
            }
        } catch (IOException e) {
            LOGGER.debug("{} not revove because do not exist.", modelsPath);
        }
        String applicationDirectoryPath = modelPath.substring(0, modelPath.lastIndexOf(File.separator));
        try {
            Collection<File> l = FileUtils.listFiles(new File(applicationDirectoryPath), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            if (l.isEmpty()) {
                if (verbose) {
                    LOGGER.info("Empty directory, so remove application directory.");
                }
                FileUtils.deleteDirectory(new File(applicationDirectoryPath));
            }
        } catch (IOException e) {
            LOGGER.debug("{} not revove because do not exist.", applicationDirectoryPath);
        }

    }

    /**
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model added.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addModel(String applicationName, String modelName, String[] fieldList, String[] resultList, Class<?> robotContext, boolean verbose) {
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replace(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(robotContext.getSimpleName().replace(CONTEXT, ""))).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, "application.model." + applicationName) + ";").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import org.apache.commons.lang3.builder.EqualsBuilder;").append(System.lineSeparator());
        sb.append("import org.apache.commons.lang3.builder.HashCodeBuilder;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import com.google.common.collect.ComparisonChain;").append(System.lineSeparator());
        sb.append("import com.google.gson.Gson;").append(System.lineSeparator());
        sb.append("import com.google.gson.GsonBuilder;").append(System.lineSeparator());
        sb.append("import com.google.gson.annotations.Expose;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import com.github.noraui.annotation.Column;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.Model;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("public class " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " implements Model, Comparable<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "> {")
                .append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    @Expose(serialize = false, deserialize = false)").append(System.lineSeparator());
        sb.append("    private Integer nid;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("    @Expose").append(System.lineSeparator());
            sb.append("    @Column(name = \"" + field + "\")").append(System.lineSeparator());
            sb.append("    private String " + field + ";").append(System.lineSeparator());
            sb.append("").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append("    @Column(name = \"" + result + "\")").append(System.lineSeparator());
            sb.append("    private String " + result + ";").append(System.lineSeparator());
            sb.append("").append(System.lineSeparator());
        }
        sb.append("    // constructor by default for serialize/deserialize").append(System.lineSeparator());
        sb.append("    public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "() {").append(System.lineSeparator());
        sb.append("        this.nid = -1;").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("        this." + field + " = \"\";").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append("        this." + result + " = \"\";").append(System.lineSeparator());
        }
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "(String nid");
        for (String field : fieldList) {
            sb.append(", String " + field);
        }
        for (String result : resultList) {
            sb.append(", String " + result);
        }
        sb.append(") {").append(System.lineSeparator());
        sb.append("        this.nid = Integer.parseInt(nid);").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("        this." + field + " = " + field + ";").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append("        this." + result + " = " + result + ";").append(System.lineSeparator());
        }
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public String serialize() {").append(System.lineSeparator());
        sb.append("        final GsonBuilder builder = new GsonBuilder();").append(System.lineSeparator());
        sb.append("        builder.excludeFieldsWithoutExposeAnnotation();").append(System.lineSeparator());
        sb.append("        builder.disableHtmlEscaping();").append(System.lineSeparator());
        sb.append("        final Gson gson = builder.create();").append(System.lineSeparator());
        sb.append("        return gson.toJson(this);").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public void deserialize(String jsonString) {").append(System.lineSeparator());
        sb.append("        final GsonBuilder builder = new GsonBuilder();").append(System.lineSeparator());
        sb.append("        builder.excludeFieldsWithoutExposeAnnotation();").append(System.lineSeparator());
        sb.append("        final Gson gson = builder.create();").append(System.lineSeparator());
        sb.append("        " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " w = gson.fromJson(jsonString, " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ".class);")
                .append(System.lineSeparator());
        sb.append("        this.nid = w.nid;").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("        this." + field + " = w." + field + ";").append(System.lineSeparator());
        }
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public Class<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s> getModelList() {").append(System.lineSeparator());
        sb.append("        return " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s.class;").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public int hashCode() {").append(System.lineSeparator());
        sb.append("        return new HashCodeBuilder()");
        for (String field : fieldList) {
            sb.append(".append(" + field + ")");
        }
        sb.append(".toHashCode();").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public boolean equals(Object obj) {").append(System.lineSeparator());
        sb.append("        if (obj instanceof " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ") {").append(System.lineSeparator());
        sb.append("            final " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " other = (" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ") obj;")
                .append(System.lineSeparator());
        sb.append("            return new EqualsBuilder()").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("                    .append(" + field + ", other." + field + ")").append(System.lineSeparator());
        }
        sb.append("                    .isEquals();").append(System.lineSeparator());
        sb.append("        } else {").append(System.lineSeparator());
        sb.append("            return false;").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public int compareTo(" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " other) {").append(System.lineSeparator());

        sb.append("        return ComparisonChain.start()").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("                .compare(" + field + ", other." + field + ")").append(System.lineSeparator());
        }
        sb.append("        .result();").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public String toString() {").append(System.lineSeparator());
        sb.append("        return \"{nid:\" + nid + \"");
        for (String field : fieldList) {
            sb.append(", " + field + ":\\\"\" + " + field + " + \"\\\"");
        }
        for (String result : resultList) {
            sb.append(", " + result + ":\\\"\" + " + result + " + \"\\\"");
        }
        sb.append("}\";").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public Integer getNid() {").append(System.lineSeparator());
        sb.append("        return nid;").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public void setNid(Integer nid) {").append(System.lineSeparator());
        sb.append("        this.nid = nid;").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append("    public String get" + field.toUpperCase().charAt(0) + field.substring(1) + "() {").append(System.lineSeparator());
            sb.append("        return " + field + ";").append(System.lineSeparator());
            sb.append("    }").append(System.lineSeparator());
            sb.append("").append(System.lineSeparator());
            sb.append("    public void set" + field.toUpperCase().charAt(0) + field.substring(1) + "(String " + field + ") {").append(System.lineSeparator());
            sb.append("        this." + field + " = " + field + ";").append(System.lineSeparator());
            sb.append("    }").append(System.lineSeparator());
            sb.append("").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append("    public String get" + result.toUpperCase().charAt(0) + result.substring(1) + "() {").append(System.lineSeparator());
            sb.append("        return " + result + ";").append(System.lineSeparator());
            sb.append("    }").append(System.lineSeparator());
            sb.append("").append(System.lineSeparator());
            sb.append("    public void set" + result.toUpperCase().charAt(0) + result.substring(1) + "(String " + result + ") {").append(System.lineSeparator());
            sb.append("        this." + result + " = " + result + ";").append(System.lineSeparator());
            sb.append("    }").append(System.lineSeparator());
            sb.append("").append(System.lineSeparator());
        }
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(modelPath.substring(0, modelPath.lastIndexOf(File.separator))));
            File newSelector = new File(modelPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    LOGGER.info("File [{}] created with success.", modelPath);
                }
            } else {
                if (verbose) {
                    LOGGER.info("File [{}] already exist.", modelPath);
                }
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model added.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addModels(String applicationName, String modelName, Class<?> robotContext, boolean verbose) {
        String modelsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replace(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + "s.java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(robotContext.getSimpleName().replace(CONTEXT, ""))).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, "application.model." + applicationName) + ";").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import java.lang.reflect.Type;").append(System.lineSeparator());
        sb.append("import java.util.ArrayList;").append(System.lineSeparator());
        sb.append("import java.util.Iterator;").append(System.lineSeparator());
        sb.append("import java.util.List;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import com.google.gson.Gson;").append(System.lineSeparator());
        sb.append("import com.google.gson.GsonBuilder;").append(System.lineSeparator());
        sb.append("import com.google.gson.reflect.TypeToken;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import com.github.noraui.application.model.CommonModels;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.Model;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.ModelList;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("public class " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s extends CommonModels<" + modelName.toUpperCase().charAt(0) + modelName.substring(1)
                + "> implements ModelList {").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     *").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    private static final long serialVersionUID = 9002528163560746878L;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s() {").append(System.lineSeparator());
        sb.append("        super();").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s(CommonModels<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "> inputList) {")
                .append(System.lineSeparator());
        sb.append("        super(inputList);").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public void deserialize(String jsonString) {").append(System.lineSeparator());
        sb.append("            Type listType = new TypeToken<ArrayList<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ">>() {").append(System.lineSeparator());
        sb.append("            }.getType();").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("            final GsonBuilder builder = new GsonBuilder();").append(System.lineSeparator());
        sb.append("            builder.excludeFieldsWithoutExposeAnnotation();").append(System.lineSeparator());
        sb.append("             final Gson gson = builder.create();").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("            List<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "> list = gson.fromJson(jsonString, listType);").append(System.lineSeparator());
        sb.append("            this.addAll(list);").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("        /**").append(System.lineSeparator());
        sb.append("         * {@inheritDoc}").append(System.lineSeparator());
        sb.append("         */").append(System.lineSeparator());
        sb.append("        @Override").append(System.lineSeparator());
        sb.append("        public ModelList addModel(Model m) {").append(System.lineSeparator());
        sb.append("            super.add((" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ") m);").append(System.lineSeparator());
        sb.append("            return this;").append(System.lineSeparator());
        sb.append("       }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("        /**").append(System.lineSeparator());
        sb.append("        * {@inheritDoc}").append(System.lineSeparator());
        sb.append("        */").append(System.lineSeparator());
        sb.append("       @Override").append(System.lineSeparator());
        sb.append("       public void subtract(ModelList list) {").append(System.lineSeparator());
        sb.append("           Iterator<?> iterator = ((" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s) list).iterator();").append(System.lineSeparator());
        sb.append("          while (iterator.hasNext()) {").append(System.lineSeparator());
        sb.append("              this.remove(iterator.next());").append(System.lineSeparator());
        sb.append("          }").append(System.lineSeparator());
        sb.append("     }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public List<Integer> getIds() {").append(System.lineSeparator());
        sb.append("        List<Integer> result = new ArrayList<>();").append(System.lineSeparator());
        sb.append("        for (" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + " : this) {").append(System.lineSeparator());
        sb.append("            result.add(" + modelName + ".getNid());").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("        return result;").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(modelsPath.substring(0, modelsPath.lastIndexOf(File.separator))));
            File newSelector = new File(modelsPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    LOGGER.info("File [{}] created with success.", modelsPath);
                }
            } else {
                if (verbose) {
                    LOGGER.info("File [{}] already exist.", modelsPath);
                }
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

}
