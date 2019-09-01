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

    private static final String UTILS = "utils";
    private static final String APPLICATION_MODEL_SLASH = "application/model/";
    private static final String APPLICATION_MODEL_DOT = "application.model.";
    private static final String CONTEXT = "Context";
    private static final String TAB = "    ";
    private static final String TAB2 = TAB + TAB;
    private static final String PUBLIC_CLASS = "public class";
    private static final String NEW_GSON_BUILDER = TAB2 + "final GsonBuilder builder = new GsonBuilder();";
    private static final String JUNIT_TEST = TAB + "@Test";
    private static final String PREPARE_MOCK = TAB2 + "// prepare mock";
    private static final String RUN_TEST = TAB2 + "// run test";
    private static final String ASSERT_EQUALS = TAB2 + "Assert.assertEquals";
    private static final String LOG_FILE_CREATED_WITH_SUCCESS = "File [{}] created with success.";
    private static final String LOG_FILE_ALREADY_EXIST = "File [{}] already exist.";
    
    private String mainPath;
    private String testPath = "src" + File.separator + "test";

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
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        String[] list = new File(modelPath).list();
        if (list != null) {
            models.addAll(Arrays.asList(list));
            for (int i = 0; i < models.size(); i++) {
                models.set(i, models.get(i).replace(".java", "").toLowerCase());
            }
            for (int i = 0; i < models.size(); i++) {
                if (models.contains(models.get(i) + "s")) {
                    models.remove(models.get(i) + "s");
                }
                if (models.contains(models.get(i) + "ut")) {
                    models.remove(models.get(i) + "ut");
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
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH)
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
        addModelUT(applicationName, modelName, fieldList, resultList, robotContext, verbose);
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
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + ".java";
        String modelsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + "s.java";
        String modelTUPath = testPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + "UT.java";
        removeModelFile(verbose, modelPath);
        removeModelFile(verbose, modelsPath);
        removeApplicationDirectoryIfEmpty(verbose, modelPath.substring(0, modelPath.lastIndexOf(File.separator)));
        removeModelFile(verbose, modelTUPath);
        removeApplicationDirectoryIfEmpty(verbose, modelTUPath.substring(0, modelPath.lastIndexOf(File.separator)));

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
        String modelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replace(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(robotContext.getSimpleName().replace(CONTEXT, ""))).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, APPLICATION_MODEL_DOT + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import org.apache.commons.lang3.builder.EqualsBuilder;").append(System.lineSeparator());
        sb.append("import org.apache.commons.lang3.builder.HashCodeBuilder;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.google.common.collect.ComparisonChain;").append(System.lineSeparator());
        sb.append("import com.google.gson.Gson;").append(System.lineSeparator());
        sb.append("import com.google.gson.GsonBuilder;").append(System.lineSeparator());
        sb.append("import com.google.gson.annotations.Expose;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.annotation.Column;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.Model;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(PUBLIC_CLASS + " " +  modelName.toUpperCase().charAt(0) + modelName.substring(1) + " implements Model, Comparable<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "> {")
                .append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "@Expose(serialize = false, deserialize = false)").append(System.lineSeparator());
        sb.append(TAB + "private Integer nid;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append(TAB + "@Expose").append(System.lineSeparator());
            sb.append(TAB + "@Column(name = \"" + field + "\")").append(System.lineSeparator());
            sb.append(TAB + "private String " + field + ";").append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append(TAB + "@Column(name = \"" + result + "\")").append(System.lineSeparator());
            sb.append(TAB + "private String " + result + ";").append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        sb.append(TAB + "// constructor by default for serialize/deserialize").append(System.lineSeparator());
        sb.append(TAB + "public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "() {").append(System.lineSeparator());
        sb.append(TAB2 + "this.nid = -1;").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append(TAB2 + "this." + field + " = \"\";").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append(TAB2 + "this." + result + " = \"\";").append(System.lineSeparator());
        }
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "(String nid");
        for (String field : fieldList) {
            sb.append(", String " + field);
        }
        for (String result : resultList) {
            sb.append(", String " + result);
        }
        sb.append(") {").append(System.lineSeparator());
        sb.append(TAB2 + "this.nid = Integer.parseInt(nid);").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append(TAB2 + "this." + field + " = " + field + ";").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append(TAB2 + "this." + result + " = " + result + ";").append(System.lineSeparator());
        }
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public String serialize() {").append(System.lineSeparator());
        sb.append(NEW_GSON_BUILDER).append(System.lineSeparator());
        sb.append(TAB2 + "builder.excludeFieldsWithoutExposeAnnotation();").append(System.lineSeparator());
        sb.append(TAB2 + "builder.disableHtmlEscaping();").append(System.lineSeparator());
        sb.append(TAB2 + "final Gson gson = builder.create();").append(System.lineSeparator());
        sb.append(TAB2 + "return gson.toJson(this);").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public void deserialize(String jsonString) {").append(System.lineSeparator());
        sb.append(NEW_GSON_BUILDER).append(System.lineSeparator());
        sb.append(TAB2 + "builder.excludeFieldsWithoutExposeAnnotation();").append(System.lineSeparator());
        sb.append(TAB2 + "final Gson gson = builder.create();").append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " w = gson.fromJson(jsonString, " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ".class);")
                .append(System.lineSeparator());
        sb.append(TAB2 + "this.nid = w.nid;").append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append(TAB2 + "this." + field + " = w." + field + ";").append(System.lineSeparator());
        }
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public Class<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s> getModelList() {").append(System.lineSeparator());
        sb.append(TAB2 + "return " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s.class;").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public int hashCode() {").append(System.lineSeparator());
        sb.append(TAB2 + "return new HashCodeBuilder()");
        for (String field : fieldList) {
            sb.append(".append(" + field + ")");
        }
        sb.append(".toHashCode();").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public boolean equals(Object obj) {").append(System.lineSeparator());
        sb.append(TAB2 + "if (obj instanceof " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ") {").append(System.lineSeparator());
        sb.append(TAB2 + TAB + "final " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " other = (" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ") obj;")
                .append(System.lineSeparator());
        sb.append(TAB2 + TAB + "return new EqualsBuilder()");
        for (String field : fieldList) {
            sb.append(".append(" + field + ", other." + field + ")");
        }
        sb.append(".isEquals();").append(System.lineSeparator());
        sb.append(TAB2 + "} else {").append(System.lineSeparator());
        sb.append(TAB2 + TAB + "return false;").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public int compareTo(" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " other) {").append(System.lineSeparator());

        sb.append(TAB2 + "return ComparisonChain.start()");
        for (String field : fieldList) {
            sb.append(".compare(" + field + ", other." + field + ")");
        }
        sb.append(".result();").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public String toString() {").append(System.lineSeparator());
        sb.append(TAB2 + "return \"{nid:\" + nid + \"");
        for (String field : fieldList) {
            sb.append(", " + field + ":\\\"\" + " + field + " + \"\\\"");
        }
        for (String result : resultList) {
            sb.append(", " + result + ":\\\"\" + " + result + " + \"\\\"");
        }
        sb.append("}\";").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "public Integer getNid() {").append(System.lineSeparator());
        sb.append(TAB2 + "return nid;").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "public void setNid(Integer nid) {").append(System.lineSeparator());
        sb.append(TAB2 + "this.nid = nid;").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        for (String field : fieldList) {
            sb.append(TAB + "public String get" + field.toUpperCase().charAt(0) + field.substring(1) + "() {").append(System.lineSeparator());
            sb.append(TAB2 + "return " + field + ";").append(System.lineSeparator());
            sb.append(TAB2 + "}").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            sb.append(TAB + "public void set" + field.toUpperCase().charAt(0) + field.substring(1) + "(String " + field + ") {").append(System.lineSeparator());
            sb.append(TAB2 + "this." + field + " = " + field + ";").append(System.lineSeparator());
            sb.append(TAB2 + "}").append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append(TAB + "public String get" + result.toUpperCase().charAt(0) + result.substring(1) + "() {").append(System.lineSeparator());
            sb.append(TAB2 + "return " + result + ";").append(System.lineSeparator());
            sb.append(TAB2 + "}").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            sb.append(TAB + "public void set" + result.toUpperCase().charAt(0) + result.substring(1) + "(String " + result + ") {").append(System.lineSeparator());
            sb.append(TAB2 + "this." + result + " = " + result + ";").append(System.lineSeparator());
            sb.append(TAB2 + "}").append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(modelPath.substring(0, modelPath.lastIndexOf(File.separator))));
            File newSelector = new File(modelPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    LOGGER.info(LOG_FILE_CREATED_WITH_SUCCESS, modelPath);
                }
            } else {
                if (verbose) {
                    LOGGER.info(LOG_FILE_ALREADY_EXIST, modelPath);
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
        String modelsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replace(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + "s.java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(robotContext.getSimpleName().replace(CONTEXT, ""))).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, APPLICATION_MODEL_DOT + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import java.lang.reflect.Type;").append(System.lineSeparator());
        sb.append("import java.util.ArrayList;").append(System.lineSeparator());
        sb.append("import java.util.Iterator;").append(System.lineSeparator());
        sb.append("import java.util.List;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.google.gson.Gson;").append(System.lineSeparator());
        sb.append("import com.google.gson.GsonBuilder;").append(System.lineSeparator());
        sb.append("import com.google.gson.reflect.TypeToken;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.model.CommonModels;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.Model;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.ModelList;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(PUBLIC_CLASS + " " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s extends CommonModels<" + modelName.toUpperCase().charAt(0) + modelName.substring(1)
                + "> implements ModelList {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " *").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "private static final long serialVersionUID = 9002528163560746878L;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s() {").append(System.lineSeparator());
        sb.append(TAB2 + "super();").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "public " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s(CommonModels<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "> inputList) {")
                .append(System.lineSeparator());
        sb.append(TAB2 + "super(inputList);").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public void deserialize(String jsonString) {").append(System.lineSeparator());
        sb.append(TAB2 + "Type listType = new TypeToken<ArrayList<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ">>() {").append(System.lineSeparator());
        sb.append(TAB2 + "}.getType();").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NEW_GSON_BUILDER).append(System.lineSeparator());
        sb.append(TAB2 + "builder.excludeFieldsWithoutExposeAnnotation();").append(System.lineSeparator());
        sb.append(TAB2 + "final Gson gson = builder.create();").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB2 + "List<" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "> list = gson.fromJson(jsonString, listType);").append(System.lineSeparator());
        sb.append(TAB2 + "this.addAll(list);").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public ModelList addModel(Model m) {").append(System.lineSeparator());
        sb.append(TAB2 + "super.add((" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + ") m);").append(System.lineSeparator());
        sb.append(TAB2 + "return this;").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public void subtract(ModelList list) {").append(System.lineSeparator());
        sb.append(TAB2 + "Iterator<?> iterator = ((" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s) list).iterator();").append(System.lineSeparator());
        sb.append(TAB2 + "while (iterator.hasNext()) {").append(System.lineSeparator());
        sb.append(TAB2 + TAB + "this.remove(iterator.next());").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB + "/**").append(System.lineSeparator());
        sb.append(TAB + " * {@inheritDoc}").append(System.lineSeparator());
        sb.append(TAB + " */").append(System.lineSeparator());
        sb.append(TAB + "@Override").append(System.lineSeparator());
        sb.append(TAB + "public List<Integer> getIds() {").append(System.lineSeparator());
        sb.append(TAB2 + "List<Integer> result = new ArrayList<>();").append(System.lineSeparator());
        sb.append(TAB2 + "for (" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + " : this) {").append(System.lineSeparator());
        sb.append(TAB2 + TAB + "result.add(" + modelName + ".getNid());").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(TAB2 + "return result;").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(modelsPath.substring(0, modelsPath.lastIndexOf(File.separator))));
            File newSelector = new File(modelsPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    LOGGER.info(LOG_FILE_CREATED_WITH_SUCCESS, modelsPath);
                }
            } else {
                if (verbose) {
                    LOGGER.info(LOG_FILE_ALREADY_EXIST, modelsPath);
                }
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    private void addModelUT(String applicationName, String modelName, String[] fieldList, String[] resultList, Class<?> robotContext, boolean verbose) {
        String modelPath = testPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace(UTILS, APPLICATION_MODEL_SLASH + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replace(robotContext.getSimpleName(), modelName.toUpperCase().charAt(0) + modelName.substring(1)) + "UT.java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(robotContext.getSimpleName().replace(CONTEXT, ""))).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, APPLICATION_MODEL_DOT + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import java.util.Collections;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import org.junit.Assert;").append(System.lineSeparator());
        sb.append("import org.junit.Test;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, APPLICATION_MODEL_DOT + applicationName).replace("package ", "import ") + "." + modelName.toUpperCase().charAt(0)
                + modelName.substring(1) + ";").append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace(UTILS, APPLICATION_MODEL_DOT + applicationName).replace("package ", "import ") + "." + modelName.toUpperCase().charAt(0)
                + modelName.substring(1) + "s;").append(System.lineSeparator());
        sb.append("import com.github.noraui.model.ModelList;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(PUBLIC_CLASS + " " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "UT {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(JUNIT_TEST).append(System.lineSeparator());
        sb.append(TAB + "public void check" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "SerializeTest() {").append(System.lineSeparator());
        sb.append(PREPARE_MOCK).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + " = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        int i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append(TAB2 + modelName + ".set" + field.toUpperCase().charAt(0) + field.substring(1) + "(\"" + i + "\");").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"{");
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\", " + modelName + ".serialize());").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(JUNIT_TEST).append(System.lineSeparator());
        sb.append(TAB + "public void check" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "SerializeAllTest() {").append(System.lineSeparator());
        sb.append(TAB + "   // prepare mock").append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + " = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + ".setNid(123);").append(System.lineSeparator());
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append(TAB2 + modelName + ".set" + field.toUpperCase().charAt(0) + field.substring(1) + "(\"" + i + "\");").append(System.lineSeparator());
        }
        for (String result : resultList) {
            i++;
            sb.append(TAB2 + modelName + ".set" + result.toUpperCase().charAt(0) + result.substring(1) + "(\"" + i + "\");").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"{");
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\", " + modelName + ".serialize());").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(JUNIT_TEST).append(System.lineSeparator());
        sb.append(TAB + "public void check" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "DeserializeTest() {").append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + " = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + ".deserialize(\"{");
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\");").append(System.lineSeparator());
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append(ASSERT_EQUALS + "(\"" + i + "\", " + modelName + ".get" + field.toUpperCase().charAt(0) + field.substring(1) + "());").append(System.lineSeparator());
        }

        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(JUNIT_TEST).append(System.lineSeparator());
        sb.append(TAB + "public void check" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "DeserializeAllTest() {").append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + " = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + ".deserialize(\"{nid:123,");
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        for (String result : resultList) {
            i++;
            sb.append("\\\"" + result + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\");").append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(new Integer(-1), " + modelName + ".getNid());").append(System.lineSeparator());
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append(ASSERT_EQUALS + "(\"" + i + "\", " + modelName + ".get" + field.toUpperCase().charAt(0) + field.substring(1) + "());").append(System.lineSeparator());
        }
        for (String result : resultList) {
            sb.append(ASSERT_EQUALS + "(\"\", " + modelName + ".get" + result.toUpperCase().charAt(0) + result.substring(1) + "());").append(System.lineSeparator());
        }
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append(JUNIT_TEST).append(System.lineSeparator());
        sb.append(TAB + "public void check" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "SerializeListTest() {").append(System.lineSeparator());
        sb.append(PREPARE_MOCK).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + "1 = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append(TAB2 + modelName + "1.set" + field.toUpperCase().charAt(0) + field.substring(1) + "(\"" + i + "\");").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " " + modelName + "2 = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        for (String field : fieldList) {
            i++;
            sb.append(TAB2 + modelName + "2.set" + field.toUpperCase().charAt(0) + field.substring(1) + "(\"" + i + "\");").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s " + modelName + "s = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + "s.add(" + modelName + "1);").append(System.lineSeparator());
        sb.append(TAB2 + modelName + "s.add(" + modelName + "2);").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"[{");
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("},{");
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}]\", " + modelName + "s.serialize());").append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("@Test").append(System.lineSeparator());
        sb.append(TAB + "public void check" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "DeserializeListTest() {").append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s " + modelName + "s = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + "s.deserialize(\"[{");
        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("},{");
        for (String field : fieldList) {
            i++;
            sb.append("\\\"" + field + "\\\":\\\"" + i + "\\\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}]\");").append(System.lineSeparator());

        i = 4000;
        for (String field : fieldList) {
            i++;
            sb.append(ASSERT_EQUALS + "(\"" + i + "\", " + modelName + "s.get(0).get" + field.toUpperCase().charAt(0) + field.substring(1) + "());").append(System.lineSeparator());
        }
        for (String field : fieldList) {
            i++;
            sb.append(ASSERT_EQUALS + "(\"" + i + "\", " + modelName + "s.get(1).get" + field.toUpperCase().charAt(0) + field.substring(1) + "());").append(System.lineSeparator());
        }
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(JUNIT_TEST).append(System.lineSeparator());
        sb.append(TAB + "public void checkDelete" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "sAndAdd" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "sTest() {")
                .append(System.lineSeparator());
        sb.append(PREPARE_MOCK).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " a = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + "a.set" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "(\"aaaa\");").append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " b = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + "b.set" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "(\"cccc\");").append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " c = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + "c.set" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "(\"bbbb\");").append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + " d = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "();")
                .append(System.lineSeparator());
        sb.append(TAB2 + "d.set" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "(\"eeee\");").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s " + modelName + "sInGame = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + "sInGame.add(a);").append(System.lineSeparator());
        sb.append(TAB2 + modelName + "sInGame.add(b);").append(System.lineSeparator());
        sb.append(TAB2 + modelName + "sInGame.add(c);").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s " + modelName + "s = new " + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s();")
                .append(System.lineSeparator());
        sb.append(TAB2 + modelName + "s.add(b);").append(System.lineSeparator());
        sb.append(TAB2 + modelName + "s.add(c);").append(System.lineSeparator());
        sb.append(TAB2 + modelName + "s.add(d);").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(RUN_TEST).append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s mInGame = (" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s) " + modelName
                + "sInGame.clone();").append(System.lineSeparator());
        sb.append(TAB2 + "mInGame.subtract(" + modelName + "s);").append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(1, mInGame.size());").append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"aaaa\", mInGame.get(0).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB2 + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s l = (" + modelName.toUpperCase().charAt(0) + modelName.substring(1) + "s) " + modelName + "s.clone();")
                .append(System.lineSeparator());
        sb.append(TAB2 + "l.subtract(" + modelName + "sInGame);").append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(1, l.size());").append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"eeee\", l.get(0).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"aaaa\", " + modelName + "sInGame.get(0).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());")
                .append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"cccc\", " + modelName + "sInGame.get(1).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());")
                .append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"bbbb\", " + modelName + "sInGame.get(2).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());")
                .append(System.lineSeparator());
        sb.append(TAB2 + "Collections.sort(" + modelName + "sInGame);").append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"aaaa\", " + modelName + "sInGame.get(0).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());")
                .append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"bbbb\", " + modelName + "sInGame.get(1).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());")
                .append(System.lineSeparator());
        sb.append(ASSERT_EQUALS + "(\"cccc\", " + modelName + "sInGame.get(2).get" + fieldList[0].toUpperCase().charAt(0) + fieldList[0].substring(1) + "());")
                .append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(TAB2 + "}").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(modelPath.substring(0, modelPath.lastIndexOf(File.separator))));
            File newSelector = new File(modelPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    LOGGER.info(LOG_FILE_CREATED_WITH_SUCCESS, modelPath);
                }
            } else {
                if (verbose) {
                    LOGGER.info(LOG_FILE_ALREADY_EXIST, modelPath);
                }
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param modelPath
     *            name of model removed.
     */
    private void removeModelFile(boolean verbose, String modelPath) {
        try {
            FileUtils.forceDelete(new File(modelPath));
            if (verbose) {
                LOGGER.info("{} removed with success.", modelPath);
            }
        } catch (IOException e) {
            LOGGER.debug("{} not revove because do not exist.", modelPath);
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param applicationDirectoryPath
     *            path of application directory (src or test).
     */
    private void removeApplicationDirectoryIfEmpty(boolean verbose, String applicationDirectoryPath) {
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

}
